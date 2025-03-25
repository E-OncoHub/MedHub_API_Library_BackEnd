package ro.ase.ro.api_oncohub_backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.ase.ro.api_oncohub_backend.exceptions.DuplicateApiKeyException;
import ro.ase.ro.api_oncohub_backend.exceptions.NullApiKeyException;
import ro.ase.ro.api_oncohub_backend.models.ApiKey;
import ro.ase.ro.api_oncohub_backend.repositories.ApiKeyRepository;

import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;

    public ApiKey generateApiKey(String description) {
        if (description == null || description.isBlank()) {
            throw new NullApiKeyException("Description (Name) is null or empty");
        }

        if (apiKeyRepository.findByDescription(description).isPresent()) {
           throw new DuplicateApiKeyException("Description (Name) already exists");
        }

        String timeStamp = LocalDateTime.now().toString();
        String rawKey = description + "::" + timeStamp;
        String hashKey = hashBase64SHA256(rawKey).substring(0, 16);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String key = hashKey + "-" + randomPart;

        ApiKey apiKey = new ApiKey();
        apiKey.setKeyValue(key);
        apiKey.setDescription(description);
        apiKey.setIsActive(true);
        apiKey.setCreatedAt(LocalDateTime.now());
        apiKey.setExpiresAt(LocalDateTime.now().plusMonths(12));

        return apiKeyRepository.save(apiKey);
    }

    private String hashBase64SHA256(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported");
        }
    }
}
