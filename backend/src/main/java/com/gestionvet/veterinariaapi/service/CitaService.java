package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.CitaDTO;
import com.gestionvet.veterinariaapi.entity.*;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitaService {

    @Autowired private CitaRepository citaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private MascotaRepository mascotaRepository;
    @Autowired private MedicoRepository medicoRepository;
    @Autowired private TipoCitaRepository tipoCitaRepository;

    @Transactional(readOnly = true)
    public List<CitaDTO> listarTodas() {
        return citaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CitaDTO buscarPorId(Integer id) {
        return toDTO(citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id)));
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> buscarPorFecha(LocalDate fecha) {
        return citaRepository.findByFecha(fecha).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> buscarPorCliente(Integer clienteId) {
        return citaRepository.findByClienteId(clienteId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> buscarPorMedico(Integer medicoId) {
        return citaRepository.findByMedicoId(medicoId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> buscarPorEstado(String estado) {
        return citaRepository.findByEstado(estado).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public CitaDTO crear(CitaDTO dto) {
        Cita cita = toEntity(dto);
        // Calcular hora_fin automáticamente según duración del tipo de cita
        int duracion = cita.getTipoCita().getDuracionMinutos();
        cita.setHoraFin(cita.getHoraInicio().plusMinutes(duracion));
        cita.setEstado("pendiente");
        return toDTO(citaRepository.save(cita));
    }

    public CitaDTO actualizar(Integer id, CitaDTO dto) {
        Cita existente = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));

        existente.setFecha(dto.getFecha());
        existente.setHoraInicio(dto.getHoraInicio());

        TipoCita tipoCita = tipoCitaRepository.findById(dto.getTipoCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("TipoCita", "id", dto.getTipoCitaId()));
        existente.setTipoCita(tipoCita);
        existente.setHoraFin(dto.getHoraInicio().plusMinutes(tipoCita.getDuracionMinutos()));

        existente.setMedico(medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico", "id", dto.getMedicoId())));
        existente.setMascota(mascotaRepository.findById(dto.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", dto.getMascotaId())));
        existente.setCliente(clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", dto.getClienteId())));
        existente.setMotivo(dto.getMotivo());
        existente.setObservaciones(dto.getObservaciones());
        if (dto.getEstado() != null) existente.setEstado(dto.getEstado());

        return toDTO(citaRepository.save(existente));
    }

    public CitaDTO cambiarEstado(Integer id, String nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));

        List<String> estadosValidos = List.of(
                "pendiente", "confirmada", "en_curso", "completada", "cancelada", "no_asistio");
        if (!estadosValidos.contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
        }
        cita.setEstado(nuevoEstado);
        return toDTO(citaRepository.save(cita));
    }

    public void eliminar(Integer id) {
        if (!citaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cita", "id", id);
        }
        citaRepository.deleteById(id);
    }

    // ── Conversiones ───────────────────────────────────────────────────────

    private CitaDTO toDTO(Cita c) {
        CitaDTO dto = new CitaDTO();
        dto.setId(c.getId());
        dto.setFecha(c.getFecha());
        dto.setHoraInicio(c.getHoraInicio());
        dto.setHoraFin(c.getHoraFin());
        dto.setEstado(c.getEstado());
        dto.setTipoCitaId(c.getTipoCita().getId());
        dto.setTipoCitaNombre(c.getTipoCita().getNombre());
        dto.setTipoCitaDuracionMinutos(c.getTipoCita().getDuracionMinutos());
        dto.setMedicoId(c.getMedico().getId());
        dto.setMedicoNombre(c.getMedico().getNombre() + " " + c.getMedico().getApellido());
        dto.setMascotaId(c.getMascota().getId());
        dto.setMascotaNombre(c.getMascota().getNombre());
        dto.setClienteId(c.getCliente().getId());
        dto.setClienteNombre(c.getCliente().getNombre() + " " + c.getCliente().getApellido());
        dto.setMotivo(c.getMotivo());
        dto.setObservaciones(c.getObservaciones());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }

    private Cita toEntity(CitaDTO dto) {
        Cita c = new Cita();
        c.setFecha(dto.getFecha());
        c.setHoraInicio(dto.getHoraInicio());
        c.setTipoCita(tipoCitaRepository.findById(dto.getTipoCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("TipoCita", "id", dto.getTipoCitaId())));
        c.setMedico(medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico", "id", dto.getMedicoId())));
        c.setMascota(mascotaRepository.findById(dto.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", dto.getMascotaId())));
        c.setCliente(clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", dto.getClienteId())));
        c.setMotivo(dto.getMotivo());
        c.setObservaciones(dto.getObservaciones());
        return c;
    }
}
