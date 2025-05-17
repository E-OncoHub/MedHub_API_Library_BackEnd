package ro.ase.ro.api_oncohub_backend.services;

import org.springframework.stereotype.Service;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.adjuvant.AdjuvantResponse;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.adjuvant.AdjuvantStepDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdjuvantService {

    public AdjuvantResponse getAdjuvantPlan(Integer er, Integer pr, Integer her2, String tnm) {
        List<AdjuvantStepDto> firstStep = new ArrayList<>();
        AdjuvantStepDto secondStep = null;

        boolean hrNegative = er < 10 && pr < 10;
        boolean her2Positive = her2 > 0;
        boolean her2Negative = her2 <= 0;

        boolean t1 = tnm.contains("T1");
        boolean t2t3t4 = tnm.contains("T2") || tnm.contains("T3") || tnm.contains("T4");
        boolean n0 = tnm.contains("N0");
        boolean n1n5 = tnm.contains("N1") || tnm.contains("N2") || tnm.contains("N3") || tnm.contains("N4") || tnm.contains("N5");

        // Caz 1
        if (her2Positive && t1 && n0) {
            firstStep.add(new AdjuvantStepDto("12 weeks pacitaxel", null));
            firstStep.add(new AdjuvantStepDto("18 cycles trastuzumab", null));
        }

        // Caz 2
        else if (her2Positive && t2t3t4 && n0) {
            firstStep.add(new AdjuvantStepDto("6-8 cycles ChT - trastuzumab [I, B; MCBS A]", null));
            secondStep = new AdjuvantStepDto(
                    "Endrocrine Theraphy if HR+",
                    "Complete 1 year of Trastuzumab [I, B; MCBS A"
            );
        }

        // Caz 3
        else if (her2Positive && n1n5) {
            firstStep.add(new AdjuvantStepDto("6-8 cycles ChT - HP [I, A; MCBS A]", null));
            secondStep = new AdjuvantStepDto(
                    "Endrocrine Theraphy if HR+ [I; A]",
                    "Complete 1 year of HP [I, A; MCBS A"
            );
        }

        // Caz 4
        else if (her2Positive && (t2t3t4 || n1n5)) {
            firstStep.add(new AdjuvantStepDto("cN0 at initial diagnostic: Complete 1 year of Trastuzumab. If HR+ adjuvant Endocrine Theraphy", null));
            firstStep.add(new AdjuvantStepDto("cN+ or pN+: Complete 1 year of HP. If HR+, adjuvant Endocrine Theraphy.", null));
            firstStep.add(new AdjuvantStepDto("Residual invasive disease: T-DM1 up to 14 cycles. If HR+, adjuvant ET", null));
        }

        // Caz 5: HR- și HER2 negativ, T1 și N0
        else if (hrNegative && her2Negative && t1 && n0) {
            firstStep.add(new AdjuvantStepDto("pT1b pN0: 6-8 cycles systematic ChT", null));
            firstStep.add(new AdjuvantStepDto("pT1b any pN: 6-8 cycles systematic ChT + If gBRCA 1/2m - Olaparib for 1 year", null));
        }

        // Caz 6: HR- și HER2 negativ, T2/T3/T4 și N1+
        else if (hrNegative && her2Negative && (t2t3t4 || n1n5)) {
            firstStep.add(new AdjuvantStepDto("pCR: 9 cycles Pembrolizumab", null));
            firstStep.add(new AdjuvantStepDto("Residual Disease - Default Treatment: 9 cycles Pembrolizumab", null));
            firstStep.add(new AdjuvantStepDto("Residual Disease - gBRCA 1/2-wt: Caoecutabine for 6-8 cycles", null));
            firstStep.add(new AdjuvantStepDto("Residual Disease - gBRCA 1/2-m: Olaparib for 1 year", null));
        }

        return new AdjuvantResponse(firstStep, secondStep);
    }
}