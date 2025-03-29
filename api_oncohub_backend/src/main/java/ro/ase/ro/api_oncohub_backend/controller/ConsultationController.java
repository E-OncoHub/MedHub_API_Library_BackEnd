package ro.ase.ro.api_oncohub_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationRequestDto;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationResponsetDto;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.GetConsultationByClientDto;
import ro.ase.ro.api_oncohub_backend.exceptions.ConsultationAlreadyViewedException;
import ro.ase.ro.api_oncohub_backend.exceptions.ConsultationNotFoundException;
import ro.ase.ro.api_oncohub_backend.exceptions.InvalidConsultationDataException;
import ro.ase.ro.api_oncohub_backend.services.ConsultationService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/medhub/breastCancer")
@RequiredArgsConstructor
public class ConsultationController {
    private final ConsultationService consultationService;

    @GetMapping("/{idConsultationAccessManager}")
    public ResponseEntity<?> getConsultation(@PathVariable UUID idConsultationAccessManager) {
        try {
            GetConsultationByClientDto consultation = consultationService.getNonAccessedConsultationById(idConsultationAccessManager);
            return ResponseEntity.status(HttpStatus.OK).body(consultation);
        } catch (ConsultationAlreadyViewedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Consultation already viewed.");
        } catch (ConsultationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No consultation found!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createConsultation(@RequestBody CreateConsultationRequestDto consultationRequestDto) {
        try {
            CreateConsultationResponsetDto createdConsultation = consultationService.createConsultation(consultationRequestDto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new CreateConsultationResponsetDto(
                            createdConsultation.accessId(),
                            createdConsultation.accessLink(),
                            createdConsultation.diagnositc(),
                            createdConsultation.firstLine(),
                            createdConsultation.secondLine()
                    ));
        } catch (InvalidConsultationDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
