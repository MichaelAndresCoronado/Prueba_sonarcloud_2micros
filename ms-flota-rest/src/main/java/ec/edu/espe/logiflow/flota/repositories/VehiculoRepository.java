package ec.edu.espe.logiflow.flota.repositories;

import ec.edu.espe.logiflow.flota.enums.EstadoVehiculo;
import ec.edu.espe.logiflow.flota.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
//repo
public interface VehiculoRepository extends JpaRepository<Vehiculo, UUID> {
    boolean existsByMatricula(String matricula);
    // Operación clave para Ruteo
    List<Vehiculo> findByEstado(EstadoVehiculo estado);
}