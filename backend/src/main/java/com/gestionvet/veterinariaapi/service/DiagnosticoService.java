package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.DiagnosticoDTO;
import com.gestionvet.veterinariaapi.entity.Cita;
import com.gestionvet.veterinariaapi.entity.Diagnostico;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.CitaRepository;
import com.gestionvet.veterinariaapi.repository.DiagnosticoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DiagnosticoService {

    private static final Logger log = LoggerFactory.getLogger(DiagnosticoService.class);

    @Autowired private DiagnosticoRepository diagnosticoRepository;
    @Autowired private CitaRepository        citaRepository;

    // ── Buscar por ID ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public DiagnosticoDTO buscarPorId(Integer id) {
        return toDTO(diagnosticoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnostico", "id", id)));
    }

    // ── Buscar por cita ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public DiagnosticoDTO buscarPorCita(Integer citaId) {
        return toDTO(diagnosticoRepository.findByCitaId(citaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Diagnostico", "citaId", citaId)));
    }

    // ── Historial clínico de una mascota (paginado) ────────────────────────

    @Transactional(readOnly = true)
    public Page<DiagnosticoDTO> historialPorMascota(Integer mascotaId, Pageable pageable) {
        return diagnosticoRepository.findByMascotaId(mascotaId, pageable).map(this::toDTO);
    }

    // ── Diagnósticos de un médico (paginado) ──────────────────────────────

    @Transactional(readOnly = true)
    public Page<DiagnosticoDTO> porMedico(Integer medicoId, Pageable pageable) {
        return diagnosticoRepository.findByMedicoId(medicoId, pageable).map(this::toDTO);
    }

    // ── Crear diagnóstico ─────────────────────────────────────────────────

    @Transactional(rollbackFor = Exception.class)
    public DiagnosticoDTO crear(DiagnosticoDTO dto) {

        // Validar que la cita existe
        Cita cita = citaRepository.findById(dto.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", dto.getCitaId()));

        // Una cita solo puede tener un diagnóstico
        if (diagnosticoRepository.existsByCitaId(cita.getId())) {
            throw new IllegalArgumentException(
                    "La cita #" + cita.getId() + " ya tiene un diagnóstico registrado.");
        }

        // La cita debe estar en estado completada o en_curso
        if (!"completada".equals(cita.getEstado()) && !"en_curso".equals(cita.getEstado())) {
            throw new IllegalArgumentException(
                    "Solo se puede registrar diagnóstico en citas con estado 'en_curso' o 'completada'. " +
                    "Estado actual: " + cita.getEstado());
        }

        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setCita(cita);
        diagnostico.setMascota(cita.getMascota());
        diagnostico.setMedico(cita.getMedico());
        diagnostico.setSintomas(dto.getSintomas());
        diagnostico.setDiagnostico(dto.getDiagnostico());
        diagnostico.setPronostico(dto.getPronostico());
        diagnostico.setPesoConsulta(dto.getPesoConsulta());
        diagnostico.setTemperatura(dto.getTemperatura());
        diagnostico.setObservaciones(dto.getObservaciones());

        // Marcar la cita como completada al registrar diagnóstico
        if (!"completada".equals(cita.getEstado())) {
            cita.setEstado("completada");
            citaRepository.save(cita);
        }

        Diagnostico saved = diagnosticoRepository.save(diagnostico);
        log.info("Diagnostico creado: id={}, citaId={}, mascotaId={}",
                saved.getId(), cita.getId(), cita.getMascota().getId());
        return toDTO(saved);
    }

    // ── Actualizar diagnóstico ─────────────────────────────────────────────

    public DiagnosticoDTO actualizar(Integer id, DiagnosticoDTO dto) {
        Diagnostico existente = diagnosticoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnostico", "id", id));

        existente.setSintomas(dto.getSintomas());
        existente.setDiagnostico(dto.getDiagnostico());
        existente.setPronostico(dto.getPronostico());
        existente.setPesoConsulta(dto.getPesoConsulta());
        existente.setTemperatura(dto.getTemperatura());
        existente.setObservaciones(dto.getObservaciones());

        return toDTO(diagnosticoRepository.save(existente));
    }

    // ── Conversión ─────────────────────────────────────────────────────────

    private DiagnosticoDTO toDTO(Diagnostico d) {
        DiagnosticoDTO dto = new DiagnosticoDTO();
        dto.setId(d.getId());
        dto.setCitaId(d.getCita().getId());
        dto.setCitaFecha(d.getCita().getFecha().toString());
        dto.setMascotaId(d.getMascota().getId());
        dto.setMascotaNombre(d.getMascota().getNombre());
        dto.setMedicoId(d.getMedico().getId());
        dto.setMedicoNombre(d.getMedico().getNombre() + " " + d.getMedico().getApellido());
        dto.setSintomas(d.getSintomas());
        dto.setDiagnostico(d.getDiagnostico());
        dto.setPronostico(d.getPronostico());
        dto.setPesoConsulta(d.getPesoConsulta());
        dto.setTemperatura(d.getTemperatura());
        dto.setObservaciones(d.getObservaciones());
        dto.setCreatedAt(d.getCreatedAt());
        dto.setUpdatedAt(d.getUpdatedAt());
        return dto;
    }
}
