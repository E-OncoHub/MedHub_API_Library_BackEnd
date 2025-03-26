package ro.ase.ro.api_oncohub_backend.dtos.consultation;

public record CreateConsultationRequestDto(
        String protocolCarcMamInvaz,
        Integer er,
        Integer pr,
        Integer her2,
        String tnm,
        String histologicType,
        Integer histologicGrade,
        String carcinomaInSitu,
        String nuclearHistologicGrade,
        Integer ki67
) { }
