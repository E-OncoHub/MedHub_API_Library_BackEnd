package ro.ase.ro.api_oncohub_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ase.ro.api_oncohub_backend.models.ConsultationAccessManager;

import java.util.Optional;
import java.util.UUID;

public interface ConsultationAccessManagerRepository extends JpaRepository<ConsultationAccessManager, UUID> {
    Optional<ConsultationAccessManager> findByIdAndIsViewedIsFalse (UUID uuid);
}
