package ro.ase.ro.api_oncohub_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationRequestDto;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationResponsetDto;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.GetConsultationByClientDto;
import ro.ase.ro.api_oncohub_backend.services.ConsultationService;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/medhub/breastCancer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConsultationController {
    private final ConsultationService consultationService;

    @GetMapping("/{idConsultationAccessManager}")
    public ResponseEntity<?> getConsultation(@PathVariable UUID idConsultationAccessManager) {
        GetConsultationByClientDto consultation = consultationService.getNonAccessedConsultationById(idConsultationAccessManager);
        return ResponseEntity.status(HttpStatus.OK).body(consultation);
    }

    @PostMapping
    public ResponseEntity<?> createConsultation(@RequestBody CreateConsultationRequestDto consultationRequestDto) {
        CreateConsultationResponsetDto createdConsultation = consultationService.createConsultation(consultationRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new CreateConsultationResponsetDto(
                        createdConsultation.accessId(),
                        createdConsultation.accessLink(),
                        createdConsultation.diagnositc(),
                        createdConsultation.firstLine(),
                        createdConsultation.secondLine()
                ));
    }
}
