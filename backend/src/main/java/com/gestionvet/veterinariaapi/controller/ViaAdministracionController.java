package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.ViaAdministracionDTO;
import com.gestionvet.veterinariaapi.service.ViaAdministracionService;
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
@RequestMapping("/api/vias-administracion")
@Tag(name = "Vías de Administración", description = "Catálogo de vías de administración de medicamentos")
@SecurityRequirement(name = "bearerAuth")
public class ViaAdministracionController {

    @Autowired
    private ViaAdministracionService service;

    @GetMapping
    @Operation(summary = "Listar todas las vías de administración")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<List<ViaAdministracionDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar vía de administración por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrada"),
        @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<ViaAdministracionDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva vía de administración")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o duplicado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ViaAdministracionDTO> crear(@Valid @RequestBody ViaAdministracionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vía de administración")
    @ApiResponse(responseCode = "200", description = "Actualizada correctamente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ViaAdministracionDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ViaAdministracionDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vía de administración")
    @ApiResponse(responseCode = "204", description = "Eliminada correctamente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
