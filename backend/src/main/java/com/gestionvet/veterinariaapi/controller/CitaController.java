package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.CitaDTO;
import com.gestionvet.veterinariaapi.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "Gestión de citas veterinarias")
public class CitaController {

    @Autowired
    private CitaService citaService;

    // GET /api/citas
    @GetMapping
    @Operation(summary = "Listar todas las citas")
    public ResponseEntity<List<CitaDTO>> listarTodas() {
        return ResponseEntity.ok(citaService.listarTodas());
    }

    // GET /api/citas/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cita por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cita encontrada"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    public ResponseEntity<CitaDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(citaService.buscarPorId(id));
    }

    // GET /api/citas/fecha?fecha=2026-06-22
    @GetMapping("/fecha")
    @Operation(summary = "Listar citas por fecha específica")
    public ResponseEntity<List<CitaDTO>> buscarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.buscarPorFecha(fecha));
    }

    // GET /api/citas/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar citas por cliente")
    public ResponseEntity<List<CitaDTO>> buscarPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(citaService.buscarPorCliente(clienteId));
    }

    // GET /api/citas/medico/{medicoId}
    @GetMapping("/medico/{medicoId}")
    @Operation(summary = "Listar citas por médico")
    public ResponseEntity<List<CitaDTO>> buscarPorMedico(@PathVariable Integer medicoId) {
        return ResponseEntity.ok(citaService.buscarPorMedico(medicoId));
    }

    // GET /api/citas/estado/{estado}
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar citas por estado")
    public ResponseEntity<List<CitaDTO>> buscarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(citaService.buscarPorEstado(estado));
    }

    // POST /api/citas
    @PostMapping
    @Operation(summary = "Registrar nueva cita")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cita creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Médico, mascota o cliente no encontrado")
    })
    public ResponseEntity<CitaDTO> crear(@Valid @RequestBody CitaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crear(dto));
    }

    // PUT /api/citas/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cita existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cita actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    public ResponseEntity<CitaDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CitaDTO dto) {
        return ResponseEntity.ok(citaService.actualizar(id, dto));
    }

    // PATCH /api/citas/{id}/estado
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de una cita",
               description = "Estados válidos: pendiente, confirmada, en_curso, completada, cancelada, no_asistio")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Estado inválido"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    public ResponseEntity<CitaDTO> cambiarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String estado = body.get("estado");
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El campo 'estado' es obligatorio");
        }
        return ResponseEntity.ok(citaService.cambiarEstado(id, estado));
    }

    // DELETE /api/citas/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cita")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cita eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
