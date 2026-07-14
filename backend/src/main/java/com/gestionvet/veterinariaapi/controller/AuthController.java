package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.LoginRequest;
import com.gestionvet.veterinariaapi.dto.LoginResponse;
import com.gestionvet.veterinariaapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login y gestión de tokens JWT")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * POST /api/auth/login
     * Ruta pública — no requiere token.
     * Retorna JWT + datos del usuario autenticado.
     *
     * Body de ejemplo:
     * {
     *   "username": "admin",
     *   "password": "admin123"
     * }
     */
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica usuario con username y contraseña. " +
                      "Retorna un token JWT válido por 24 horas. " +
                      "Usar el token en el header: Authorization: Bearer <token>"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso — retorna JWT"),
        @ApiResponse(responseCode = "400", description = "Username o contraseña incorrectos"),
        @ApiResponse(responseCode = "400", description = "Usuario inactivo")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
