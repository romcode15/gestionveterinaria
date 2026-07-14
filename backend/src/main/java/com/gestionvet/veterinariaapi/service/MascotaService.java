package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.MascotaDTO;
import com.gestionvet.veterinariaapi.entity.*;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MascotaService {

    @Autowired private MascotaRepository mascotaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private EspecieRepository especieRepository;
    @Autowired private RazaRepository razaRepository;

    @Transactional(readOnly = true)
    public Page<MascotaDTO> listarTodas(Pageable pageable) {
        return mascotaRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public MascotaDTO buscarPorId(Integer id) {
        return toDTO(mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", id)));
    }

    @Transactional(readOnly = true)
    public Page<MascotaDTO> buscarPorCliente(Integer clienteId, Pageable pageable) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente", "id", clienteId);
        }
        return mascotaRepository.findByClienteId(clienteId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<MascotaDTO> buscarPorNombre(String nombre, Pageable pageable) {
        return mascotaRepository.findByNombreContainingIgnoreCase(nombre, pageable).map(this::toDTO);
    }

    public MascotaDTO crear(MascotaDTO dto) {
        Mascota mascota = toEntity(dto);
        return toDTO(mascotaRepository.save(mascota));
    }

    public MascotaDTO actualizar(Integer id, MascotaDTO dto) {
        Mascota existente = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", id));

        existente.setNombre(dto.getNombre());
        existente.setEspecie(especieRepository.findById(dto.getEspecieId())
                .orElseThrow(() -> new ResourceNotFoundException("Especie", "id", dto.getEspecieId())));
        existente.setRaza(razaRepository.findById(dto.getRazaId())
                .orElseThrow(() -> new ResourceNotFoundException("Raza", "id", dto.getRazaId())));
        existente.setSexo(dto.getSexo());
        existente.setFechaNacimiento(dto.getFechaNacimiento());
        existente.setColor(dto.getColor());
        existente.setPeso(dto.getPeso());
        existente.setMicrochip(dto.getMicrochip());
        existente.setEsterilizado(dto.getEsterilizado() != null ? dto.getEsterilizado() : false);
        if (dto.getEstado() != null) existente.setEstado(dto.getEstado());
        existente.setObservaciones(dto.getObservaciones());
        existente.setCliente(clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", dto.getClienteId())));

        return toDTO(mascotaRepository.save(existente));
    }

    public void eliminar(Integer id) {
        if (!mascotaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mascota", "id", id);
        }
        mascotaRepository.deleteById(id);
    }

    // ── Conversiones ───────────────────────────────────────────────────────

    private MascotaDTO toDTO(Mascota m) {
        MascotaDTO dto = new MascotaDTO();
        dto.setId(m.getId());
        dto.setNombre(m.getNombre());
        dto.setEspecieId(m.getEspecie().getId());
        dto.setEspecieNombre(m.getEspecie().getNombre());
        dto.setRazaId(m.getRaza().getId());
        dto.setRazaNombre(m.getRaza().getNombre());
        dto.setSexo(m.getSexo());
        dto.setFechaNacimiento(m.getFechaNacimiento());
        dto.setColor(m.getColor());
        dto.setPeso(m.getPeso());
        dto.setMicrochip(m.getMicrochip());
        dto.setEsterilizado(m.getEsterilizado());
        dto.setEstado(m.getEstado());
        dto.setClienteId(m.getCliente().getId());
        dto.setClienteNombre(m.getCliente().getNombre() + " " + m.getCliente().getApellido());
        dto.setObservaciones(m.getObservaciones());
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }

    private Mascota toEntity(MascotaDTO dto) {
        Mascota m = new Mascota();
        m.setNombre(dto.getNombre());
        m.setEspecie(especieRepository.findById(dto.getEspecieId())
                .orElseThrow(() -> new ResourceNotFoundException("Especie", "id", dto.getEspecieId())));
        m.setRaza(razaRepository.findById(dto.getRazaId())
                .orElseThrow(() -> new ResourceNotFoundException("Raza", "id", dto.getRazaId())));
        m.setSexo(dto.getSexo());
        m.setFechaNacimiento(dto.getFechaNacimiento());
        m.setColor(dto.getColor());
        m.setPeso(dto.getPeso());
        m.setMicrochip(dto.getMicrochip());
        m.setEsterilizado(dto.getEsterilizado() != null ? dto.getEsterilizado() : false);
        m.setEstado(dto.getEstado() != null ? dto.getEstado() : "activo");
        m.setObservaciones(dto.getObservaciones());
        m.setCliente(clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", dto.getClienteId())));
        return m;
    }
}
