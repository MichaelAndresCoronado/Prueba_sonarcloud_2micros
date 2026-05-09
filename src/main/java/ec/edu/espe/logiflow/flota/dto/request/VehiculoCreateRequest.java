package ec.edu.espe.logiflow.flota.dto.request;

import ec.edu.espe.logiflow.flota.enums.TipoVehiculo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehiculoCreateRequest {
    @NotBlank(message = "La matrícula es obligatoria")
    private String matricula;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipo;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private Double capacidadKg;

    @NotNull(message = "La autonomía es obligatoria")
    @Min(value = 1, message = "La autonomía debe ser mayor a 0")
    private Double autonomiaKm;
}