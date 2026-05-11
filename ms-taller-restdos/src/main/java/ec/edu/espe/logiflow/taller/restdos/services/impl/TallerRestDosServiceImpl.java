package ec.edu.espe.logiflow.taller.restdos.services.impl;

import ec.edu.espe.logiflow.taller.restdos.dto.client.VehiculoFlotaResponse;
import ec.edu.espe.logiflow.taller.restdos.dto.client.VehiculoFlotaUpdateRequest;
import ec.edu.espe.logiflow.taller.restdos.dto.request.OrdenMantenimientoRequest;
import ec.edu.espe.logiflow.taller.restdos.dto.response.OrdenMantenimientoResponse;
import ec.edu.espe.logiflow.taller.restdos.dto.response.VehiculoTallerResponse;
import ec.edu.espe.logiflow.taller.restdos.models.OrdenMantenimiento;
import ec.edu.espe.logiflow.taller.restdos.repositories.OrdenMantenimientoRepository;
import ec.edu.espe.logiflow.taller.restdos.services.TallerRestDosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class TallerRestDosServiceImpl implements TallerRestDosService {

    private final OrdenMantenimientoRepository ordenMantenimientoRepository;
    private final WebClient flotaWebClient;

    @Override
    @Transactional(readOnly = true)
    public VehiculoTallerResponse consultarVehiculo(String matricula) {
        VehiculoFlotaResponse vehiculo = obtenerVehiculoDesdeFlota(matricula);
        return mapToVehiculoTallerResponse(vehiculo);
    }

    @Override
    public OrdenMantenimientoResponse registrarOrdenMantenimiento(OrdenMantenimientoRequest request) {
        VehiculoFlotaResponse vehiculo = obtenerVehiculoDesdeFlota(request.getMatricula());

        OrdenMantenimiento orden = OrdenMantenimiento.builder()
                .matricula(vehiculo.getMatricula())
                .descripcion(request.getDescripcion().trim())
                .estado("REGISTRADA")
                .build();

        orden = ordenMantenimientoRepository.save(orden);

        actualizarVehiculoEnMantenimiento(vehiculo);

        return OrdenMantenimientoResponse.builder()
                .id(orden.getId())
                .matricula(orden.getMatricula())
                .descripcion(orden.getDescripcion())
                .estado(orden.getEstado())
                .fechaRegistro(orden.getFechaRegistro())
                .build();
    }

    private VehiculoFlotaResponse obtenerVehiculoDesdeFlota(String matricula) {
        try {
            return flotaWebClient.get()
                    .uri("/api/v1/vehiculos/matricula/{matricula}", matricula.toUpperCase())
                    .retrieve()
                    .bodyToMono(VehiculoFlotaResponse.class)
                    .block();
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Vehículo no encontrado en ms-flota-rest o servicio de flota no disponible"
            );
        }
    }

    private void actualizarVehiculoEnMantenimiento(VehiculoFlotaResponse vehiculo) {
        VehiculoFlotaUpdateRequest updateRequest = VehiculoFlotaUpdateRequest.builder()
                .estado("MANTENIMIENTO")
                .capacidadKg(vehiculo.getCapacidadKg())
                .autonomiaKm(vehiculo.getAutonomiaKm())
                .build();

        try {
            flotaWebClient.put()
                    .uri("/api/v1/vehiculos/{id}", vehiculo.getId())
                    .bodyValue(updateRequest)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "La orden fue preparada, pero no se pudo actualizar el estado del vehículo en flota"
            );
        }
    }

    private VehiculoTallerResponse mapToVehiculoTallerResponse(VehiculoFlotaResponse vehiculo) {
        return VehiculoTallerResponse.builder()
                .id(vehiculo.getId())
                .matricula(vehiculo.getMatricula())
                .tipo(vehiculo.getTipo())
                .capacidadKg(vehiculo.getCapacidadKg())
                .autonomiaKm(vehiculo.getAutonomiaKm())
                .estado(vehiculo.getEstado())
                .disponibleParaMantenimiento(!"EN_SERVICIO".equalsIgnoreCase(vehiculo.getEstado()))
                .build();
    }
}
