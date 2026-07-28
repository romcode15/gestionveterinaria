package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.ClienteDTO;
import com.gestionvet.veterinariaapi.service.ClienteService;
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
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes de la veterinaria")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar clientes con filtros combinados opcionales",
               description = "Soporta busqueda (nombre/apellido/email/documento) y estado simultáneamente. " +
                             "Si no se envían parámetros, devuelve todos paginado.")
    @ApiResponse(responseCode = "200", description = "Página obtenida correctamente")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<ClienteDTO>> listarTodos(
            @Parameter(description = "Texto a buscar en nombre, apellido, email o documento")
            @RequestParam(required = false) String busqueda,
            @Parameter(description = "Filtrar por estado: activo | inactivo")
            @RequestParam(required = false) String estado,
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0")  int page,
            @Parameter(description = "Registros por página")       @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento")      @RequestParam(defaultValue = "apellido") String sort,
            @Parameter(description = "Dirección: asc o desc")      @RequestParam(defaultValue = "asc") String dir) {

        Pageable pageable = PageRequest.of(page, size,
                dir.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(clienteService.buscarConFiltros(busqueda, estado, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar clientes por nombre o apellido (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<ClienteDTO>> buscarPorNombre(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0")        int page,
            @RequestParam(defaultValue = "20")       int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("apellido").ascending());
        return ResponseEntity.ok(clienteService.buscarPorNombre(nombre, pageable));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar clientes por estado (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Page<ClienteDTO>> listarPorEstado(
            @PathVariable String estado,
            @RequestParam(defaultValue = "0")        int page,
            @RequestParam(defaultValue = "20")       int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("apellido").ascending());
        return ResponseEntity.ok(clienteService.listarPorEstado(estado, pageable));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo cliente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o duplicado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<ClienteDTO> crear(@Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<ClienteDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar cliente (borrado lógico)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cliente desactivado correctamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
