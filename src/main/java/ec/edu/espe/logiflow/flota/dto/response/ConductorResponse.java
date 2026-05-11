package ec.edu.espe.logiflow.flota.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConductorResponse {
    private UUID id;
    private String dni;
    private String nombreCompleto;
    private String numeroLicencia;
    private String tipoLicencia;
    private Boolean activo;
    private LocalDateTime createdAt;
}