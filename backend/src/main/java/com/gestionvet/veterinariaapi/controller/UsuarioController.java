package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.UsuarioDTO;
import com.gestionvet.veterinariaapi.service.UsuarioService;
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

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema — solo ADMIN")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")   // toda la clase queda restringida a ADMIN
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar usuarios paginado",
               description = "Soporta filtro por búsqueda (nombre, username, email) y por rol.")
    public ResponseEntity<Page<UsuarioDTO>> listarTodos(
            @Parameter(description = "Término de búsqueda")
            @RequestParam(required = false) String busqueda,
            @Parameter(description = "Filtrar por rol: admin, veterinario, recepcionista, cliente")
            @RequestParam(required = false) String rol,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "15")  int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("apellido", "nombre").ascending());
        return ResponseEntity.ok(usuarioService.listarPaginado(busqueda, rol, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario duplicado")
    })
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
