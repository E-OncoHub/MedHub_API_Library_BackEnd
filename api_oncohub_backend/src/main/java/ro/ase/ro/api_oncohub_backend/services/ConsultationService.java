package ro.ase.ro.api_oncohub_backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationRequestDto;
import ro.ase.ro.api_oncohub_backend.exceptions.InvalidConsultationDataException;
import ro.ase.ro.api_oncohub_backend.models.Consultation;
import ro.ase.ro.api_oncohub_backend.models.ConsultationAccessManager;
import ro.ase.ro.api_oncohub_backend.repositories.ConsultationAccessManagerRepository;
import ro.ase.ro.api_oncohub_backend.repositories.ConsultationRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ConsultationAccessManagerRepository consultationAccessManagerRepository;

    public UUID createConsultation(CreateConsultationRequestDto createConsultationRequestDto) {
        if (createConsultationRequestDto.er() == null
                || createConsultationRequestDto.pr() == null
                || createConsultationRequestDto.ki67() == null
                || createConsultationRequestDto.her2() == null) {
            throw new InvalidConsultationDataException("Invalid biomarkers: ER, PR, HER2, KI67");
        }

        if (createConsultationRequestDto.tnm() == null) {
            throw new InvalidConsultationDataException("Indicator not found: TNM");
        }

        Consultation consultation = new Consultation();
        consultation.setProtocolCarcMamInvaz(createConsultationRequestDto.protocolCarcMamInvaz());
        consultation.setEr(createConsultationRequestDto.er());
        consultation.setPr(createConsultationRequestDto.pr());
        consultation.setHer2(createConsultationRequestDto.her2());
        consultation.setTnm(createConsultationRequestDto.tnm());
        consultation.setHistologicType(createConsultationRequestDto.histologicType());
        consultation.setHistologicGrade(createConsultationRequestDto.histologicGrade());
        consultation.setCarcinomaInSitu(createConsultationRequestDto.carcinomaInSitu());
        consultation.setNuclearHistologicGrade(createConsultationRequestDto.nuclearHistologicGrade());
        consultation.setKi67(createConsultationRequestDto.ki67());

        // Temporary information
        consultation.setStage("N/A");
        consultation.setDiagnostic("N/A");
        consultation.setFirstLineTreatment("N/A");
        consultation.setSecondLineTreatment("N/A");

        Consultation saved = consultationRepository.save(consultation);

        ConsultationAccessManager consultationAccessManager = new ConsultationAccessManager();
        consultationAccessManager.setConsultation(consultation);
        consultationAccessManager.setIsViewed(false);
        ConsultationAccessManager savedConsultationAccessManager = consultationAccessManagerRepository.save(consultationAccessManager);

        return savedConsultationAccessManager.getId();
    }
}
