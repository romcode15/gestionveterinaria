package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.*;
import com.gestionvet.veterinariaapi.service.PortalClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portal/cliente")
@Tag(name = "Portal Cliente", description = "Endpoints exclusivos para usuarios con rol CLIENTE — solo ven sus propios datos")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('CLIENTE')")
public class PortalClienteController {

    @Autowired
    private PortalClienteService service;

    @GetMapping("/perfil")
    @Operation(summary = "Ver mi perfil de cliente")
    @ApiResponse(responseCode = "200", description = "Perfil obtenido")
    public ResponseEntity<ClienteDTO> miPerfil() {
        return ResponseEntity.ok(service.miPerfil());
    }

    @GetMapping("/mascotas")
    @Operation(summary = "Ver mis mascotas",
               description = "Solo devuelve las mascotas registradas a nombre del cliente autenticado.")
    public ResponseEntity<Page<MascotaDTO>> misMascotas(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return ResponseEntity.ok(service.misMascotas(pageable));
    }

    @GetMapping("/citas")
    @Operation(summary = "Ver mis citas",
               description = "Solo devuelve las citas del cliente autenticado, ordenadas de más reciente a más antigua.")
    public ResponseEntity<Page<CitaDTO>> misCitas(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        return ResponseEntity.ok(service.misCitas(pageable));
    }

    @GetMapping("/mascotas/{mascotaId}/historial")
    @Operation(summary = "Ver historial clínico de una de mis mascotas",
               description = "Devuelve todas las consultas con diagnóstico y tratamiento. " +
                             "Solo funciona con mascotas que pertenecen al cliente autenticado.")
    @ApiResponse(responseCode = "400", description = "La mascota no te pertenece")
    @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    public ResponseEntity<HistorialClinicoDTO> historialMascota(
            @PathVariable Integer mascotaId) {
        return ResponseEntity.ok(service.historialMascota(mascotaId));
    }

    @GetMapping("/mascotas/{mascotaId}/vacunas")
    @Operation(summary = "Ver vacunas de una de mis mascotas")
    @ApiResponse(responseCode = "400", description = "La mascota no te pertenece")
    public ResponseEntity<Page<MascotaVacunaDTO>> vacunasMascota(
            @PathVariable Integer mascotaId,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Registros por página")       @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaAplicacion").descending());
        return ResponseEntity.ok(service.vacunasMascota(mascotaId, pageable));
    }
}
