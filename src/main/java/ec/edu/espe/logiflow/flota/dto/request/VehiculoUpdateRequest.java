package ec.edu.espe.logiflow.flota.dto.request;

import ec.edu.espe.logiflow.flota.enums.EstadoVehiculo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehiculoUpdateRequest {
    @NotNull(message = "El estado es obligatorio")
    private EstadoVehiculo estado;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private Double capacidadKg;

    @NotNull(message = "La autonomía es obligatoria")
    @Min(value = 1, message = "La autonomía debe ser mayor a 0")
    private Double autonomiaKm;
}