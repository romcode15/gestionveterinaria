package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.MedicoDTO;
import com.gestionvet.veterinariaapi.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@Tag(name = "Médicos", description = "Gestión del equipo médico veterinario")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    // GET /api/medicos
    @GetMapping
    @Operation(summary = "Listar todos los médicos")
    public ResponseEntity<List<MedicoDTO>> listarTodos() {
        return ResponseEntity.ok(medicoService.listarTodos());
    }

    // GET /api/medicos/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar médico por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Médico encontrado"),
        @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    })
    public ResponseEntity<MedicoDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(medicoService.buscarPorId(id));
    }

    // GET /api/medicos/disponibles
    @GetMapping("/disponibles")
    @Operation(summary = "Listar médicos disponibles para citas")
    public ResponseEntity<List<MedicoDTO>> listarDisponibles() {
        return ResponseEntity.ok(medicoService.listarDisponibles());
    }

    // GET /api/medicos/buscar?nombre=Garcia
    @GetMapping("/buscar")
    @Operation(summary = "Buscar médicos por nombre o apellido")
    public ResponseEntity<List<MedicoDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(medicoService.buscarPorNombre(nombre));
    }

    // POST /api/medicos
    @PostMapping
    @Operation(summary = "Registrar nuevo médico")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Médico creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o médico duplicado")
    })
    public ResponseEntity<MedicoDTO> crear(@Valid @RequestBody MedicoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicoService.crear(dto));
    }

    // PUT /api/medicos/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar médico existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Médico actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    })
    public ResponseEntity<MedicoDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MedicoDTO dto) {
        return ResponseEntity.ok(medicoService.actualizar(id, dto));
    }

    // DELETE /api/medicos/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar médico")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Médico eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Médico no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        medicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
