package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.RecepcionistaDTO;
import com.gestionvet.veterinariaapi.entity.Recepcionista;
import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.RecepcionistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecepcionistaService {

    @Autowired private RecepcionistaRepository recepcionistaRepository;
    @Autowired private UsuarioService           usuarioService;

    // ── Listar todos (paginado) ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<RecepcionistaDTO> listarTodos(Pageable pageable) {
        return recepcionistaRepository.findAll(pageable).map(this::toDTO);
    }

    // ── Buscar por ID ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public RecepcionistaDTO buscarPorId(Integer id) {
        return toDTO(recepcionistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepcionista", "id", id)));
    }

    // ── Buscar por nombre ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<RecepcionistaDTO> buscarPorNombre(String nombre, Pageable pageable) {
        return recepcionistaRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(nombre, nombre, pageable)
                .map(this::toDTO);
    }

    // ── Crear ─────────────────────────────────────────────────────────────

    public RecepcionistaDTO crear(RecepcionistaDTO dto) {
        if (recepcionistaRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe una recepcionista con el email: " + dto.getEmail());
        }

        Recepcionista recepcionista = toEntity(dto);
        recepcionista = recepcionistaRepository.save(recepcionista);

        // Auto-crear usuario con rol 'recepcionista' — se necesita un identificador único
        // Como recepcionista no tiene numero_documento, usamos el email como username
        Usuario usuario = usuarioService.crearUsuarioAutomatico(
                dto.getNombre(), dto.getApellido(),
                dto.getEmail(), dto.getEmail(), "recepcionista");

        usuario.setRecepcionista(recepcionista);
        recepcionistaRepository.save(recepcionista);

        return toDTO(recepcionista);
    }

    // ── Actualizar ────────────────────────────────────────────────────────

    public RecepcionistaDTO actualizar(Integer id, RecepcionistaDTO dto) {
        Recepcionista existente = recepcionistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepcionista", "id", id));

        if (!existente.getEmail().equalsIgnoreCase(dto.getEmail())) {
            if (recepcionistaRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Ya existe una recepcionista con el email: " + dto.getEmail());
            }
        }

        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setEmail(dto.getEmail());
        existente.setTelefono(dto.getTelefono());
        if (dto.getEstado() != null) existente.setEstado(dto.getEstado());

        return toDTO(recepcionistaRepository.save(existente));
    }

    // ── Eliminar (borrado lógico) ─────────────────────────────────────────

    public void eliminar(Integer id) {
        Recepcionista recepcionista = recepcionistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepcionista", "id", id));
        recepcionista.setEstado("inactivo");
        recepcionistaRepository.save(recepcionista);
    }

    // ── Conversiones ──────────────────────────────────────────────────────

    private RecepcionistaDTO toDTO(Recepcionista r) {
        RecepcionistaDTO dto = new RecepcionistaDTO();
        dto.setId(r.getId());
        dto.setNombre(r.getNombre());
        dto.setApellido(r.getApellido());
        dto.setEmail(r.getEmail());
        dto.setTelefono(r.getTelefono());
        dto.setEstado(r.getEstado());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

    private Recepcionista toEntity(RecepcionistaDTO dto) {
        Recepcionista r = new Recepcionista();
        r.setNombre(dto.getNombre());
        r.setApellido(dto.getApellido());
        r.setEmail(dto.getEmail());
        r.setTelefono(dto.getTelefono());
        r.setEstado(dto.getEstado() != null ? dto.getEstado() : "activo");
        return r;
    }
}
