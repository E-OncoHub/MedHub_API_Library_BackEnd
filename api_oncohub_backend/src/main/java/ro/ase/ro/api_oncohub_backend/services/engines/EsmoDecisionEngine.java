package ro.ase.ro.api_oncohub_backend.services.engines;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.EsmoResult;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.TreatmentItemDto;
import ro.ase.ro.api_oncohub_backend.models.Consultation;

import java.util.List;

@Service
@Scope("singleton")
@RequiredArgsConstructor
public class EsmoDecisionEngine {

    public EsmoResult evaluate(Consultation consultation) {
        String diagnostic = determineDiagnostic(consultation);

        List<TreatmentItemDto> firstLine;
        List<TreatmentItemDto> secondLine;

        if (consultation.getTnm() != null && consultation.getTnm().contains("M1")) {
            firstLine = getAdvancedRecommendations(diagnostic);
            secondLine = getProgressionRecommendations(diagnostic);
        } else {
            firstLine = getEarlyRecommendations(diagnostic);
            secondLine = List.of(); //TODO: ESMO Early-stage progression not implemented yet
        }

        return new EsmoResult(diagnostic, firstLine, secondLine);
    }

    public String determineDiagnostic(Consultation c) {
        final int er = safe(c.getEr());
        final int pr = safe(c.getPr());
        final int her2 = safe(c.getHer2());
        final int ki67 = safe(c.getKi67());

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

    private List<TreatmentItemDto> getEarlyRecommendations(String diagnostic) {
        return switch (diagnostic) {
            case "Luminal A", "Luminal B HER2 negative" -> List.of(
                    new TreatmentItemDto("ET + CDK4/6 inhibitor", "Standard for early luminal disease", null)
            );
            case "Triple Negative Breast Cancer (TNBC)" -> List.of(
                    new TreatmentItemDto("Anthracycline + Taxane", "Standard chemotherapy regimen", null)
            );
            default -> List.of(
                    new TreatmentItemDto("No early-stage guidance", "Refer to specialist", null)
            );
        };
    }

    private List<TreatmentItemDto> getAdvancedRecommendations(String diagnostic) {
        return switch (diagnostic) {
            case "Luminal A", "Luminal B HER2 negative" -> List.of(
                    new TreatmentItemDto("ET-CDK 4/6 Inhibitor", "Recommended for initial treatment.", null),
                    new TreatmentItemDto("Organ Failure", "Chemotherapy required.", null)
            );
            case "Luminal B HER2 positive" -> List.of(
                    new TreatmentItemDto("Trastuzumab + (Pertuzumab)", "With or without chemo", null),
                    new TreatmentItemDto("Docetaxel / Paclitaxel + Trastuzumab", "Standard in eligible patients", null)
            );
            case "Triple Negative Breast Cancer (TNBC)" -> List.of(
                    new TreatmentItemDto("PD-L1+", "Atezolizumab + Nabpaclitaxel", null),
                    new TreatmentItemDto("gBRCAm", "Platinum chemo or PARP inhibitor", null)
            );
            default -> List.of(
                    new TreatmentItemDto("No guidelines", "Refer to specialist", null)
            );
        };
    }

    private List<TreatmentItemDto> getProgressionRecommendations(String diagnostic) {
        return switch (diagnostic) {
            case "Luminal A", "Luminal B HER2 negative" -> List.of(
                    new TreatmentItemDto("Stop CDK4/6", "Switch to Everolimus + Exemestane", null),
                    new TreatmentItemDto("Further progression", "Switch to chemotherapy", null)
            );
            case "Luminal B HER2 positive" -> List.of(
                    new TreatmentItemDto("Progression", "T-DM1 or Tucatinib-based regimens", null)
            );
            case "Triple Negative Breast Cancer (TNBC)" -> List.of(
                    new TreatmentItemDto("Progression", "Sacituzumab govitecan", null)
            );
            default -> List.of(
                    new TreatmentItemDto("No data", "Custom evaluation needed", null)
            );
        };
    }

    private int safe(Integer value) {
        return value != null ? value : 0;
    }
}