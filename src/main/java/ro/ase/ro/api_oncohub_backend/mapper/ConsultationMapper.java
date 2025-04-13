package ro.ase.ro.api_oncohub_backend.mapper;

import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationRequestDto;
import ro.ase.ro.api_oncohub_backend.models.Consultation;

public class ConsultationMapper {
    public Consultation fromCreateDto(CreateConsultationRequestDto dto) {
        Consultation c = new Consultation();
        c.setProtocolCarcMamInvaz(dto.protocolCarcMamInvaz());
        c.setEr(dto.er());
        c.setPr(dto.pr());
        c.setHer2(dto.her2());
        c.setTnm(dto.tnm());
        c.setHistologicType(dto.histologicType());
        c.setHistologicGrade(dto.histologicGrade());
        c.setCarcinomaInSitu(dto.carcinomaInSitu());
        c.setNuclearHistologicGrade(dto.nuclearHistologicGrade());
        c.setKi67(dto.ki67());
        return c;
    }
}
