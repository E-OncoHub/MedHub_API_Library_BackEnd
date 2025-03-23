package ro.ase.ro.api_oncohub_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ase.ro.api_oncohub_backend.models.ApiKey;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyValue(String keyValue);

    Optional<ApiKey> findByKeyValueAndIsActiveTrue(String keyValue);
}