package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.LoginRequest;
import com.gestionvet.veterinariaapi.dto.LoginResponse;
import com.gestionvet.veterinariaapi.entity.Rol;
import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.repository.UsuarioRepository;
import com.gestionvet.veterinariaapi.security.JwtUtil;
import com.gestionvet.veterinariaapi.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Autenticar usuario y retornar JWT.
     * Flujo:
     *  1. AuthenticationManager verifica username + password con BCrypt
     *  2. Si es válido, genera el JWT
     *  3. Actualiza ultimo_acceso en la BD
     *  4. Retorna LoginResponse con token + datos del usuario
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {

        try {
            // 1. Autenticar — lanza excepción si credenciales incorrectas
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 2. Cargar UserDetails para generar el token
            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            // 3. Generar JWT con roles incluidos en claims
            String token = jwtUtil.generateToken(userDetails);

            // 4. Actualizar ultimo_acceso en la BD
            Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);

            // 5. Construir respuesta con datos del usuario
            return new LoginResponse(
                    token,
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getEmail(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getRoles().stream()
                            .map(Rol::getNombre)
                            .collect(Collectors.toSet())
            );

        } catch (DisabledException e) {
            throw new IllegalArgumentException("Usuario inactivo. Contacte al administrador.");
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Username o contraseña incorrectos.");
        }
    }
}
