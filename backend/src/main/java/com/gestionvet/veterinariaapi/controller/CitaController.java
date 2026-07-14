package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.CitaDTO;
import com.gestionvet.veterinariaapi.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "Gestión de citas veterinarias")
@SecurityRequirement(name = "bearerAuth")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @GetMapping
    @Operation(summary = "Listar citas paginadas",
               description = "Parámetros: page (0-based), size (default 20), sort (campo,asc|desc)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<CitaDTO>> listarTodas(
            @RequestParam(defaultValue = "0")     int page,
            @RequestParam(defaultValue = "20")    int size,
            @RequestParam(defaultValue = "fecha") String sort,
            @RequestParam(defaultValue = "desc")  String dir) {

        Pageable pageable = PageRequest.of(page, size,
                dir.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(citaService.listarTodas(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cita por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cita encontrada"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<CitaDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(citaService.buscarPorId(id));
    }

    @GetMapping("/fecha")
    @Operation(summary = "Listar citas por fecha (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<CitaDTO>> buscarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(defaultValue = "0")     int page,
            @RequestParam(defaultValue = "20")    int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("horaInicio").ascending());
        return ResponseEntity.ok(citaService.buscarPorFecha(fecha, pageable));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar citas por cliente (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<CitaDTO>> buscarPorCliente(
            @PathVariable Integer clienteId,
            @RequestParam(defaultValue = "0")     int page,
            @RequestParam(defaultValue = "20")    int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        return ResponseEntity.ok(citaService.buscarPorCliente(clienteId, pageable));
    }

    @GetMapping("/medico/{medicoId}")
    @Operation(summary = "Listar citas por médico (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<CitaDTO>> buscarPorMedico(
            @PathVariable Integer medicoId,
            @RequestParam(defaultValue = "0")     int page,
            @RequestParam(defaultValue = "20")    int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        return ResponseEntity.ok(citaService.buscarPorMedico(medicoId, pageable));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar citas por estado (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<CitaDTO>> buscarPorEstado(
            @PathVariable String estado,
            @RequestParam(defaultValue = "0")     int page,
            @RequestParam(defaultValue = "20")    int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        return ResponseEntity.ok(citaService.buscarPorEstado(estado, pageable));
    }

    @PostMapping
    @Operation(summary = "Registrar nueva cita")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cita creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Médico, mascota o cliente no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<CitaDTO> crear(@Valid @RequestBody CitaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cita existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cita actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<CitaDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CitaDTO dto) {
        return ResponseEntity.ok(citaService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/estado")
    @Operation(
        summary = "Cambiar estado de una cita",
        description = "Estados válidos: pendiente, confirmada, en_curso, completada, cancelada, no_asistio"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Estado inválido"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<CitaDTO> cambiarEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String estado = body.get("estado");
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El campo 'estado' es obligatorio");
        }
        return ResponseEntity.ok(citaService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cita")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cita eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
