package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.DiagnosticoDTO;
import com.gestionvet.veterinariaapi.service.DiagnosticoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diagnosticos")
@Tag(name = "Diagnósticos", description = "Registro clínico de diagnósticos por consulta")
@SecurityRequirement(name = "bearerAuth")
public class DiagnosticoController {

    @Autowired
    private DiagnosticoService service;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar diagnóstico por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Diagnóstico encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<DiagnosticoDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/cita/{citaId}")
    @Operation(summary = "Obtener diagnóstico de una cita específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Diagnóstico encontrado"),
        @ApiResponse(responseCode = "404", description = "Cita sin diagnóstico")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<DiagnosticoDTO> buscarPorCita(@PathVariable Integer citaId) {
        return ResponseEntity.ok(service.buscarPorCita(citaId));
    }

    @GetMapping("/mascota/{mascotaId}")
    @Operation(summary = "Historial de diagnósticos de una mascota (paginado)",
               description = "Ordenado de más reciente a más antiguo")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<DiagnosticoDTO>> porMascota(
            @PathVariable Integer mascotaId,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Registros por página")       @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("cita.fecha").descending());
        return ResponseEntity.ok(service.historialPorMascota(mascotaId, pageable));
    }

    @GetMapping("/medico/{medicoId}")
    @Operation(summary = "Diagnósticos registrados por un médico (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public ResponseEntity<Page<DiagnosticoDTO>> porMedico(
            @PathVariable Integer medicoId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.porMedico(medicoId, pageable));
    }

    @PostMapping
    @Operation(summary = "Registrar diagnóstico de una consulta",
               description = "Solo se puede registrar si la cita está en estado 'en_curso' o 'completada'. " +
                             "Al registrar, la cita pasa automáticamente a 'completada'.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Diagnóstico creado"),
        @ApiResponse(responseCode = "400", description = "La cita ya tiene diagnóstico o estado inválido"),
        @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public ResponseEntity<DiagnosticoDTO> crear(@Valid @RequestBody DiagnosticoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar diagnóstico existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public ResponseEntity<DiagnosticoDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody DiagnosticoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }
}
