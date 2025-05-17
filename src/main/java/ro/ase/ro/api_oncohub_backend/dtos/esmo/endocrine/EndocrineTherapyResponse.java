package ro.ase.ro.api_oncohub_backend.dtos.esmo.endocrine;

import java.util.List;

public record EndocrineTherapyResponse(
        List<EndocrineStepDto> premenopausal,
        EndocrinePostmenopausalDto postmenopausal
) {}