package ro.ase.ro.api_oncohub_backend.services;

import org.springframework.stereotype.Service;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.endocrine.*;

import java.util.List;

@Service
public class EndocrineTherapyService {

    public EndocrineTherapyResponse getEndocrineTherapyPlan(Integer er, Integer pr) {
        boolean erPositive = er != null && er >= 10;
        boolean prPositive = pr != null && pr >= 10;

        if (!(erPositive || prPositive)) {
            return new EndocrineTherapyResponse(List.of(), null); // no endocrine indication
        }

        List<EndocrineStepDto> premenopausal = List.of(
                new EndocrineStepDto("Luminal A-like stage I", "Tamoxifen [I,A]"),
                new EndocrineStepDto("Luminal A-like stage II / III or Luminal B-like stage I / II / III", "OFS-tamoxifen [I,A] / OFS-AI [I,A]")
        );

        EndocrinePostmenopausalDto postmenopausal = new EndocrinePostmenopausalDto(
                "Tamoxifen followed by AI [I, A]",
                "AI [I, A]",
                "AI followed by tamoxifen [I, A]",
                "Tamoxifen [I,A]"
        );

        return new EndocrineTherapyResponse(premenopausal, postmenopausal);
    }
}