package ec.edu.espe.logiflow.flota.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
//Validaciones
@Data
public class ConductorCreateRequest {

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El DNI debe tener 10 dígitos numéricos")
    private String dni;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+\\s[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$",
            message = "Debe ingresar exactamente un nombre y un apellido separados por un solo espacio, sin números"
    )
    private String nombreCompleto;

    @NotBlank(message = "El número de licencia es obligatorio")
    @Size(min = 9, max = 9, message = "La licencia debe tener exactamente 9 caracteres")
    @Pattern(
            regexp = "^[A-Z]{3}-[0-9]{5}$",
            message = "Formato de licencia inválido. Debe ser AAA-##### (3 letras, guion, 5 números)"
    )
    private String numeroLicencia;

    @NotBlank(message = "El tipo de licencia es obligatorio")
    @Pattern(
            regexp = "^[ABCE]$",
            message = "Tipo de licencia no válido. Valores permitidos: A, B, C o E"
    )
    private String tipoLicencia;
}