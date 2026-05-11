package ec.edu.espe.logiflow.flota.services.impl;

import ec.edu.espe.logiflow.flota.dto.request.VehiculoCreateRequest;
import ec.edu.espe.logiflow.flota.dto.request.VehiculoUpdateRequest;
import ec.edu.espe.logiflow.flota.dto.response.VehiculoResponse;
import ec.edu.espe.logiflow.flota.enums.EstadoVehiculo;
import ec.edu.espe.logiflow.flota.models.Vehiculo;
import ec.edu.espe.logiflow.flota.repositories.VehiculoRepository;
import ec.edu.espe.logiflow.flota.services.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    @Override
    public VehiculoResponse createVehiculo(VehiculoCreateRequest request) {
        if (vehiculoRepository.existsByMatricula(request.getMatricula())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula ya registrada");
        }

        Vehiculo vehiculo = Vehiculo.builder()
                .matricula(request.getMatricula().toUpperCase())
                .tipo(request.getTipo())
                .capacidadKg(request.getCapacidadKg())
                .autonomiaKm(request.getAutonomiaKm())
                .build();

        vehiculo = vehiculoRepository.save(vehiculo);
        return mapToResponse(vehiculo);
    }

    @Override
    public VehiculoResponse updateVehiculo(UUID id, VehiculoUpdateRequest request) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado"));

        vehiculo.setEstado(request.getEstado());
        vehiculo.setCapacidadKg(request.getCapacidadKg());
        vehiculo.setAutonomiaKm(request.getAutonomiaKm());

        vehiculo = vehiculoRepository.save(vehiculo);
        return mapToResponse(vehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoResponse> getAllVehiculos() {
        return vehiculoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoResponse> getVehiculosDisponibles() {
        return vehiculoRepository.findByEstado(EstadoVehiculo.DISPONIBLE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private VehiculoResponse mapToResponse(Vehiculo vehiculo) {
        return VehiculoResponse.builder()
                .id(vehiculo.getId())
                .matricula(vehiculo.getMatricula())
                .tipo(vehiculo.getTipo())
                .capacidadKg(vehiculo.getCapacidadKg())
                .autonomiaKm(vehiculo.getAutonomiaKm())
                .estado(vehiculo.getEstado())
                .createdAt(vehiculo.getCreatedAt())
                .build();
    }

    @Override
    public void deleteVehiculo(UUID id) {
        if (!vehiculoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado con el ID proporcionado");
        }
        vehiculoRepository.deleteById(id);
    }
}