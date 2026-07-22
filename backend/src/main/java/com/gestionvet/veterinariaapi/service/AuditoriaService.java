package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.AuditoriaDTO;
import com.gestionvet.veterinariaapi.entity.AuditoriaGeneral;
import com.gestionvet.veterinariaapi.repository.AuditoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    /**
     * Registra un evento de auditoría de forma asíncrona.
     * Usa Propagation.REQUIRES_NEW para que se guarde incluso si
     * la transacción principal hace rollback (ej: error en el servicio).
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String username, Integer usuarioId, String accion,
                          String entidad, String entidadId, String descripcion,
                          String ipOrigen, String endpoint,
                          boolean exitoso, String errorMensaje) {
        try {
            AuditoriaGeneral auditoria = new AuditoriaGeneral(
                    username, usuarioId, accion, entidad, entidadId,
                    descripcion, ipOrigen, endpoint, exitoso, errorMensaje);
            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            // La auditoría nunca debe romper el flujo principal
            log.error("Error al registrar auditoría: accion={}, entidad={}, error={}",
                    accion, entidad, e.getMessage());
        }
    }

    // ── Consultas (solo ADMIN) ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<AuditoriaDTO> listarTodas(Pageable pageable) {
        return auditoriaRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AuditoriaDTO> porUsuario(String username, Pageable pageable) {
        return auditoriaRepository.findByUsername(username, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AuditoriaDTO> porEntidad(String entidad, Pageable pageable) {
        return auditoriaRepository.findByEntidad(entidad, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AuditoriaDTO> porAccion(String accion, Pageable pageable) {
        return auditoriaRepository.findByAccion(accion, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AuditoriaDTO> porRangoFecha(LocalDateTime inicio, LocalDateTime fin, Pageable pageable) {
        return auditoriaRepository.findByCreatedAtBetween(inicio, fin, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<AuditoriaDTO> errores(Pageable pageable) {
        return auditoriaRepository.findByExitoso(false, pageable).map(this::toDTO);
    }

    // ── Conversión ─────────────────────────────────────────────────────────

    private AuditoriaDTO toDTO(AuditoriaGeneral a) {
        AuditoriaDTO dto = new AuditoriaDTO();
        dto.setId(a.getId());
        dto.setUsuarioId(a.getUsuarioId());
        dto.setUsername(a.getUsername());
        dto.setAccion(a.getAccion());
        dto.setEntidad(a.getEntidad());
        dto.setEntidadId(a.getEntidadId());
        dto.setDescripcion(a.getDescripcion());
        dto.setIpOrigen(a.getIpOrigen());
        dto.setEndpoint(a.getEndpoint());
        dto.setExitoso(a.getExitoso());
        dto.setErrorMensaje(a.getErrorMensaje());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}
