package ec.edu.espe.logiflow.taller.restdos.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OrdenMantenimientoResponse {
    private UUID id;
    private String matricula;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaRegistro;
}
