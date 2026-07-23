package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.*;
import com.gestionvet.veterinariaapi.entity.Medico;
import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.repository.ClienteRepository;
import com.gestionvet.veterinariaapi.repository.MascotaRepository;
import com.gestionvet.veterinariaapi.security.UsuarioAutenticadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.time.LocalDate;

/**
 * Portal exclusivo para usuarios con rol VETERINARIO.
 * Filtra automáticamente por el médico autenticado.
 */
@Service
@Transactional(readOnly = true)
public class PortalMedicoService {

    @Autowired private UsuarioAutenticadoService usuarioAutenticadoService;
    @Autowired private CitaService               citaService;
    @Autowired private DiagnosticoService        diagnosticoService;
    @Autowired private MedicoService             medicoService;
    @Autowired private ClienteRepository         clienteRepository;
    @Autowired private MascotaRepository         mascotaRepository;
    @Autowired private ClienteService            clienteService;
    @Autowired private MascotaService            mascotaService;

    // ── Helper: obtener médico del usuario autenticado ─────────────────────

    private Medico getMedicoAutenticado() {
        Usuario usuario = usuarioAutenticadoService.getUsuarioActual();
        if (usuario.getMedico() == null) {
            throw new IllegalStateException(
                    "Tu cuenta no está vinculada a un médico. Contacta al administrador.");
        }
        return usuario.getMedico();
    }

    // ── Mis citas ──────────────────────────────────────────────────────────

    public Page<CitaDTO> misCitas(Pageable pageable) {
        Medico medico = getMedicoAutenticado();
        return citaService.buscarPorMedico(medico.getId(), pageable);
    }

    // ── Mis citas del día ──────────────────────────────────────────────────

    public Page<CitaDTO> misCitasHoy(Pageable pageable) {
        Medico medico = getMedicoAutenticado();
        return citaService.buscarPorFechaYMedico(LocalDate.now(), medico.getId(), pageable);
    }

    // ── Mis citas de una fecha específica ──────────────────────────────────

    public Page<CitaDTO> misCitasPorFecha(LocalDate fecha, Pageable pageable) {
        Medico medico = getMedicoAutenticado();
        return citaService.buscarPorFechaYMedico(fecha, medico.getId(), pageable);
    }

    // ── Mis diagnósticos registrados ───────────────────────────────────────

    public Page<DiagnosticoDTO> misDiagnosticos(Pageable pageable) {
        Medico medico = getMedicoAutenticado();
        return diagnosticoService.porMedico(medico.getId(), pageable);
    }

    // ── Mi perfil como médico ──────────────────────────────────────────────

    public MedicoDTO miPerfil() {
        Medico medico = getMedicoAutenticado();
        return medicoService.buscarPorId(medico.getId());
    }

    // ── Mis clientes (los que tienen citas conmigo) ────────────────────────

    @Transactional(readOnly = true)
    public Page<ClienteDTO> misClientes(String busqueda, Pageable pageable) {
        Medico medico = getMedicoAutenticado();
        if (busqueda != null && !busqueda.isBlank()) {
            return clienteRepository
                    .findByMedicoIdAndNombre(medico.getId(), busqueda.trim(), pageable)
                    .map(c -> clienteService.buscarPorId(c.getId()));
        }
        return clienteRepository
                .findByMedicoId(medico.getId(), pageable)
                .map(c -> clienteService.buscarPorId(c.getId()));
    }

    // ── Mis mascotas (las que tienen citas conmigo) ────────────────────────

    @Transactional(readOnly = true)
    public Page<MascotaDTO> misMascotas(String busqueda, Pageable pageable) {
        Medico medico = getMedicoAutenticado();
        if (busqueda != null && !busqueda.isBlank()) {
            return mascotaRepository
                    .findByMedicoIdAndNombre(medico.getId(), busqueda.trim(), pageable)
                    .map(m -> mascotaService.buscarPorId(m.getId()));
        }
        return mascotaRepository
                .findByMedicoId(medico.getId(), pageable)
                .map(m -> mascotaService.buscarPorId(m.getId()));
    }
}
