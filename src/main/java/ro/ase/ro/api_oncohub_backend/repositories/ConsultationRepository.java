package ro.ase.ro.api_oncohub_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ase.ro.api_oncohub_backend.models.Consultation;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}