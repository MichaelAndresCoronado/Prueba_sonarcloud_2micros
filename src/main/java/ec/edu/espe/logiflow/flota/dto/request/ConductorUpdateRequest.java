package ec.edu.espe.logiflow.flota.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConductorUpdateRequest {
    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}