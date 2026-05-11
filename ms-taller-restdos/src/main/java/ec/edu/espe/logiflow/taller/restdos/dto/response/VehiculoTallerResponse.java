package ec.edu.espe.logiflow.taller.restdos.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class VehiculoTallerResponse {
    private UUID id;
    private String matricula;
    private String tipo;
    private Double capacidadKg;
    private Double autonomiaKm;
    private String estado;
    private Boolean disponibleParaMantenimiento;
}
