package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.MascotaDTO;
import com.gestionvet.veterinariaapi.service.MascotaService;
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
@RequestMapping("/api/mascotas")
@Tag(name = "Mascotas", description = "Gestión de mascotas (pacientes)")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    // GET /api/mascotas
    @GetMapping
    @Operation(summary = "Listar todas las mascotas")
    public ResponseEntity<List<MascotaDTO>> listarTodas() {
        return ResponseEntity.ok(mascotaService.listarTodas());
    }

    // GET /api/mascotas/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar mascota por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mascota encontrada"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<MascotaDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(mascotaService.buscarPorId(id));
    }

    // GET /api/mascotas/cliente/{clienteId}
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar mascotas por cliente propietario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mascotas encontradas"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<List<MascotaDTO>> buscarPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(mascotaService.buscarPorCliente(clienteId));
    }

    // GET /api/mascotas/buscar?nombre=Max
    @GetMapping("/buscar")
    @Operation(summary = "Buscar mascotas por nombre")
    public ResponseEntity<List<MascotaDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(mascotaService.buscarPorNombre(nombre));
    }

    // POST /api/mascotas
    @PostMapping
    @Operation(summary = "Registrar nueva mascota")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mascota creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente, especie o raza no encontrada")
    })
    public ResponseEntity<MascotaDTO> crear(@Valid @RequestBody MascotaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaService.crear(dto));
    }

    // PUT /api/mascotas/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mascota existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mascota actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<MascotaDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody MascotaDTO dto) {
        return ResponseEntity.ok(mascotaService.actualizar(id, dto));
    }

    // DELETE /api/mascotas/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Mascota eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
