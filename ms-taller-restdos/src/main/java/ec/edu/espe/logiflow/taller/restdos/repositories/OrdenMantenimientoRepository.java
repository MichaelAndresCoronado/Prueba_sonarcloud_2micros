package ec.edu.espe.logiflow.taller.restdos.repositories;

import ec.edu.espe.logiflow.taller.restdos.models.OrdenMantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrdenMantenimientoRepository extends JpaRepository<OrdenMantenimiento, UUID> {
}
