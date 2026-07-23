package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.CitaDTO;
import com.gestionvet.veterinariaapi.entity.*;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitaService {

    @Autowired private CitaRepository     citaRepository;
    @Autowired private ClienteRepository  clienteRepository;
    @Autowired private MascotaRepository  mascotaRepository;
    @Autowired private MedicoRepository   medicoRepository;
    @Autowired private TipoCitaRepository tipoCitaRepository;

    // ── Consultas (solo lectura) ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<CitaDTO> listarTodas(Pageable pageable) {
        return citaRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public CitaDTO buscarPorId(Integer id) {
        return toDTO(citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id)));
    }

    @Transactional(readOnly = true)
    public Page<CitaDTO> buscarPorFecha(LocalDate fecha, Pageable pageable) {
        return citaRepository.findByFecha(fecha, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<CitaDTO> buscarPorFechaYMedico(LocalDate fecha, Integer medicoId, Pageable pageable) {
        return citaRepository.findByFechaAndMedicoId(fecha, medicoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<CitaDTO> buscarPorCliente(Integer clienteId, Pageable pageable) {
        return citaRepository.findByClienteId(clienteId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<CitaDTO> buscarPorMedico(Integer medicoId, Pageable pageable) {
        return citaRepository.findByMedicoId(medicoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<CitaDTO> buscarPorEstado(String estado, Pageable pageable) {
        return citaRepository.findByEstado(estado, pageable).map(this::toDTO);
    }

    // ── PROCESO TRANSACCIONAL COMPLETO ────────────────────────────────────
    /**
     * Registrar una cita es un proceso que afecta múltiples tablas.
     * @Transactional garantiza que TODO se guarda o TODO se revierte (rollback).
     *
     * Pasos:
     *  1. Verificar que el cliente existe y está activo
     *  2. Verificar que la mascota pertenece al cliente y está activa
     *  3. Verificar que el médico existe y está disponible
     *  4. Calcular hora_fin según duración del tipo de cita
     *  5. Verificar que no hay conflicto de horario para ese médico
     *  6. Crear la cita con estado = 'pendiente'
     *  7. Actualizar numero_mascotas en la tabla clientes
     *
     * Si cualquiera de los pasos lanza una excepción →
     * Spring revierte TODOS los cambios automáticamente.
     */
    @Transactional(rollbackFor = Exception.class)
    public CitaDTO crear(CitaDTO dto) {

        // ── Paso 1: Verificar cliente activo ──────────────────────────────
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", dto.getClienteId()));

        if (!"activo".equals(cliente.getEstado())) {
            throw new IllegalArgumentException(
                "El cliente '" + cliente.getNombre() + " " + cliente.getApellido() +
                "' está inactivo y no puede agendar citas.");
        }

        // ── Paso 2: Verificar mascota del cliente y estado activo ─────────
        Mascota mascota = mascotaRepository.findById(dto.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", dto.getMascotaId()));

        if (!mascota.getCliente().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException(
                "La mascota '" + mascota.getNombre() +
                "' no pertenece al cliente indicado.");
        }

        if (!"activo".equals(mascota.getEstado())) {
            throw new IllegalArgumentException(
                "La mascota '" + mascota.getNombre() +
                "' no está activa (estado: " + mascota.getEstado() + ").");
        }

        // ── Paso 3: Verificar médico disponible y activo ──────────────────
        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico", "id", dto.getMedicoId()));

        if (!medico.getDisponible()) {
            throw new IllegalArgumentException(
                "El médico '" + medico.getNombre() + " " + medico.getApellido() +
                "' no está disponible para nuevas citas.");
        }

        if (!"activo".equals(medico.getEstado())) {
            throw new IllegalArgumentException(
                "El médico '" + medico.getNombre() + " " + medico.getApellido() +
                "' está inactivo.");
        }

        // ── Paso 4: Resolver tipo de cita y calcular hora_fin ─────────────
        TipoCita tipoCita = tipoCitaRepository.findById(dto.getTipoCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("TipoCita", "id", dto.getTipoCitaId()));

        LocalTime horaInicio = dto.getHoraInicio();
        LocalTime horaFin    = horaInicio.plusMinutes(tipoCita.getDuracionMinutos());

        // ── Paso 5: Verificar conflicto de horario del médico ─────────────
        boolean hayConflicto = citaRepository.existeConflictoHorario(
                medico.getId(),
                dto.getFecha(),
                horaInicio,
                horaFin,
                null   // null = nueva cita, no excluir ninguna
        );

        if (hayConflicto) {
            throw new IllegalArgumentException(
                "El médico '" + medico.getNombre() + " " + medico.getApellido() +
                "' ya tiene una cita el " + dto.getFecha() +
                " entre " + horaInicio + " y " + horaFin +
                ". Elija otro horario o médico.");
        }

        // ── Paso 6: Crear la cita ─────────────────────────────────────────
        Cita cita = new Cita();
        cita.setFecha(dto.getFecha());
        cita.setHoraInicio(horaInicio);
        cita.setHoraFin(horaFin);
        cita.setEstado("pendiente");
        cita.setTipoCita(tipoCita);
        cita.setMedico(medico);
        cita.setMascota(mascota);
        cita.setCliente(cliente);
        cita.setMotivo(dto.getMotivo());
        cita.setObservaciones(dto.getObservaciones());

        Cita citaGuardada = citaRepository.save(cita);

        // ── Paso 7: Actualizar numero_mascotas del cliente (+1) ───────────
        // Operación directa en BD — no carga el objeto completo para evitar
        // condiciones de carrera en entornos concurrentes
        clienteRepository.actualizarNumeroMascotas(cliente.getId(), 1);

        return toDTO(citaGuardada);
    }

    // ── Actualizar cita ───────────────────────────────────────────────────

    public CitaDTO actualizar(Integer id, CitaDTO dto) {
        Cita existente = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));

        TipoCita tipoCita = tipoCitaRepository.findById(dto.getTipoCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("TipoCita", "id", dto.getTipoCitaId()));

        LocalTime horaInicio = dto.getHoraInicio();
        LocalTime horaFin    = horaInicio.plusMinutes(tipoCita.getDuracionMinutos());

        // Verificar conflicto al actualizar (excluir la cita actual)
        boolean hayConflicto = citaRepository.existeConflictoHorario(
                dto.getMedicoId(),
                dto.getFecha(),
                horaInicio,
                horaFin,
                id   // excluir la propia cita al editar
        );

        if (hayConflicto) {
            throw new IllegalArgumentException(
                "El médico ya tiene una cita en ese horario. Elija otro horario o médico.");
        }

        existente.setFecha(dto.getFecha());
        existente.setHoraInicio(horaInicio);
        existente.setHoraFin(horaFin);
        existente.setTipoCita(tipoCita);
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

    // ── Cambiar estado ────────────────────────────────────────────────────

    public CitaDTO cambiarEstado(Integer id, String nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));

        List<String> estadosValidos = List.of(
                "pendiente", "confirmada", "en_curso", "completada", "cancelada", "no_asistio");

        if (!estadosValidos.contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado +
                ". Valores permitidos: " + estadosValidos);
        }

        cita.setEstado(nuevoEstado);
        return toDTO(citaRepository.save(cita));
    }

    // ── Eliminar ──────────────────────────────────────────────────────────

    public void eliminar(Integer id) {
        if (!citaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cita", "id", id);
        }
        citaRepository.deleteById(id);
    }

    // ── Conversiones DTO ↔ Entidad ────────────────────────────────────────

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
        dto.setTipoCitaColor(c.getTipoCita().getColor());
        dto.setTipoCitaDescripcion(c.getTipoCita().getDescripcion());
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
}
