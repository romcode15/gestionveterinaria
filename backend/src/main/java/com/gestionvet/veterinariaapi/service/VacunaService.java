package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.VacunaDTO;
import com.gestionvet.veterinariaapi.entity.Especie;
import com.gestionvet.veterinariaapi.entity.Vacuna;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.EspecieRepository;
import com.gestionvet.veterinariaapi.repository.VacunaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VacunaService {

    private static final Logger log = LoggerFactory.getLogger(VacunaService.class);

    @Autowired private VacunaRepository  vacunaRepository;
    @Autowired private EspecieRepository especieRepository;

    @Transactional(readOnly = true)
    public List<VacunaDTO> listarTodas() {
        return vacunaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VacunaDTO> listarActivas() {
        return vacunaRepository.findByActiva(true).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VacunaDTO> listarPorEspecie(Integer especieId) {
        return vacunaRepository.findByEspecieIdOrEspecieIsNull(especieId)
                .stream().filter(v -> v.getActiva()).map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VacunaDTO buscarPorId(Integer id) {
        return toDTO(vacunaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna", "id", id)));
    }

    public VacunaDTO crear(VacunaDTO dto) {
        if (vacunaRepository.findByNombreIgnoreCase(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una vacuna con el nombre: " + dto.getNombre());
        }
        Vacuna vacuna = toEntity(dto);
        Vacuna saved = vacunaRepository.save(vacuna);
        log.info("Vacuna creada: id={}, nombre={}", saved.getId(), saved.getNombre());
        return toDTO(saved);
    }

    public VacunaDTO actualizar(Integer id, VacunaDTO dto) {
        Vacuna existente = vacunaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna", "id", id));
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setIntervaloDiasRevacunacion(dto.getIntervaloDiasRevacunacion());
        if (dto.getActiva() != null) existente.setActiva(dto.getActiva());
        if (dto.getEspecieId() != null) {
            existente.setEspecie(especieRepository.findById(dto.getEspecieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Especie", "id", dto.getEspecieId())));
        } else {
            existente.setEspecie(null);
        }
        return toDTO(vacunaRepository.save(existente));
    }

    // Borrado lógico: desactivar vacuna
    public void desactivar(Integer id) {
        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna", "id", id));
        vacuna.setActiva(false);
        vacunaRepository.save(vacuna);
        log.info("Vacuna desactivada: id={}", id);
    }

    // ── Conversión ─────────────────────────────────────────────────────────

    private VacunaDTO toDTO(Vacuna v) {
        VacunaDTO dto = new VacunaDTO();
        dto.setId(v.getId());
        dto.setNombre(v.getNombre());
        dto.setDescripcion(v.getDescripcion());
        dto.setIntervaloDiasRevacunacion(v.getIntervaloDiasRevacunacion());
        dto.setActiva(v.getActiva());
        dto.setCreatedAt(v.getCreatedAt());
        if (v.getEspecie() != null) {
            dto.setEspecieId(v.getEspecie().getId());
            dto.setEspecieNombre(v.getEspecie().getNombre());
        }
        return dto;
    }

    private Vacuna toEntity(VacunaDTO dto) {
        Vacuna v = new Vacuna();
        v.setNombre(dto.getNombre());
        v.setDescripcion(dto.getDescripcion());
        v.setIntervaloDiasRevacunacion(dto.getIntervaloDiasRevacunacion());
        v.setActiva(dto.getActiva() != null ? dto.getActiva() : true);
        if (dto.getEspecieId() != null) {
            v.setEspecie(especieRepository.findById(dto.getEspecieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Especie", "id", dto.getEspecieId())));
        }
        return v;
    }
}
