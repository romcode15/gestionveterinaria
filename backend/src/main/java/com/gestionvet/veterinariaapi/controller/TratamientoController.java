package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.TratamientoDTO;
import com.gestionvet.veterinariaapi.service.TratamientoService;
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

@RestController
@RequestMapping("/api/tratamientos")
@Tag(name = "Tratamientos", description = "Plan de tratamiento y medicamentos por diagnóstico")
@SecurityRequirement(name = "bearerAuth")
public class TratamientoController {

    @Autowired
    private TratamientoService service;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tratamiento por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<TratamientoDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/diagnostico/{diagnosticoId}")
    @Operation(summary = "Obtener tratamiento de un diagnóstico específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "Diagnóstico sin tratamiento aún")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<TratamientoDTO> buscarPorDiagnostico(@PathVariable Integer diagnosticoId) {
        return ResponseEntity.ok(service.buscarPorDiagnostico(diagnosticoId));
    }

    @PostMapping
    @Operation(summary = "Registrar plan de tratamiento",
               description = "Incluye todos los medicamentos con dosis, frecuencia y vía de administración. " +
                             "Un diagnóstico solo puede tener un tratamiento.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tratamiento creado"),
        @ApiResponse(responseCode = "400", description = "El diagnóstico ya tiene tratamiento"),
        @ApiResponse(responseCode = "404", description = "Diagnóstico o vía de administración no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public ResponseEntity<TratamientoDTO> crear(@Valid @RequestBody TratamientoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tratamiento y sus medicamentos",
               description = "Reemplaza todos los medicamentos del tratamiento con los nuevos enviados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public ResponseEntity<TratamientoDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody TratamientoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }
}
