package ro.ase.ro.api_oncohub_backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ase.ro.api_oncohub_backend.models.ApiKey;
import ro.ase.ro.api_oncohub_backend.repositories.ApiKeyRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;

    public ApiKey generateApiKey(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is null or empty");
        }

        boolean exists = apiKeyRepository.findByDescription(description).isPresent();
        if (exists) {
            throw new IllegalArgumentException("Description already exists");
        }

        ApiKey apiKey = new ApiKey();
        apiKey.setKeyValue(UUID.randomUUID().toString());
        apiKey.setDescription(description);
        apiKey.setIsActive(true);
        apiKey.setCreatedAt(LocalDateTime.now());
        apiKey.setExpiresAt(LocalDateTime.now().plusMonths(6));

        return apiKeyRepository.save(apiKey);
    }
}
