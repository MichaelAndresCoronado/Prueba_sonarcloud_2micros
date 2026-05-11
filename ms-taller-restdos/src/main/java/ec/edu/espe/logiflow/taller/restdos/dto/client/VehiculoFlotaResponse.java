package ec.edu.espe.logiflow.taller.restdos.dto.client;

import lombok.Data;

import java.util.UUID;

@Data
public class VehiculoFlotaResponse {
    private UUID id;
    private String matricula;
    private String tipo;
    private Double capacidadKg;
    private Double autonomiaKm;
    private String estado;
}
