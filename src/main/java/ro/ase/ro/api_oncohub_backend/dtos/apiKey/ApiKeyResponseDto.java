package ro.ase.ro.api_oncohub_backend.dtos.apiKey;

import java.time.LocalDateTime;

public record ApiKeyResponseDto(
        String key,
        String description,
        LocalDateTime expiresAt
) { }
