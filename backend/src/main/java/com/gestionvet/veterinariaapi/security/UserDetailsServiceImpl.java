package com.gestionvet.veterinariaapi.security;

import com.gestionvet.veterinariaapi.entity.Permiso;
import com.gestionvet.veterinariaapi.entity.Rol;
import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga el usuario por username para Spring Security.
     * Convierte los roles y permisos en GrantedAuthority:
     *   - Roles  → "ROLE_ADMIN", "ROLE_VETERINARIO", etc.
     *   - Permisos → "clientes.ver", "citas.crear", etc.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        // Intentar por username primero, luego por email
        Usuario usuario = usuarioRepository.findByUsername(usernameOrEmail)
                .or(() -> usuarioRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + usernameOrEmail));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + usernameOrEmail);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Rol rol : usuario.getRoles()) {
            // Agregar el rol con prefijo ROLE_ (convención de Spring Security)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre().toUpperCase()));

            // Agregar cada permiso del rol como authority individual
            for (Permiso permiso : rol.getPermisos()) {
                authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
            }
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }
}
