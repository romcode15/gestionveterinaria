package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.RecepcionistaDTO;
import com.gestionvet.veterinariaapi.service.RecepcionistaService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/recepcionistas")
@Tag(name = "Recepcionistas", description = "Gestión del personal de recepción")
@SecurityRequirement(name = "bearerAuth")
public class RecepcionistaController {

    @Autowired
    private RecepcionistaService recepcionistaService;

    @GetMapping
    @Operation(summary = "Listar recepcionistas paginados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<RecepcionistaDTO>> listarTodos(
            @RequestParam(defaultValue = "0")       int page,
            @RequestParam(defaultValue = "20")      int size,
            @RequestParam(defaultValue = "apellido") String sort,
            @RequestParam(defaultValue = "asc")     String dir) {

        Pageable pageable = PageRequest.of(page, size,
                dir.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(recepcionistaService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar recepcionista por ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecepcionistaDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(recepcionistaService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar recepcionistas por nombre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<RecepcionistaDTO>> buscarPorNombre(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("apellido").ascending());
        return ResponseEntity.ok(recepcionistaService.buscarPorNombre(nombre, pageable));
    }

    @PostMapping
    @Operation(summary = "Crear recepcionista y su usuario de acceso automáticamente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecepcionistaDTO> crear(@Valid @RequestBody RecepcionistaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recepcionistaService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar recepcionista")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecepcionistaDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody RecepcionistaDTO dto) {
        return ResponseEntity.ok(recepcionistaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar recepcionista (borrado lógico)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        recepcionistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
