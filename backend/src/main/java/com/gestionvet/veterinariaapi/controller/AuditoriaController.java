package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.AuditoriaDTO;
import com.gestionvet.veterinariaapi.service.AuditoriaService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/auditoria")
@Tag(name = "Auditoría", description = "Registro de actividad del sistema — solo ADMIN")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    @Autowired
    private AuditoriaService service;

    @GetMapping
    @Operation(summary = "Listar toda la auditoría paginada",
               description = "Ordenada de más reciente a más antigua.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida")
    public ResponseEntity<Page<AuditoriaDTO>> listarTodas(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.listarTodas(pageable));
    }

    @GetMapping("/usuario/{username}")
    @Operation(summary = "Auditoría por usuario")
    public ResponseEntity<Page<AuditoriaDTO>> porUsuario(
            @PathVariable String username,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.porUsuario(username, pageable));
    }

    @GetMapping("/entidad/{entidad}")
    @Operation(summary = "Auditoría por entidad",
               description = "Valores: Cliente, Cita, Diagnostico, Tratamiento, MascotaVacuna, etc.")
    public ResponseEntity<Page<AuditoriaDTO>> porEntidad(
            @PathVariable String entidad,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.porEntidad(entidad, pageable));
    }

    @GetMapping("/accion/{accion}")
    @Operation(summary = "Auditoría por acción",
               description = "Valores: CREATE, UPDATE, DELETE, LOGIN, SALIDA_INVENTARIO")
    public ResponseEntity<Page<AuditoriaDTO>> porAccion(
            @PathVariable String accion,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.porAccion(accion, pageable));
    }

    @GetMapping("/rango")
    @Operation(summary = "Auditoría en rango de fechas",
               description = "Formato de fecha: yyyy-MM-dd")
    public ResponseEntity<Page<AuditoriaDTO>> porRango(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.porRangoFecha(
                inicio.atStartOfDay(), fin.atTime(LocalTime.MAX), pageable));
    }

    @GetMapping("/errores")
    @Operation(summary = "Listar solo operaciones fallidas",
               description = "Útil para detectar intentos de acceso no autorizado o errores recurrentes.")
    public ResponseEntity<Page<AuditoriaDTO>> errores(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.errores(pageable));
    }
}
