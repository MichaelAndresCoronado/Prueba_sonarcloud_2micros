package ec.edu.espe.logiflow.flota.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ConductorCreateRequest {
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El DNI debe tener 10 dígitos")
    private String dni;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "El número de licencia es obligatorio")
    private String numeroLicencia;
}