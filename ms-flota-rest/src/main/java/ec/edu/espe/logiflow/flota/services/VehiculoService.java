package ec.edu.espe.logiflow.flota.services;

import ec.edu.espe.logiflow.flota.dto.request.VehiculoCreateRequest;
import ec.edu.espe.logiflow.flota.dto.request.VehiculoUpdateRequest;
import ec.edu.espe.logiflow.flota.dto.response.VehiculoResponse;
import java.util.List;
import java.util.UUID;

public interface VehiculoService {
    VehiculoResponse createVehiculo(VehiculoCreateRequest request);
    VehiculoResponse updateVehiculo(UUID id, VehiculoUpdateRequest request);
    List<VehiculoResponse> getAllVehiculos();
    List<VehiculoResponse> getVehiculosDisponibles(); // Para Ruteo
    void deleteVehiculo(UUID id);

}