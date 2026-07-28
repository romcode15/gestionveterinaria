package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.MascotaDTO;
import com.gestionvet.veterinariaapi.service.MascotaService;
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
@RequestMapping("/api/mascotas")
@Tag(name = "Mascotas", description = "Gestión de mascotas (pacientes)")
@SecurityRequirement(name = "bearerAuth")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    @Operation(summary = "Listar mascotas con filtros combinados opcionales",
               description = "busqueda (nombre/propietario), especieId y estado se pueden combinar simultáneamente.")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<MascotaDTO>> listarTodas(
            @Parameter(description = "Texto en nombre o propietario") @RequestParam(required = false) String busqueda,
            @Parameter(description = "ID de especie")                 @RequestParam(required = false) Integer especieId,
            @Parameter(description = "Estado: activo|fallecido|transferido") @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0")      int page,
            @RequestParam(defaultValue = "10")     int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(defaultValue = "asc")    String dir) {

        Pageable pageable = PageRequest.of(page, size,
                dir.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(mascotaService.buscarConFiltros(busqueda, especieId, estado, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar mascota por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mascota encontrada"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<MascotaDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(mascotaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar mascotas por cliente (paginado)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mascotas encontradas"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<MascotaDTO>> buscarPorCliente(
            @PathVariable Integer clienteId,
            @RequestParam(defaultValue = "0")        int page,
            @RequestParam(defaultValue = "20")       int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return ResponseEntity.ok(mascotaService.buscarPorCliente(clienteId, pageable));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar mascotas por nombre (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<MascotaDTO>> buscarPorNombre(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0")        int page,
            @RequestParam(defaultValue = "20")       int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return ResponseEntity.ok(mascotaService.buscarPorNombre(nombre, pageable));
    }

    @PostMapping
    @Operation(summary = "Registrar nueva mascota")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mascota creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente, especie o raza no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<MascotaDTO> crear(@Valid @RequestBody MascotaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mascota existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mascota actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public ResponseEntity<MascotaDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MascotaDTO dto) {
        return ResponseEntity.ok(mascotaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Mascota eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
