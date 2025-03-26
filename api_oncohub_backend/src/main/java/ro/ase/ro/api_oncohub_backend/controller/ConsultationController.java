package ro.ase.ro.api_oncohub_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationRequestDto;
import ro.ase.ro.api_oncohub_backend.dtos.consultation.CreateConsultationResponsetDto;
import ro.ase.ro.api_oncohub_backend.exceptions.InvalidConsultationDataException;
import ro.ase.ro.api_oncohub_backend.services.ConsultationService;

@RestController
@RequestMapping("/medhub/breastCancer")
@RequiredArgsConstructor
public class ConsultationController {
    private final ConsultationService consultationService;

    @PostMapping
    public ResponseEntity<?> createConsultation(@RequestBody CreateConsultationRequestDto consultationRequestDto) {
        try {
            var accessID = consultationService.createConsultation(consultationRequestDto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new CreateConsultationResponsetDto(accessID));
        } catch (InvalidConsultationDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
