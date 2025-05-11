package ro.ase.ro.api_oncohub_backend.services;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.neoadjuvant.NeoAdjuvantResponse;

@Service
@RequiredArgsConstructor
public class NeoadjuvantService {
    public NeoAdjuvantResponse getNeoAdjuvantPlanLayer2(Integer er, Integer pr, Integer her2, String tnm) {
        boolean hrPozitive = er > 0 || pr > 0;
        boolean her2Pozitive = her2 > 0;
        boolean t1 = tnm.contains("T1");
        boolean t2 = tnm.contains("T2");
        boolean t2t3t4 = tnm.contains("T2") || tnm.contains("T3") || tnm.contains("T4");
        boolean n0 = tnm.contains("N0");
        boolean n0n1n2n3n4 = n0 || tnm.contains("N1") || tnm.contains("N2") || tnm.contains("N3") || tnm.contains("N4");
        boolean n1n2n3n4 = tnm.contains("N1") || tnm.contains("N2") || tnm.contains("N3") || tnm.contains("N4");

        String initialNeoadjuvantTreatment = "N/A";
        String followUpNeoadjuvantTreatment = "N/A";
        String alternativeFollowUpNeoadjuvantTreatment = "N/A";

        if (her2Pozitive && (t2t3t4 || n1n2n3n4)) {
            initialNeoadjuvantTreatment = "6-8 cycles ChT-HP [I, A; MCBS C]";
            followUpNeoadjuvantTreatment = null;
            alternativeFollowUpNeoadjuvantTreatment = null;
        } else {
            if (hrPozitive) {
                initialNeoadjuvantTreatment = "(Neo)Adjuvant ChT (Luminal A like High Risk & Luminal B like";
                followUpNeoadjuvantTreatment = "Endocrine Theraphy [I, A]";
                alternativeFollowUpNeoadjuvantTreatment = "Adjuvant Endocrine Theraphy [I, A] (Luminal A like, Low Risk)";
            }
            else {
                if (t1 && n0) {
                    initialNeoadjuvantTreatment = "6-8 cycles: taxane-(carbo)platin";
                    followUpNeoadjuvantTreatment = null;
                    alternativeFollowUpNeoadjuvantTreatment = "Doxorubicin -  / Epirubicin - Cyclophosphamide [I, A]";
                } else if (t2t3t4 || n1n2n3n4) {    //TODO: Validate if N+ is here from 1 to 4 or from 0 to 4
                    initialNeoadjuvantTreatment = "6-8 cycles: taxane-carboplatin";
                    followUpNeoadjuvantTreatment = "Doxorubicin -  / Epirubicin - Cyclophosphamide with pembrolizumab [I, A; MCBS A]'";
                    alternativeFollowUpNeoadjuvantTreatment = null;
                }
            }
        }

        return new NeoAdjuvantResponse(
                initialNeoadjuvantTreatment,
                followUpNeoadjuvantTreatment,
                alternativeFollowUpNeoadjuvantTreatment
        );
    }
}
