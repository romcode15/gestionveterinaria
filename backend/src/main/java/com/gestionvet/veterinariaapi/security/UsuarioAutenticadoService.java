package com.gestionvet.veterinariaapi.security;

import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Helper para obtener el Usuario completo (con sus relaciones)
 * desde el contexto de seguridad en cualquier servicio.
 */
@Service
public class UsuarioAutenticadoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Devuelve el Usuario autenticado con cliente y medico cargados.
     * Lanza excepción si no hay sesión activa o el usuario no existe.
     */
    public Usuario getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        String username = auth.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
    }

    public String getUsernameActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonimo";
    }
}
