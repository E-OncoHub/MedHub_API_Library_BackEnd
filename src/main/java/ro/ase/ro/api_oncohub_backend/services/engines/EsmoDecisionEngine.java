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
        List<TreatmentItemDto> thirdLine;

        if (consultation.getTnm() != null && consultation.getTnm().contains("M1")) {
            firstLine = getAdvancedRecommendations(diagnostic);
            secondLine = getProgressionRecommendations(diagnostic);
            thirdLine = List.of(); // No third line for advanced
        } else {
            List<TreatmentItemDto> earlyTreatments = getEarlyStageRecommendations(consultation);
            firstLine = earlyTreatments.size() > 0 ? List.of(earlyTreatments.get(0)) : List.of();
            secondLine = earlyTreatments.size() > 1 ? List.of(earlyTreatments.get(1)) : List.of();
            thirdLine = earlyTreatments.size() > 2 ? List.of(earlyTreatments.get(2)) : List.of();
        }

        return new EsmoResult(diagnostic, firstLine, secondLine, thirdLine);
    }

    public String determineDiagnostic(Consultation c) {
        final int er = safe(c.getEr());
        final int pr = safe(c.getPr());
        final int her2 = safe(c.getHer2());
        final int ki67 = safe(c.getKi67());

        boolean erPositive = er >= 10;
        boolean prPositive = pr >= 10;
        boolean her2Positive = her2 > 0;

        if (erPositive && prPositive && !her2Positive) {
            return ki67 <= 14 ? "Luminal A" : "Luminal B HER2 negative";
        } else if (erPositive && her2Positive) {
            return "Luminal B HER2 positive";
        } else if (!erPositive && !prPositive && !her2Positive) {
            return "Triple Negative Breast Cancer (TNBC)";
        } else {
            return "Unknown Diagnostic";
        }
    }

    private List<TreatmentItemDto> getEarlyStageRecommendations(Consultation consultation) {
        final int er = safe(consultation.getEr());
        final int pr = safe(consultation.getPr());
        final int her2 = safe(consultation.getHer2());
        final String tnm = consultation.getTnm() != null ? consultation.getTnm() : "";

        boolean erPositive = er >= 10;
        boolean prPositive = pr >= 10;
        boolean her2Positive = her2 > 0;
        boolean her2Negative = !her2Positive;

        boolean t1aOrT1b = tnm.contains("T1a") || tnm.contains("T1b");
        boolean t1ct1t2t3t4 = tnm.contains("T1c") || tnm.contains("T1") || tnm.contains("T2") || tnm.contains("T3") || tnm.contains("T4");
        boolean t1 = tnm.contains("T1");
        boolean t2 = tnm.contains("T2");
        boolean n0 = tnm.contains("N0");
        boolean n1n2n3 = tnm.contains("N1") || tnm.contains("N2") || tnm.contains("N3");

        if (erPositive && prPositive) {
            return List.of(
                    new TreatmentItemDto("Adjuvant", "Endocrine therapy", null)
            );
        }

        if ((erPositive || prPositive) && her2Negative) {
            return List.of(
                    new TreatmentItemDto("Adjuvant", "Neoadjuvant therapy", null),
                    new TreatmentItemDto("Surgery", "Primary Surgery +/- RT", null),
                    new TreatmentItemDto("Adjuvant", "Systematic treatment", null)
            );
        }

        if (her2Positive && (erPositive || prPositive)) {
            if (tnm.contains("T1") && n0) {
                return List.of(
                        new TreatmentItemDto("Surgery", "Primary Surgery +/- RT", null),
                        new TreatmentItemDto("Adjuvant", "Systematic treatment", null)
                );
            } else if (t2 && n1n2n3) {
                return List.of(
                        new TreatmentItemDto("Adjuvant", "Neoadjuvant Therapy", null),
                        new TreatmentItemDto("Surgery", "Primary Surgery +/- RT", null),
                        new TreatmentItemDto("Adjuvant", "Systematic treatment", null)
                );
            }
        }

        if (her2Negative && !erPositive && !prPositive) {
            if (t1 && n0) {
                return List.of(
                        new TreatmentItemDto("Surgery", "Primary Surgery +/- RT", null),
                        new TreatmentItemDto("Adjuvant", "Systematic treatment", null)
                );
            } else if (t1ct1t2t3t4 || n1n2n3) {
                return List.of(
                        new TreatmentItemDto("Adjuvant", "Neoadjuvant Therapy", null),
                        new TreatmentItemDto("Surgery", "Primary Surgery +/- RT", null),
                        new TreatmentItemDto("Adjuvant", "Systematic treatment", null)
                );
            }
        }

        return List.of(
                new TreatmentItemDto("No early-stage guidance", "Refer to specialist", null)
        );
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