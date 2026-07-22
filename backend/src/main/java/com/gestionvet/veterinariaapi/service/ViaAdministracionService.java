package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.ViaAdministracionDTO;
import com.gestionvet.veterinariaapi.entity.ViaAdministracion;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.ViaAdministracionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ViaAdministracionService {

    private static final Logger log = LoggerFactory.getLogger(ViaAdministracionService.class);

    @Autowired
    private ViaAdministracionRepository repository;

    @Transactional(readOnly = true)
    public List<ViaAdministracionDTO> listarTodas() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ViaAdministracionDTO buscarPorId(Integer id) {
        return toDTO(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ViaAdministracion", "id", id)));
    }

    public ViaAdministracionDTO crear(ViaAdministracionDTO dto) {
        if (repository.findByNombreIgnoreCase(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una vía de administración con el nombre: " + dto.getNombre());
        }
        ViaAdministracion entity = new ViaAdministracion();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        ViaAdministracion saved = repository.save(entity);
        log.info("ViaAdministracion creada: id={}, nombre={}", saved.getId(), saved.getNombre());
        return toDTO(saved);
    }

    public ViaAdministracionDTO actualizar(Integer id, ViaAdministracionDTO dto) {
        ViaAdministracion existente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ViaAdministracion", "id", id));
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        return toDTO(repository.save(existente));
    }

    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("ViaAdministracion", "id", id);
        }
        repository.deleteById(id);
        log.info("ViaAdministracion eliminada: id={}", id);
    }

    private ViaAdministracionDTO toDTO(ViaAdministracion e) {
        ViaAdministracionDTO dto = new ViaAdministracionDTO();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setDescripcion(e.getDescripcion());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }
}
