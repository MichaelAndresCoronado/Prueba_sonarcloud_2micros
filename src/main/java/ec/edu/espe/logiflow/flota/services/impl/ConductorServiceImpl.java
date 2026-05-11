package ec.edu.espe.logiflow.flota.services.impl;

import ec.edu.espe.logiflow.flota.dto.request.ConductorCreateRequest;
import ec.edu.espe.logiflow.flota.dto.request.ConductorUpdateRequest;
import ec.edu.espe.logiflow.flota.dto.response.ConductorResponse;
import ec.edu.espe.logiflow.flota.models.Conductor;
import ec.edu.espe.logiflow.flota.repositories.ConductorRepository;
import ec.edu.espe.logiflow.flota.services.ConductorService;
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
public class ConductorServiceImpl implements ConductorService {

    private final ConductorRepository conductorRepository;

    @Override
    public ConductorResponse createConductor(ConductorCreateRequest request) {
        if (conductorRepository.existsByDni(request.getDni())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El DNI ya está registrado");
        }
        if (conductorRepository.existsByNumeroLicencia(request.getNumeroLicencia())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La licencia ya está registrada");
        }

        Conductor conductor = Conductor.builder()
                .dni(request.getDni())
                .nombreCompleto(request.getNombreCompleto())
                .numeroLicencia(request.getNumeroLicencia().toUpperCase()) // Forzamos mayúsculas
                .tipoLicencia(request.getTipoLicencia().toUpperCase())
                .build();

        conductor = conductorRepository.save(conductor);
        return mapToResponse(conductor);
    }

    @Override
    public ConductorResponse updateConductor(UUID id, ConductorUpdateRequest request) {
        Conductor conductor = conductorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conductor no encontrado"));

        // Actualizamos también el tipo de licencia
        conductor.setNombreCompleto(request.getNombreCompleto());
        conductor.setTipoLicencia(request.getTipoLicencia().toUpperCase());
        conductor.setActivo(request.getActivo());

        conductor = conductorRepository.save(conductor);
        return mapToResponse(conductor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConductorResponse> getAllConductores() {
        return conductorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ConductorResponse mapToResponse(Conductor conductor) {
        return ConductorResponse.builder()
                .id(conductor.getId())
                .dni(conductor.getDni())
                .nombreCompleto(conductor.getNombreCompleto())
                .numeroLicencia(conductor.getNumeroLicencia())
                .tipoLicencia(conductor.getTipoLicencia())
                .activo(conductor.getActivo())
                .createdAt(conductor.getCreatedAt())
                .build();
    }

    @Override
    public void deleteConductor(UUID id) {
        if (!conductorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conductor no encontrado con el ID proporcionado");
        }
        conductorRepository.deleteById(id);
    }
}