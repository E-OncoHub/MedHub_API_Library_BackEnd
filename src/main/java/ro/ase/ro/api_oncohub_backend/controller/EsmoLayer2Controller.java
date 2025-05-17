package ro.ase.ro.api_oncohub_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.adjuvant.AdjuvantDto;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.adjuvant.AdjuvantResponse;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.endocrine.EndocrineTherapyDto;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.endocrine.EndocrineTherapyResponse;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.neoadjuvant.NeoAdjuvantDto;
import ro.ase.ro.api_oncohub_backend.dtos.esmo.neoadjuvant.NeoAdjuvantResponse;
import ro.ase.ro.api_oncohub_backend.services.AdjuvantService;
import ro.ase.ro.api_oncohub_backend.services.EndocrineTherapyService;
import ro.ase.ro.api_oncohub_backend.services.NeoadjuvantService;

import java.util.List;

@RestController
@RequestMapping("api/v1/medhub/breastCancer/esmo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EsmoLayer2Controller {
    private final NeoadjuvantService neoAdjuvantService;
    private final AdjuvantService adjuvantService;
    private final EndocrineTherapyService endocrineTherapyService;

    @PostMapping("/neoadjuvant-treatment")
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

    @PostMapping("/adjuvant-treatment")
    public ResponseEntity<?> getAdjuvantTreatmentLayer2(@RequestBody AdjuvantDto requestAdjuvantDto) {
        AdjuvantResponse response = adjuvantService.getAdjuvantPlan(
                requestAdjuvantDto.er(),
                requestAdjuvantDto.pr(),
                requestAdjuvantDto.her2(),
                requestAdjuvantDto.tnm()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/endocrine-therapy")
    public ResponseEntity<?> getEndocrineTherapyPlan(@RequestBody EndocrineTherapyDto dto) {
        EndocrineTherapyResponse response = endocrineTherapyService.getEndocrineTherapyPlan(dto.er(), dto.pr());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
