package ro.ase.ro.api_oncohub_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.neoadjuvant.NeoAdjuvantDto;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.neoadjuvant.NeoAdjuvantResponse;
import ro.ase.ro.api_oncohub_backend.services.NeoadjuvantService;

import java.util.List;

@RestController
@RequestMapping("api/v1/medhub/breastCancer/esmo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EsmoLayer2Controller {
    private final NeoadjuvantService neoAdjuvantService;

    @GetMapping("/neoadjuvant-treatment")
    public ResponseEntity<?> getNeoadjuvantTreatmentLayer2(@RequestBody NeoAdjuvantDto requestNeoAdjuvantDto) {
        NeoAdjuvantResponse response = neoAdjuvantService.getNeoAdjuvantPlanLayer2(
                requestNeoAdjuvantDto.er(),
                requestNeoAdjuvantDto.pr(),
                requestNeoAdjuvantDto.her2(),
                requestNeoAdjuvantDto.tnm()
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(new NeoAdjuvantResponse(
                        response.initialNeoadjuvantTreatment(),
                        response.followUpNeoadjuvantTreatment(),
                        response.alternativeFollowUpNeoadjuvantTreatment()
                ));
    }
}
