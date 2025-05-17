package ro.ase.ro.api_oncohub_backend.dtos.esmo.adjuvant;

import java.util.List;

public record AdjuvantResponse(
        List<AdjuvantStepDto> firstStep,
        AdjuvantStepDto secondStep
) {}