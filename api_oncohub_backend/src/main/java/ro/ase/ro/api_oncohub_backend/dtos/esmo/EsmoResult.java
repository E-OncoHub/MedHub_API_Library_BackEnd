package ro.ase.ro.api_oncohub_backend.dtos.esmo;

import java.util.List;

public record EsmoResult (
        String diagnostic,
        List<TreatmentItemDto> firstLine,
        List<TreatmentItemDto> secondLine
){ }
