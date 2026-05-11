package ec.edu.espe.logiflow.taller.restdos.controller;

import ec.edu.espe.logiflow.taller.restdos.dto.request.OrdenMantenimientoRequest;
import ec.edu.espe.logiflow.taller.restdos.dto.response.OrdenMantenimientoResponse;
import ec.edu.espe.logiflow.taller.restdos.dto.response.VehiculoTallerResponse;
import ec.edu.espe.logiflow.taller.restdos.services.TallerRestDosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/taller")
@RequiredArgsConstructor
@Tag(name = "Taller RestDos", description = "Operaciones REST para integración con taller externo")
public class TallerRestDosController {

    private final TallerRestDosService tallerRestDosService;

    @GetMapping("/vehiculos/{matricula}")
    @Operation(
            summary = "Consultar vehículo por matrícula",
            description = "Consulta en ms-flota-rest los datos del vehículo y los devuelve en formato para taller externo."
    )
    public VehiculoTallerResponse consultarVehiculo(@PathVariable String matricula) {
        return tallerRestDosService.consultarVehiculo(matricula);
    }

    @PostMapping("/ordenes-mantenimiento")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Registrar orden de mantenimiento",
            description = "Registra una incidencia de mantenimiento y actualiza el vehículo en flota a estado MANTENIMIENTO."
    )
    public OrdenMantenimientoResponse registrarOrdenMantenimiento(
            @Valid @RequestBody OrdenMantenimientoRequest request
    ) {
        return tallerRestDosService.registrarOrdenMantenimiento(request);
    }
}
