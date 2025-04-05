package ro.ase.ro.api_oncohub_backend.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AzureTokenValidator {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void validateAndLog(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token format");
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode payload = objectMapper.readTree(payloadJson);

            String username = payload.has("upn") ? payload.get("upn").asText() :
                    payload.has("preferred_username") ? payload.get("preferred_username").asText() :
                            "Unknown User";

            System.out.println("[Azure Access] Request made by: " + username);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed to decode token");
        }
    }
}
