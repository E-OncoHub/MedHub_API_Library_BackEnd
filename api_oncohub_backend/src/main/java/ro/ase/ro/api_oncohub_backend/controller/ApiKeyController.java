package ro.ase.ro.api_oncohub_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.ase.ro.api_oncohub_backend.dtos.ApiKeyRequestDto;
import ro.ase.ro.api_oncohub_backend.dtos.ApiKeyResponseDto;
import ro.ase.ro.api_oncohub_backend.exceptions.DuplicateApiKeyException;
import ro.ase.ro.api_oncohub_backend.exceptions.ErrorResponse;
import ro.ase.ro.api_oncohub_backend.exceptions.NullApiKeyException;
import ro.ase.ro.api_oncohub_backend.models.ApiKey;
import ro.ase.ro.api_oncohub_backend.services.ApiKeyService;

@RestController
@RequestMapping("/apiKey")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<?> createApiKey(@RequestBody ApiKeyRequestDto requestDto) {
        try {
          ApiKey apiKey = apiKeyService.generateApiKey(requestDto.description());
          return ResponseEntity.status(HttpStatus.CREATED)
                  .body(new ApiKeyResponseDto(
                      apiKey.getKeyValue(),
                      apiKey.getDescription(),
                      apiKey.getExpiresAt()
                  ));
        } catch (DuplicateApiKeyException | NullApiKeyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
