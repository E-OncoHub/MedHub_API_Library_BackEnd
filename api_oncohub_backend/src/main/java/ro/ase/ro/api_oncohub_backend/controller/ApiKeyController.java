package ro.ase.ro.api_oncohub_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.ase.ro.api_oncohub_backend.dtos.apiKey.ApiKeyByDescriptionResponseDto;
import ro.ase.ro.api_oncohub_backend.dtos.apiKey.ApiKeyRequestDto;
import ro.ase.ro.api_oncohub_backend.dtos.apiKey.ApiKeyResponseDto;
import ro.ase.ro.api_oncohub_backend.models.ApiKey;
import ro.ase.ro.api_oncohub_backend.services.ApiKeyService;

@RestController
@RequestMapping("api/v1/apiKey")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyResponseDto> createApiKey(@RequestBody ApiKeyRequestDto requestDto) {
        ApiKey apiKey = apiKeyService.generateApiKey(requestDto.description());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiKeyResponseDto(
                        apiKey.getKeyValue(),
                        apiKey.getDescription(),
                        apiKey.getExpiresAt()
                ));
    }

    @GetMapping
    public ResponseEntity<ApiKeyByDescriptionResponseDto> getApiKey(@RequestBody ApiKeyRequestDto requestDto) {
        String keyValue = apiKeyService.getApiKeyByDescription(requestDto.description());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiKeyByDescriptionResponseDto(keyValue));
    }
}