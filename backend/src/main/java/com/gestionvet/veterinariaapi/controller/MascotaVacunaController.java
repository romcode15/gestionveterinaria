package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.AlertaVacunaDTO;
import com.gestionvet.veterinariaapi.dto.MascotaVacunaDTO;
import com.gestionvet.veterinariaapi.service.MascotaVacunaService;
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
@RequestMapping("/api/mascota-vacunas")
@Tag(name = "Vacunación de Mascotas", description = "Registro y seguimiento de vacunas aplicadas")
@SecurityRequirement(name = "bearerAuth")
public class MascotaVacunaController {

    @Autowired
    private MascotaVacunaService service;

    @GetMapping
    @Operation(summary = "Listar registros de vacunación paginados",
               description = "Soporta filtros opcionales: medicoId, estado. Para el rol VETERINARIO pasar su medicoId.")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<MascotaVacunaDTO>> listar(
            @RequestParam(required = false) Integer medicoId,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaAplicacion").descending());
        return ResponseEntity.ok(service.listar(medicoId, estado, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar registro de vacuna por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<MascotaVacunaDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/mascota/{mascotaId}")
    @Operation(summary = "Historial de vacunas de una mascota (paginado)",
               description = "Ordenado de más reciente a más antiguo")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<MascotaVacunaDTO>> historialPorMascota(
            @PathVariable Integer mascotaId,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Registros por página")       @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaAplicacion").descending());
        return ResponseEntity.ok(service.historialPorMascota(mascotaId, pageable));
    }

    @GetMapping("/alertas")
    @Operation(summary = "Alertas de vacunación para el dashboard",
               description = "Devuelve vacunas próximas a vencer y ya vencidas. " +
                             "El parámetro 'dias' define la ventana de búsqueda (default: 30 días).")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<AlertaVacunaDTO> alertas(
            @Parameter(description = "Días hacia adelante para buscar vacunas próximas")
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(service.obtenerAlertas(dias));
    }

    @PostMapping
    @Operation(summary = "Registrar vacuna aplicada a una mascota",
               description = "Si la vacuna tiene intervalo configurado y no se envía fechaProximaDosis, " +
                             "se calcula automáticamente.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vacuna registrada correctamente"),
        @ApiResponse(responseCode = "400", description = "Mascota inactiva, vacuna desactivada o datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Mascota, vacuna o médico no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public ResponseEntity<MascotaVacunaDTO> registrar(@Valid @RequestBody MascotaVacunaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro de vacuna",
               description = "Permite corregir fechas, lote u observaciones")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public ResponseEntity<MascotaVacunaDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MascotaVacunaDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }
}
