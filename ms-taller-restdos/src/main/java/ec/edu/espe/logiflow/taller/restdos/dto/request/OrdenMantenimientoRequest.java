package ec.edu.espe.logiflow.taller.restdos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdenMantenimientoRequest {

    @NotBlank(message = "La matrícula es obligatoria")
    @Pattern(regexp = "^[A-Z]{3}-[0-9]{4}$", message = "La matrícula debe tener el formato AAA-####")
    private String matricula;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;
}
