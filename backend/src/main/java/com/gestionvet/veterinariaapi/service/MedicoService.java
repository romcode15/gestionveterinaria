package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.MedicoDTO;
import com.gestionvet.veterinariaapi.entity.Especialidad;
import com.gestionvet.veterinariaapi.entity.Medico;
import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.EspecialidadRepository;
import com.gestionvet.veterinariaapi.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MedicoService {

    @Autowired private MedicoRepository medicoRepository;
    @Autowired private EspecialidadRepository especialidadRepository;
    @Autowired private UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public Page<MedicoDTO> listarTodos(Pageable pageable) {
        return medicoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public MedicoDTO buscarPorId(Integer id) {
        return toDTO(medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico", "id", id)));
    }

    @Transactional(readOnly = true)
    public Page<MedicoDTO> listarDisponibles(Pageable pageable) {
        return medicoRepository.findByDisponible(true, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<MedicoDTO> buscarPorNombre(String nombre, Pageable pageable) {
        return medicoRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(nombre, nombre, pageable)
                .map(this::toDTO);
    }

    public MedicoDTO crear(MedicoDTO dto) {
        if (medicoRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un médico con el email: " + dto.getEmail());
        }
        if (medicoRepository.findByNumeroLicencia(dto.getNumeroLicencia()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un médico con la licencia: " + dto.getNumeroLicencia());
        }
        Medico medico = toEntity(dto);
        medico = medicoRepository.save(medico);

        // Auto-crear usuario con rol 'veterinario' — username y password = numeroDocumento
        Usuario usuario = usuarioService.crearUsuarioAutomatico(
                dto.getNombre(), dto.getApellido(),
                dto.getEmail(), dto.getNumeroDocumento(), "veterinario");

        // Vincular bidireccionalmente
        usuario.setMedico(medico);
        medico.setUsuario(usuario);
        medicoRepository.save(medico);

        return toDTO(medico);
    }

    public MedicoDTO actualizar(Integer id, MedicoDTO dto) {
        Medico existente = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médico", "id", id));

        if (!existente.getEmail().equalsIgnoreCase(dto.getEmail())) {
            if (medicoRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Ya existe un médico con el email: " + dto.getEmail());
            }
        }

        existente.setTipoDocumento(dto.getTipoDocumento());
        existente.setNumeroDocumento(dto.getNumeroDocumento());
        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setEmail(dto.getEmail());
        existente.setTelefono(dto.getTelefono());
        existente.setNumeroLicencia(dto.getNumeroLicencia());
        if (dto.getDisponible() != null) existente.setDisponible(dto.getDisponible());
        if (dto.getEstado() != null) existente.setEstado(dto.getEstado());

        if (dto.getEspecialidadesIds() != null && !dto.getEspecialidadesIds().isEmpty()) {
            existente.setEspecialidades(resolverEspecialidades(dto.getEspecialidadesIds()));
        }

        return toDTO(medicoRepository.save(existente));
    }

    public void eliminar(Integer id) {
        if (!medicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Médico", "id", id);
        }
        medicoRepository.deleteById(id);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private Set<Especialidad> resolverEspecialidades(Set<Integer> ids) {
        Set<Especialidad> especialidades = new HashSet<>();
        for (Integer espId : ids) {
            Especialidad esp = especialidadRepository.findById(espId)
                    .orElseThrow(() -> new ResourceNotFoundException("Especialidad", "id", espId));
            especialidades.add(esp);
        }
        return especialidades;
    }

    // ── Conversiones ───────────────────────────────────────────────────────

    private MedicoDTO toDTO(Medico m) {
        MedicoDTO dto = new MedicoDTO();
        dto.setId(m.getId());
        dto.setTipoDocumento(m.getTipoDocumento());
        dto.setNumeroDocumento(m.getNumeroDocumento());
        dto.setNombre(m.getNombre());
        dto.setApellido(m.getApellido());
        dto.setEmail(m.getEmail());
        dto.setTelefono(m.getTelefono());
        dto.setNumeroLicencia(m.getNumeroLicencia());
        dto.setDisponible(m.getDisponible());
        dto.setEstado(m.getEstado());
        dto.setCreatedAt(m.getCreatedAt());
        dto.setEspecialidadesIds(
                m.getEspecialidades().stream().map(Especialidad::getId).collect(Collectors.toSet()));
        dto.setEspecialidadesNombres(
                m.getEspecialidades().stream().map(Especialidad::getNombre).collect(Collectors.toSet()));
        if (m.getUsuario() != null) {
            dto.setUsuarioId(m.getUsuario().getId());
            dto.setUsername(m.getUsuario().getUsername());
        }
        return dto;
    }

    private Medico toEntity(MedicoDTO dto) {
        Medico m = new Medico();
        m.setTipoDocumento(dto.getTipoDocumento());
        m.setNumeroDocumento(dto.getNumeroDocumento());
        m.setNombre(dto.getNombre());
        m.setApellido(dto.getApellido());
        m.setEmail(dto.getEmail());
        m.setTelefono(dto.getTelefono());
        m.setNumeroLicencia(dto.getNumeroLicencia());
        m.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        m.setEstado(dto.getEstado() != null ? dto.getEstado() : "activo");
        m.setEspecialidades(resolverEspecialidades(dto.getEspecialidadesIds()));
        return m;
    }
}
