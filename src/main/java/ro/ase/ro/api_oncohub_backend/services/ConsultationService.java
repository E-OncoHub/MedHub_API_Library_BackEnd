package ro.ase.ro.api_oncohub_backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationRequestDto;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationResponsetDto;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.GetConsultationByClientDto;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.EsmoResult;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.TreatmentItemDto;
import ro.ase.ro.api_oncohub_backend.exceptions.ConsultationAlreadyViewedException;
import ro.ase.ro.api_oncohub_backend.exceptions.ConsultationNotFoundException;
import ro.ase.ro.api_oncohub_backend.exceptions.InvalidConsultationDataException;
import ro.ase.ro.api_oncohub_backend.models.Consultation;
import ro.ase.ro.api_oncohub_backend.models.ConsultationAccessManager;
import ro.ase.ro.api_oncohub_backend.repositories.ConsultationAccessManagerRepository;
import ro.ase.ro.api_oncohub_backend.repositories.ConsultationRepository;
import ro.ase.ro.api_oncohub_backend.services.engines.EsmoDecisionEngine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultationService {
    //    public final static String WEB_DOMAIN_NAME = "https://oncohub.com/medhub/breastCancer/";
    public final static String WEB_DOMAIN_NAME = "https://ashy-glacier-07e6ac203.6.azurestaticapps.net/";

    private final ConsultationRepository consultationRepository;
    private final ConsultationAccessManagerRepository consultationAccessManagerRepository;

    private final EsmoDecisionEngine esmoDecisionEngine;

    private final ObjectMapper objectMapper;

    public GetConsultationByClientDto getNonAccessedConsultationById(UUID uuid) throws ConsultationNotFoundException {
        ConsultationAccessManager consultationAccessManager = consultationAccessManagerRepository.findByIdAndIsViewedIsFalse(uuid)
                .orElseThrow(() -> {
                    if (consultationAccessManagerRepository.existsById(uuid)) {
                        return new ConsultationAlreadyViewedException("This consultation has already been viewed.");
                    } else {
                        return new ConsultationNotFoundException("Consultation not found");
                    }
                });

        // update the consultation access manager to mark it as viewed


        Consultation consultation = consultationAccessManager.getConsultation();

        List<TreatmentItemDto> firstLine;
        List<TreatmentItemDto> secondLine;
        List<TreatmentItemDto> thirdLine;

        try {
            firstLine = objectMapper.readValue(
                    consultation.getFirstLineTreatment(),
                    new TypeReference<>() {
                    }
            );

            secondLine = objectMapper.readValue(
                    consultation.getSecondLineTreatment(),
                    new TypeReference<>() {
                    }
            );

            thirdLine = objectMapper.readValue(
                    consultation.getThirdLineTreatment(),
                    new TypeReference<>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse treatment data", e);
        }

        consultationAccessManager.setIsViewed(true);
        consultationAccessManager.setViewedAt(LocalDateTime.now());
        consultationAccessManagerRepository.save(consultationAccessManager);

        return new GetConsultationByClientDto(
                consultation.getProtocolCarcMamInvaz(),
                consultation.getEr(),
                consultation.getPr(),
                consultation.getHer2(),
                consultation.getTnm(),
                consultation.getHistologicType(),
                consultation.getHistologicGrade(),
                consultation.getCarcinomaInSitu(),
                consultation.getNuclearHistologicGrade(),
                consultation.getKi67(),
                consultation.getDiagnostic(),
                firstLine,
                secondLine,
                thirdLine
        );
    }

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

        EsmoResult esmo = esmoDecisionEngine.evaluate(consultation);
        consultation.setDiagnostic(esmo.diagnostic());

        try {
            String firstLineJson = objectMapper.writeValueAsString(esmo.firstLine());
            String secondLineJson = objectMapper.writeValueAsString(esmo.secondLine());
            String thirdLineJson = objectMapper.writeValueAsString(esmo.thirdLine());

            consultation.setFirstLineTreatment(firstLineJson);
            consultation.setSecondLineTreatment(secondLineJson);
            consultation.setThirdLineTreatment(thirdLineJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize treatment plans", e);
        }

        Consultation saved = consultationRepository.save(consultation);

        ConsultationAccessManager consultationAccessManager = new ConsultationAccessManager();
        consultationAccessManager.setConsultation(saved);
        consultationAccessManager.setIsViewed(false);
        ConsultationAccessManager savedConsultationAccessManager = consultationAccessManagerRepository.save(consultationAccessManager);

        return new CreateConsultationResponsetDto(
                savedConsultationAccessManager.getId(),
                WEB_DOMAIN_NAME + savedConsultationAccessManager.getId(),
                saved.getDiagnostic(),
                esmo.firstLine(),
                esmo.secondLine(),
                esmo.thirdLine()
        );
    }
}
