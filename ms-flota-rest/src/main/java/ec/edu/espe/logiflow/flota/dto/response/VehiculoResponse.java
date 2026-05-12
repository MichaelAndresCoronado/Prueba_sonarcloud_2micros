package ec.edu.espe.logiflow.flota.dto.response;

import ec.edu.espe.logiflow.flota.enums.EstadoVehiculo;
import ec.edu.espe.logiflow.flota.enums.TipoVehiculo;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
//Formato de respuesta
@Data
@Builder
public class VehiculoResponse {
    private UUID id;
    private String matricula;
    private TipoVehiculo tipo;
    private Double capacidadKg;
    private Double autonomiaKm;
    private EstadoVehiculo estado;
    private LocalDateTime createdAt;
}