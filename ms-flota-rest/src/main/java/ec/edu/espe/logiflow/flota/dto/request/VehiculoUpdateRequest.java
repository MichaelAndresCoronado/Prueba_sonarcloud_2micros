package ec.edu.espe.logiflow.flota.dto.request;

import ec.edu.espe.logiflow.flota.enums.EstadoVehiculo;
import jakarta.validation.constraints.*;
import lombok.Data;
//Validaciones
@Data
public class VehiculoUpdateRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoVehiculo estado;

    @NotNull(message = "La capacidad es obligatoria")
    @DecimalMin(value = "100.0", message = "La capacidad mínima es 100 kg")
    @DecimalMax(value = "40000.0", message = "La capacidad máxima es 40000 kg")
    @Digits(integer = 5, fraction = 2, message = "La capacidad debe tener entre 3 y 5 dígitos enteros")
    private Double capacidadKg;

    @NotNull(message = "La autonomía es obligatoria")
    @DecimalMin(value = "50.0", message = "La autonomía mínima es 50 km")
    @DecimalMax(value = "350.0", message = "La autonomía máxima es 350 km")
    @Digits(integer = 3, fraction = 2, message = "La autonomía debe tener entre 2 y 3 dígitos enteros")
    private Double autonomiaKm;
}