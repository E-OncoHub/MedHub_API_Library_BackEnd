package ro.ase.ro.api_oncohub_backend.dtos.consultation;

public record CreateConsultationRequestDtoV2(
        String protocolCarcMamInvaz,
        Integer er,
        Integer pr,
        String her2,
        Integer her2Numeric,
        Integer her3,
        String tnm,
        String histologicType,
        Integer histologicGrade,
        String carcinomaInSitu,
        String nuclearHistologicGrade,
        Integer ki67
) {
    public CreateConsultationRequestDtoV2(
            String protocolCarcMamInvaz,
            Integer er,
            Integer pr,
            String her2,
            String tnm,
            String histologicType,
            Integer histologicGrade,
            String carcinomaInSitu,
            String nuclearHistologicGrade,
            Integer ki67) {
        this(protocolCarcMamInvaz, er, pr, her2, null, null, tnm,
                histologicType, histologicGrade, carcinomaInSitu, nuclearHistologicGrade, ki67);
    }
}