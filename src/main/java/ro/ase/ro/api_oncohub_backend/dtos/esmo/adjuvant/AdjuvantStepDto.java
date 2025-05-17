package ro.ase.ro.api_oncohub_backend.dtos.esmo.adjuvant;

public record AdjuvantStepDto(
        String treatment,
        String condition // possible null
) {}