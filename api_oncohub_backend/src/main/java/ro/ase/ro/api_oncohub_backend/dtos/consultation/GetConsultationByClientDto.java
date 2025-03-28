package ro.ase.ro.api_oncohub_backend.dtos.consultation;

import ro.ase.ro.api_oncohub_backend.dtos.esmo.TreatmentItemDto;

import java.util.List;

public record GetConsultationByClientDto(
        String protocolCarcMamInvaz,
        Integer er,
        Integer pr,
        Integer her2,
        String tnm,
        String histologicType,
        Integer histologicGrade,
        String carcinomaInSitu,
        String nuclearHistologicGrade,
        Integer ki67,
        String diagnostic,
        List<TreatmentItemDto> firstLine,
        List<TreatmentItemDto> secondLine
) { }
