package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.ClienteDTO;
import com.gestionvet.veterinariaapi.service.ClienteService;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes de la veterinaria")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // GET /api/clientes
    @GetMapping
    @Operation(summary = "Listar todos los clientes")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida correctamente")
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    // GET /api/clientes/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    // GET /api/clientes/buscar?nombre=Ana
    @GetMapping("/buscar")
    @Operation(summary = "Buscar clientes por nombre o apellido")
    public ResponseEntity<List<ClienteDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(clienteService.buscarPorNombre(nombre));
    }

    // GET /api/clientes/estado/{estado}
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar clientes por estado (activo/inactivo)")
    public ResponseEntity<List<ClienteDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(clienteService.listarPorEstado(estado));
    }

    // POST /api/clientes
    @PostMapping
    @Operation(summary = "Registrar nuevo cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o cliente duplicado")
    })
    public ResponseEntity<ClienteDTO> crear(@Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(dto));
    }

    // PUT /api/clientes/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ClienteDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }

    // DELETE /api/clientes/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cliente eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
