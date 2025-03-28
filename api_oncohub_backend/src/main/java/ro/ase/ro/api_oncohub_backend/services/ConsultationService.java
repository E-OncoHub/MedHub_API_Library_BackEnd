package ro.ase.ro.api_oncohub_backend.services;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationRequestDto;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationResponsetDto;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.EsmoResult;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.TreatmentItemDto;
import ro.ase.ro.api_oncohub_backend.exceptions.InvalidConsultationDataException;
import ro.ase.ro.api_oncohub_backend.models.Consultation;
import ro.ase.ro.api_oncohub_backend.models.ConsultationAccessManager;
import ro.ase.ro.api_oncohub_backend.repositories.ConsultationAccessManagerRepository;
import ro.ase.ro.api_oncohub_backend.repositories.ConsultationRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationService {
    public final static String WEB_DOMAIN_NAME = "https://oncohub.com/medhub/breastCancer/";

    private final ConsultationRepository consultationRepository;
    private final ConsultationAccessManagerRepository consultationAccessManagerRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public CreateConsultationResponsetDto createConsultation(CreateConsultationRequestDto createConsultationRequestDto) {
        if (createConsultationRequestDto.er() == null
                || createConsultationRequestDto.pr() == null
                || createConsultationRequestDto.ki67() == null
                || createConsultationRequestDto.her2() == null) {
            throw new InvalidConsultationDataException("Invalid biomarkers: ER, PR, HER2, KI67");
        }

        if (createConsultationRequestDto.tnm() == null) {
            throw new InvalidConsultationDataException("Indicator not found: TNM");
        }

        Consultation consultation = new Consultation();
        consultation.setProtocolCarcMamInvaz(createConsultationRequestDto.protocolCarcMamInvaz());
        consultation.setEr(createConsultationRequestDto.er());
        consultation.setPr(createConsultationRequestDto.pr());
        consultation.setHer2(createConsultationRequestDto.her2());
        consultation.setTnm(createConsultationRequestDto.tnm());
        consultation.setHistologicType(createConsultationRequestDto.histologicType());
        consultation.setHistologicGrade(createConsultationRequestDto.histologicGrade());
        consultation.setCarcinomaInSitu(createConsultationRequestDto.carcinomaInSitu());
        consultation.setNuclearHistologicGrade(createConsultationRequestDto.nuclearHistologicGrade());
        consultation.setKi67(createConsultationRequestDto.ki67());

        // Temporary information
        EsmoResult esmo = getEsmoResult(consultation);
        consultation.setDiagnostic(esmo.diagnostic());

        try {
            String firstLineJson = objectMapper.writeValueAsString(esmo.firstLine());
            String secondLineJson = objectMapper.writeValueAsString(esmo.secondLine());

            consultation.setFirstLineTreatment(firstLineJson);
            consultation.setSecondLineTreatment(secondLineJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize treatment plans", e);
        }

        Consultation saved = consultationRepository.save(consultation);

        ConsultationAccessManager consultationAccessManager = new ConsultationAccessManager();
        consultationAccessManager.setConsultation(consultation);
        consultationAccessManager.setIsViewed(false);
        ConsultationAccessManager savedConsultationAccessManager = consultationAccessManagerRepository.save(consultationAccessManager);

        return new CreateConsultationResponsetDto(
                savedConsultationAccessManager.getId(),
                WEB_DOMAIN_NAME + savedConsultationAccessManager.getId(),
                saved.getDiagnostic(),
                esmo.firstLine(),
                esmo.secondLine()
        );
    }

    protected EsmoResult getEsmoResult(Consultation consultation) {
        String diagnostic = getEsmoLuminal(consultation);

        List<TreatmentItemDto> firstLine;
        List<TreatmentItemDto> secondLine;

        if (consultation.getTnm() != null && consultation.getTnm().contains("M1")) {
            firstLine = getEsmoAdvancedRecommendation(diagnostic, consultation);
            secondLine = getEsmoAdvancedProgression(diagnostic, consultation);
        } else {
            firstLine = getEsmoEarlyRecommendation(diagnostic, consultation);
            secondLine = List.of(); //TODO: ESMO Early logic
        }

        return new EsmoResult(diagnostic, firstLine, secondLine);
    }

    protected String getEsmoLuminal(Consultation consultation) {
        final int er = safe(consultation.getEr());
        final int pr = safe(consultation.getPr());
        final int her2 = safe(consultation.getHer2());
        final int ki67 = safe(consultation.getKi67());

        if (er >= 1 && pr >= 1 && her2 < 10) {
            return ki67 <= 14 ? "Luminal A" : "Luminal B HER2 negative";
        } else if (er >= 1 && her2 >= 10) {
            return "Luminal B HER2 positive";
        } else if (er < 1 && pr < 1 && her2 < 10) {
            return "Triple Negative Breast Cancer (TNBC)";
        } else {
            return "Unknown Diagnostic";
        }
    }

    private int safe(Integer val) {
        return val == null ? 0 : val;
    }

    private List<TreatmentItemDto> getEsmoEarlyRecommendation(String diagnostic, Consultation c) {
        return switch (diagnostic) {
            case "Luminal A", "Luminal B HER2 negative" -> List.of(
                    new TreatmentItemDto("(N/A) ET + CDK4/6 inhibitor", "Standard for early luminal disease", null)
            );
            case "Triple Negative Breast Cancer (TNBC)" -> List.of(
                    new TreatmentItemDto("(N/A) Anthracycline + Taxane", "Standard chemotherapy regimen", null)
            );
            default -> List.of(new TreatmentItemDto("(N/A) No early-stage guidance", "Refer to specialist", null));
        };
    }

    private List<TreatmentItemDto> getEsmoAdvancedRecommendation(String diagnostic, Consultation c) {
        return switch (diagnostic) {
            case "Luminal A", "Luminal B HER2 negative" -> List.of(
                    new TreatmentItemDto("ET-CDK 4/6 Inhibitor", "Recommended for initial treatment.", null),
                    new TreatmentItemDto("Organ Failure (Any stage)", "Chemotherapy required.", null)
            );
            case "Luminal B HER2 positive" -> List.of(
                    new TreatmentItemDto("Trastuzumab + (Pertuzumab)", "With or without chemo", null),
                    new TreatmentItemDto("Docetaxel / Paclitaxel + Trastuzumab", "Standard in eligible patients", null)
            );
            case "Triple Negative Breast Cancer (TNBC)" -> List.of(
                    new TreatmentItemDto("PD-L1+", "Atezolizumab + Nabpaclitaxel", null),
                    new TreatmentItemDto("gBRCAm", "Platinum chemo or PARP inhibitor", null)
            );
            default -> List.of(new TreatmentItemDto("No specific guidelines", "Refer to specialist", null));
        };
    }

    private List<TreatmentItemDto> getEsmoAdvancedProgression(String diagnostic, Consultation c) {
        return switch (diagnostic) {
            case "Luminal A", "Luminal B HER2 negative" -> List.of(
                    new TreatmentItemDto("Stop CDK4/6", "Switch to Everolimus + Exemestane", null),
                    new TreatmentItemDto("Progression", "Switch to chemotherapy", null)
            );
            case "Luminal B HER2 positive" -> List.of(
                    new TreatmentItemDto("Progression", "T-DM1 or Tucatinib-based regimens", null)
            );
            case "Triple Negative Breast Cancer (TNBC)" -> List.of(
                    new TreatmentItemDto("Progression", "Sacituzumab govitecan", null)
            );
            default -> List.of(new TreatmentItemDto("No data", "Custom evaluation needed", null));
        };
    }
}
