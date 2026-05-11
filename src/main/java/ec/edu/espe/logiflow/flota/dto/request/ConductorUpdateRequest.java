package ec.edu.espe.logiflow.flota.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ConductorUpdateRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+\\s[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$",
            message = "Debe ingresar exactamente un nombre y un apellido separados por un solo espacio, sin números ni etiquetas"
    )
    private String nombreCompleto;

    @NotBlank(message = "El tipo de licencia es obligatorio")
    @Pattern(
            regexp = "^[ABCE]$",
            message = "Tipo de licencia no válido. Valores permitidos: A, B, C o E"
    )
    private String tipoLicencia;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}