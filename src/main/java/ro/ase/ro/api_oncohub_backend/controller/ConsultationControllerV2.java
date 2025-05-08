package ro.ase.ro.api_oncohub_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationRequestDtoV2;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationResponsetDto;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.GetConsultationByClientDto;
import ro.ase.ro.api_oncohub_backend.services.ConsultationService;

import java.util.UUID;

@RestController
@RequestMapping("api/v2/medhub/breastCancer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConsultationControllerV2 {
    private final ConsultationService consultationServiceV2;

    @GetMapping("/{idConsultationAccessManager}")
    public ResponseEntity<?> getConsultation(@PathVariable UUID idConsultationAccessManager) {
        GetConsultationByClientDto consultation = consultationServiceV2.getNonAccessedConsultationById(idConsultationAccessManager);
        return ResponseEntity.status(HttpStatus.OK).body(consultation);
    }

    @PostMapping
    public ResponseEntity<?> createConsultation(@RequestBody CreateConsultationRequestDtoV2 consultationRequestDto) {
        CreateConsultationResponsetDto createdConsultation = consultationServiceV2.createConsultationV2(consultationRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(createdConsultation);
    }
}