package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.UsuarioDTO;
import com.gestionvet.veterinariaapi.entity.Rol;
import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.ClienteRepository;
import com.gestionvet.veterinariaapi.repository.MedicoRepository;
import com.gestionvet.veterinariaapi.repository.RolRepository;
import com.gestionvet.veterinariaapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private MedicoRepository  medicoRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Integer id) {
        return toDTO(usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id)));
    }

    public UsuarioDTO crear(UsuarioDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con el username: " + dto.getUsername());
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        Usuario usuario = toEntity(dto);
        return toDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO actualizar(Integer id, UsuarioDTO dto) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Validar username único si cambió
        if (!existente.getUsername().equalsIgnoreCase(dto.getUsername())) {
            if (usuarioRepository.existsByUsername(dto.getUsername())) {
                throw new IllegalArgumentException("Ya existe un usuario con el username: " + dto.getUsername());
            }
        }
        // Validar email único si cambió
        if (!existente.getEmail().equalsIgnoreCase(dto.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Ya existe un usuario con el email: " + dto.getEmail());
            }
        }

        existente.setUsername(dto.getUsername());
        existente.setEmail(dto.getEmail());
        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        if (dto.getActivo() != null) existente.setActivo(dto.getActivo());

        // Solo actualizar password si viene en el DTO — hashear con BCrypt
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Actualizar roles si vienen
        if (dto.getRolesIds() != null && !dto.getRolesIds().isEmpty()) {
            existente.setRoles(resolverRoles(dto.getRolesIds()));
        }

        // Actualizar clienteId si viene
        if (dto.getClienteId() != null) {
            existente.setCliente(clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", dto.getClienteId())));
        } else {
            existente.setCliente(null);
        }

        // Actualizar medicoId si viene
        if (dto.getMedicoId() != null) {
            existente.setMedico(medicoRepository.findById(dto.getMedicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medico", "id", dto.getMedicoId())));
        } else {
            existente.setMedico(null);
        }

        return toDTO(usuarioRepository.save(existente));
    }

    public void eliminar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }
        usuarioRepository.deleteById(id);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private Set<Rol> resolverRoles(Set<Integer> ids) {
        Set<Rol> roles = new HashSet<>();
        for (Integer rolId : ids) {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));
            roles.add(rol);
        }
        return roles;
    }

    // ── Conversiones ───────────────────────────────────────────────────────

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        // No se expone la contraseña
        dto.setEmail(u.getEmail());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setActivo(u.getActivo());
        dto.setUltimoAcceso(u.getUltimoAcceso());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setClienteId(u.getCliente() != null ? u.getCliente().getId() : null);
        dto.setMedicoId(u.getMedico()  != null ? u.getMedico().getId()  : null);
        dto.setRolesIds(u.getRoles().stream().map(Rol::getId).collect(Collectors.toSet()));
        dto.setRolesNombres(u.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet()));
        return dto;
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Usuario u = new Usuario();
        u.setUsername(dto.getUsername());
        // Hashear contraseña con BCrypt — NUNCA guardar en texto plano
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setEmail(dto.getEmail());
        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        u.setRoles(resolverRoles(dto.getRolesIds()));
        if (dto.getClienteId() != null) {
            u.setCliente(clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", dto.getClienteId())));
        }
        if (dto.getMedicoId() != null) {
            u.setMedico(medicoRepository.findById(dto.getMedicoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medico", "id", dto.getMedicoId())));
        }
        return u;
    }
}
