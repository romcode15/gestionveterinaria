package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.CambiarPasswordDTO;
import com.gestionvet.veterinariaapi.dto.LoginRequest;
import com.gestionvet.veterinariaapi.dto.LoginResponse;
import com.gestionvet.veterinariaapi.service.AuthService;
import com.gestionvet.veterinariaapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login y gestión de tokens JWT")
public class AuthController {

    @Autowired private AuthService    authService;
    @Autowired private UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Acepta username o email + contraseña. Retorna JWT válido por 24 horas."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso — retorna JWT"),
        @ApiResponse(responseCode = "400", description = "Credenciales incorrectas o usuario inactivo")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PatchMapping("/cambiar-password")
    @Operation(summary = "Cambiar contraseña del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Contraseña actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta")
    })
    public ResponseEntity<Void> cambiarPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CambiarPasswordDTO dto) {
        usuarioService.cambiarPassword(
                userDetails.getUsername(),
                dto.getPasswordActual(),
                dto.getPasswordNueva());
        return ResponseEntity.noContent().build();
    }
}
