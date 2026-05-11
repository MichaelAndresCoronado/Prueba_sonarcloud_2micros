package ec.edu.espe.logiflow.flota.repositories;

import ec.edu.espe.logiflow.flota.models.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ConductorRepository extends JpaRepository<Conductor, UUID> {
    boolean existsByDni(String dni);
    boolean existsByNumeroLicencia(String numeroLicencia);
}