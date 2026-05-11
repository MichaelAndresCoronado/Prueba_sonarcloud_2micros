package ec.edu.espe.logiflow.flota.dto.request;

import ec.edu.espe.logiflow.flota.enums.TipoVehiculo;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VehiculoCreateRequest {

    @NotBlank(message = "La matrícula es obligatoria")
    @Pattern(
            regexp = "^[A-Z]{3}-[0-9]{4}$",
            message = "La matrícula debe tener el formato AAA-#### (3 letras, guion y 4 números)"
    )
    private String matricula;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipo;

    @NotNull(message = "La capacidad es obligatoria")
    @DecimalMin(value = "100.0", message = "La capacidad mínima es 100 kg")
    @DecimalMax(value = "40000.0", message = "La capacidad máxima es 40000 kg")
    // El Regex asegura que no entren etiquetas HTML y que el formato decimal sea correcto
    @Digits(integer = 5, fraction = 2, message = "La capacidad debe tener entre 3 y 5 dígitos enteros")
    private Double capacidadKg;

    @NotNull(message = "La autonomía es obligatoria")
    @DecimalMin(value = "50.0", message = "La autonomía mínima es 50 km")
    @DecimalMax(value = "350.0", message = "La autonomía máxima es 350 km")
    @Digits(integer = 3, fraction = 2, message = "La autonomía debe tener entre 2 y 3 dígitos enteros")
    private Double autonomiaKm;
}