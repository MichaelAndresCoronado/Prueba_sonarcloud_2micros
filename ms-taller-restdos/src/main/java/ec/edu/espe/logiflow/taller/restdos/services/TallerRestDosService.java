package ec.edu.espe.logiflow.taller.restdos.services;

import ec.edu.espe.logiflow.taller.restdos.dto.request.OrdenMantenimientoRequest;
import ec.edu.espe.logiflow.taller.restdos.dto.response.OrdenMantenimientoResponse;
import ec.edu.espe.logiflow.taller.restdos.dto.response.VehiculoTallerResponse;

public interface TallerRestDosService {
    VehiculoTallerResponse consultarVehiculo(String matricula);
    OrdenMantenimientoResponse registrarOrdenMantenimiento(OrdenMantenimientoRequest request);
}
