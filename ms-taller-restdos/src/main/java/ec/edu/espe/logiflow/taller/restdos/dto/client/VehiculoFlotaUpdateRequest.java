package ec.edu.espe.logiflow.taller.restdos.dto.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehiculoFlotaUpdateRequest {
    private String estado;
    private Double capacidadKg;
    private Double autonomiaKm;
}
