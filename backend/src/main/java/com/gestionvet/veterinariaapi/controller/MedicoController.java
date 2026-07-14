package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.MedicoDTO;
import com.gestionvet.veterinariaapi.service.MedicoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicos")
@Tag(name = "Médicos", description = "Gestión del equipo médico veterinario")
@SecurityRequirement(name = "bearerAuth")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    @Operation(summary = "Listar médicos paginados",
               description = "Parámetros: page (0-based), size (default 20), sort (campo,asc|desc)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<MedicoDTO>> listarTodos(
            @RequestParam(defaultValue = "0")        int page,
            @RequestParam(defaultValue = "20")       int size,
            @RequestParam(defaultValue = "apellido") String sort,
            @RequestParam(defaultValue = "asc")      String dir) {

        Pageable pageable = PageRequest.of(page, size,
                dir.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(medicoService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar médico por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Médico encontrado"),
        @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<MedicoDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(medicoService.buscarPorId(id));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar médicos disponibles (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<MedicoDTO>> listarDisponibles(
            @RequestParam(defaultValue = "0")        int page,
            @RequestParam(defaultValue = "20")       int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("apellido").ascending());
        return ResponseEntity.ok(medicoService.listarDisponibles(pageable));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar médicos por nombre o apellido (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<MedicoDTO>> buscarPorNombre(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0")        int page,
            @RequestParam(defaultValue = "20")       int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("apellido").ascending());
        return ResponseEntity.ok(medicoService.buscarPorNombre(nombre, pageable));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo médico")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Médico creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o médico duplicado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicoDTO> crear(@Valid @RequestBody MedicoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicoService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar médico existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Médico actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicoDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MedicoDTO dto) {
        return ResponseEntity.ok(medicoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar médico")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Médico eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        medicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
