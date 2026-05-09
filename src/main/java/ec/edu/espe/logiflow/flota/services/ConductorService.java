package ec.edu.espe.logiflow.flota.services;

import ec.edu.espe.logiflow.flota.dto.request.ConductorCreateRequest;
import ec.edu.espe.logiflow.flota.dto.request.ConductorUpdateRequest;
import ec.edu.espe.logiflow.flota.dto.response.ConductorResponse;
import java.util.List;
import java.util.UUID;

public interface ConductorService {
    ConductorResponse createConductor(ConductorCreateRequest request);
    ConductorResponse updateConductor(UUID id, ConductorUpdateRequest request);
    List<ConductorResponse> getAllConductores();
    void deleteConductor(UUID id);
}