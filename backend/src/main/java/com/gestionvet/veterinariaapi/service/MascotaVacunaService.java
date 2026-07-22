package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.AlertaVacunaDTO;
import com.gestionvet.veterinariaapi.dto.AlertaVacunaDTO.ItemAlertaDTO;
import com.gestionvet.veterinariaapi.dto.MascotaVacunaDTO;
import com.gestionvet.veterinariaapi.entity.*;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MascotaVacunaService {

    private static final Logger log = LoggerFactory.getLogger(MascotaVacunaService.class);

    @Autowired private MascotaVacunaRepository mascotaVacunaRepository;
    @Autowired private MascotaRepository       mascotaRepository;
    @Autowired private VacunaRepository        vacunaRepository;
    @Autowired private MedicoRepository        medicoRepository;
    @Autowired private CitaRepository          citaRepository;

    // ── Historial de vacunas de una mascota (paginado) ─────────────────────

    @Transactional(readOnly = true)
    public Page<MascotaVacunaDTO> historialPorMascota(Integer mascotaId, Pageable pageable) {
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new ResourceNotFoundException("Mascota", "id", mascotaId);
        }
        return mascotaVacunaRepository.findByMascotaId(mascotaId, pageable).map(this::toDTO);
    }

    // ── Buscar por ID ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public MascotaVacunaDTO buscarPorId(Integer id) {
        return toDTO(mascotaVacunaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MascotaVacuna", "id", id)));
    }

    // ── Registrar vacuna aplicada ──────────────────────────────────────────

    @Transactional(rollbackFor = Exception.class)
    public MascotaVacunaDTO registrar(MascotaVacunaDTO dto) {

        Mascota mascota = mascotaRepository.findById(dto.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", dto.getMascotaId()));

        if (!"activo".equals(mascota.getEstado())) {
            throw new IllegalArgumentException(
                    "La mascota '" + mascota.getNombre() + "' no está activa.");
        }

        Vacuna vacuna = vacunaRepository.findById(dto.getVacunaId())
                .orElseThrow(() -> new ResourceNotFoundException("Vacuna", "id", dto.getVacunaId()));

        if (!vacuna.getActiva()) {
            throw new IllegalArgumentException(
                    "La vacuna '" + vacuna.getNombre() + "' está desactivada.");
        }

        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Medico", "id", dto.getMedicoId()));

        MascotaVacuna mv = new MascotaVacuna();
        mv.setMascota(mascota);
        mv.setVacuna(vacuna);
        mv.setMedico(medico);
        mv.setFechaAplicacion(dto.getFechaAplicacion());
        mv.setLoteVacuna(dto.getLoteVacuna());
        mv.setObservaciones(dto.getObservaciones());
        mv.setEstado("vigente");

        // Cita opcional
        if (dto.getCitaId() != null) {
            Cita cita = citaRepository.findById(dto.getCitaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", dto.getCitaId()));
            mv.setCita(cita);
        }

        // Calcular próxima dosis
        // Si el usuario envió fecha explícita, se usa esa; si no, se calcula con el intervalo
        if (dto.getFechaProximaDosis() != null) {
            mv.setFechaProximaDosis(dto.getFechaProximaDosis());
        } else if (vacuna.getIntervaloDiasRevacunacion() != null) {
            mv.setFechaProximaDosis(
                    dto.getFechaAplicacion().plusDays(vacuna.getIntervaloDiasRevacunacion()));
        }

        MascotaVacuna saved = mascotaVacunaRepository.save(mv);
        log.info("Vacuna registrada: mascotaId={}, vacuna='{}', proximaDosis={}",
                mascota.getId(), vacuna.getNombre(), saved.getFechaProximaDosis());
        return toDTO(saved);
    }

    // ── Actualizar registro (corrección de datos) ──────────────────────────

    public MascotaVacunaDTO actualizar(Integer id, MascotaVacunaDTO dto) {
        MascotaVacuna existente = mascotaVacunaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MascotaVacuna", "id", id));

        existente.setFechaAplicacion(dto.getFechaAplicacion());
        existente.setFechaProximaDosis(dto.getFechaProximaDosis());
        existente.setLoteVacuna(dto.getLoteVacuna());
        existente.setObservaciones(dto.getObservaciones());
        if (dto.getEstado() != null) existente.setEstado(dto.getEstado());

        return toDTO(mascotaVacunaRepository.save(existente));
    }

    // ── Alertas del dashboard ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AlertaVacunaDTO obtenerAlertas(int diasVentana) {
        LocalDate hoy   = LocalDate.now();
        LocalDate hasta = hoy.plusDays(diasVentana);

        List<MascotaVacuna> proximas = mascotaVacunaRepository.alertasProximasDosis(hoy, hasta);
        List<MascotaVacuna> vencidas = mascotaVacunaRepository.vacunasVencidas(hoy);

        // Marcar automáticamente como vencidas en BD las que ya pasaron
        vencidas.forEach(mv -> {
            if ("vigente".equals(mv.getEstado())) {
                mv.setEstado("vencida");
                mascotaVacunaRepository.save(mv);
            }
        });

        AlertaVacunaDTO alerta = new AlertaVacunaDTO();
        alerta.setDiasConsultados(diasVentana);
        alerta.setTotalProximas(proximas.size());
        alerta.setTotalVencidas(vencidas.size());
        alerta.setProximas(proximas.stream().map(mv -> toItemAlerta(mv, hoy)).collect(Collectors.toList()));
        alerta.setVencidas(vencidas.stream().map(mv -> toItemAlerta(mv, hoy)).collect(Collectors.toList()));

        return alerta;
    }

    // ── Conversiones ───────────────────────────────────────────────────────

    private MascotaVacunaDTO toDTO(MascotaVacuna mv) {
        MascotaVacunaDTO dto = new MascotaVacunaDTO();
        dto.setId(mv.getId());
        dto.setMascotaId(mv.getMascota().getId());
        dto.setMascotaNombre(mv.getMascota().getNombre());
        dto.setClienteNombre(mv.getMascota().getCliente().getNombre()
                + " " + mv.getMascota().getCliente().getApellido());
        dto.setVacunaId(mv.getVacuna().getId());
        dto.setVacunaNombre(mv.getVacuna().getNombre());
        dto.setMedicoId(mv.getMedico().getId());
        dto.setMedicoNombre(mv.getMedico().getNombre() + " " + mv.getMedico().getApellido());
        dto.setCitaId(mv.getCita() != null ? mv.getCita().getId() : null);
        dto.setFechaAplicacion(mv.getFechaAplicacion());
        dto.setFechaProximaDosis(mv.getFechaProximaDosis());
        dto.setLoteVacuna(mv.getLoteVacuna());
        dto.setObservaciones(mv.getObservaciones());
        dto.setEstado(mv.getEstado());
        dto.setCreatedAt(mv.getCreatedAt());

        // Calcular días restantes
        if (mv.getFechaProximaDosis() != null) {
            long dias = ChronoUnit.DAYS.between(LocalDate.now(), mv.getFechaProximaDosis());
            dto.setDiasParaProximaDosis(dias);
        }
        return dto;
    }

    private ItemAlertaDTO toItemAlerta(MascotaVacuna mv, LocalDate hoy) {
        ItemAlertaDTO item = new ItemAlertaDTO();
        item.setMascotaVacunaId(mv.getId());
        item.setMascotaId(mv.getMascota().getId());
        item.setMascotaNombre(mv.getMascota().getNombre());
        item.setEspecieNombre(mv.getMascota().getEspecie().getNombre());
        item.setClienteNombre(mv.getMascota().getCliente().getNombre()
                + " " + mv.getMascota().getCliente().getApellido());
        item.setClienteTelefono(mv.getMascota().getCliente().getTelefono());
        item.setVacunaNombre(mv.getVacuna().getNombre());
        item.setFechaAplicacion(mv.getFechaAplicacion());
        item.setFechaProximaDosis(mv.getFechaProximaDosis());
        item.setDiasRestantes(ChronoUnit.DAYS.between(hoy, mv.getFechaProximaDosis()));
        return item;
    }
}
