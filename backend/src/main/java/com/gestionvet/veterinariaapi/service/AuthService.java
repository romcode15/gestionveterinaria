package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.LoginRequest;
import com.gestionvet.veterinariaapi.dto.LoginResponse;
import com.gestionvet.veterinariaapi.dto.PermisoDTO;
import com.gestionvet.veterinariaapi.dto.RolDTO;
import com.gestionvet.veterinariaapi.entity.Permiso;
import com.gestionvet.veterinariaapi.entity.Rol;
import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.repository.UsuarioRepository;
import com.gestionvet.veterinariaapi.security.JwtUtil;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

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
     *  4. Retorna LoginResponse con token + datos del usuario + roles con PERMISOS
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

            // 4. 🔥 Buscar usuario con roles Y PERMISOS precargados (EntityGraph)
            // userDetails.getUsername() devuelve el username real (Spring Security lo normaliza)
            Usuario usuario = usuarioRepository
                    .findByUsernameWithRolesAndPermisos(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // 5. Actualizar ultimo_acceso en la BD
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);

            // 6. 🔥 Construir lista de RolDTO con sus permisos
            List<RolDTO> rolesDTO = usuario.getRoles().stream().map(rol -> {
                RolDTO rolDTO = new RolDTO();
                rolDTO.setId(rol.getId());
                rolDTO.setNombre(rol.getNombre().toLowerCase());
                rolDTO.setDescripcion(rol.getDescripcion());

                // Mapear permisos a PermisoDTO
                List<PermisoDTO> permisosDTO = rol.getPermisos().stream().map(permiso -> {
                    PermisoDTO pDTO = new PermisoDTO();
                    pDTO.setId(permiso.getId());
                    pDTO.setNombre(permiso.getNombre());
                    pDTO.setDescripcion(permiso.getDescripcion());
                    pDTO.setModulo(permiso.getModulo());
                    return pDTO;
                }).collect(Collectors.toList());

                rolDTO.setPermisos(permisosDTO);
                return rolDTO;
            }).collect(Collectors.toList());

            // 7. Construir respuesta con datos del usuario + roles con permisos
            LoginResponse response = new LoginResponse(
                    token,
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getEmail(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    rolesDTO
            );

            // 8. Incluir IDs de vinculación (cliente, médico, recepcionista)
            if (usuario.getCliente() != null) {
                response.setClienteId(usuario.getCliente().getId());
            }
            if (usuario.getMedico() != null) {
                response.setMedicoId(usuario.getMedico().getId());
            }
            if (usuario.getRecepcionista() != null) {
                response.setRecepcionistaId(usuario.getRecepcionista().getId());
            }

            return response;

        } catch (DisabledException e) {
            throw new IllegalArgumentException("Usuario inactivo. Contacte al administrador.");
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Username o contraseña incorrectos.");
        }
    }
}
