package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.*;
import com.gestionvet.veterinariaapi.service.PortalMedicoService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/portal/medico")
@Tag(name = "Portal Médico", description = "Endpoints exclusivos para usuarios con rol VETERINARIO — vista personalizada del médico autenticado")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('VETERINARIO')")
public class PortalMedicoController {

    @Autowired
    private PortalMedicoService service;

    @GetMapping("/perfil")
    @Operation(summary = "Ver mi perfil como médico",
               description = "Devuelve los datos del registro médico vinculado al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Perfil obtenido")
    public ResponseEntity<MedicoDTO> miPerfil() {
        return ResponseEntity.ok(service.miPerfil());
    }

    @GetMapping("/citas")
    @Operation(summary = "Ver mis citas (todas)",
               description = "Solo devuelve las citas asignadas al médico autenticado, ordenadas de más reciente a más antigua.")
    public ResponseEntity<Page<CitaDTO>> misCitas(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        return ResponseEntity.ok(service.misCitas(pageable));
    }

    @GetMapping("/citas/hoy")
    @Operation(summary = "Ver mis citas del día de hoy",
               description = "Agenda del día actual del médico autenticado, ordenada por hora de inicio.")
    public ResponseEntity<Page<CitaDTO>> misCitasHoy(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "50") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("horaInicio").ascending());
        return ResponseEntity.ok(service.misCitasHoy(pageable));
    }

    @GetMapping("/diagnosticos")
    @Operation(summary = "Ver los diagnósticos que he registrado",
               description = "Solo devuelve diagnósticos registrados por el médico autenticado.")
    public ResponseEntity<Page<DiagnosticoDTO>> misDiagnosticos(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.misDiagnosticos(pageable));
    }
}
