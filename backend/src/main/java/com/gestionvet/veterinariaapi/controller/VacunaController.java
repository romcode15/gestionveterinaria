package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.VacunaDTO;
import com.gestionvet.veterinariaapi.service.VacunaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacunas")
@Tag(name = "Vacunas", description = "Catálogo de vacunas disponibles")
@SecurityRequirement(name = "bearerAuth")
public class VacunaController {

    @Autowired
    private VacunaService service;

    @GetMapping
    @Operation(summary = "Listar todas las vacunas")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<List<VacunaDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar solo vacunas activas")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<List<VacunaDTO>> listarActivas() {
        return ResponseEntity.ok(service.listarActivas());
    }

    @GetMapping("/especie/{especieId}")
    @Operation(summary = "Listar vacunas aplicables a una especie",
               description = "Incluye vacunas específicas de la especie y las que aplican a todas")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<List<VacunaDTO>> listarPorEspecie(@PathVariable Integer especieId) {
        return ResponseEntity.ok(service.listarPorEspecie(especieId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar vacuna por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrada"),
        @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<VacunaDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva vacuna en el catálogo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Nombre duplicado o datos inválidos")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VacunaDTO> crear(@Valid @RequestBody VacunaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vacuna")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VacunaDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody VacunaDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar vacuna (borrado lógico)",
               description = "No elimina el historial de aplicaciones existentes")
    @ApiResponse(responseCode = "204", description = "Desactivada correctamente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivar(@PathVariable Integer id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
