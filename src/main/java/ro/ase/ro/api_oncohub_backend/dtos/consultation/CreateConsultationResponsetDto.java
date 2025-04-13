package ro.ase.ro.api_oncohub_backend.dtos.consultation;

import ro.ase.ro.api_oncohub_backend.dtos.esmo.TreatmentItemDto;

import java.util.List;
import java.util.UUID;

public record CreateConsultationResponsetDto(
        UUID accessId,
        String accessLink,
        String diagnositc,
        List<TreatmentItemDto> firstLine,
        List<TreatmentItemDto> secondLine,
        List<TreatmentItemDto> thirdLine
) { }
