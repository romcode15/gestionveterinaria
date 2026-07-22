package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.*;
import com.gestionvet.veterinariaapi.entity.Cliente;
import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.*;
import com.gestionvet.veterinariaapi.security.UsuarioAutenticadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Portal exclusivo para usuarios con rol CLIENTE.
 * Todos los métodos filtran automáticamente por el cliente autenticado —
 * un cliente nunca puede ver datos de otro cliente.
 */
@Service
@Transactional(readOnly = true)
public class PortalClienteService {

    @Autowired private UsuarioAutenticadoService usuarioAutenticadoService;
    @Autowired private MascotaRepository         mascotaRepository;
    @Autowired private CitaRepository            citaRepository;
    @Autowired private DiagnosticoRepository     diagnosticoRepository;
    @Autowired private TratamientoRepository     tratamientoRepository;
    @Autowired private MascotaVacunaRepository   mascotaVacunaRepository;
    @Autowired private MascotaService            mascotaService;
    @Autowired private CitaService               citaService;
    @Autowired private HistorialClinicoService   historialClinicoService;

    // ── Helper: obtener cliente del usuario autenticado ────────────────────

    private Cliente getClienteAutenticado() {
        Usuario usuario = usuarioAutenticadoService.getUsuarioActual();
        if (usuario.getCliente() == null) {
            throw new IllegalStateException(
                    "Tu cuenta no está vinculada a un cliente. Contacta al administrador.");
        }
        return usuario.getCliente();
    }

    // ── Mis mascotas ───────────────────────────────────────────────────────

    public Page<MascotaDTO> misMascotas(Pageable pageable) {
        Cliente cliente = getClienteAutenticado();
        return mascotaService.buscarPorCliente(cliente.getId(), pageable);
    }

    // ── Mis citas ──────────────────────────────────────────────────────────

    public Page<CitaDTO> misCitas(Pageable pageable) {
        Cliente cliente = getClienteAutenticado();
        return citaService.buscarPorCliente(cliente.getId(), pageable);
    }

    // ── Historial clínico de una de mis mascotas ───────────────────────────

    public HistorialClinicoDTO historialMascota(Integer mascotaId) {
        Cliente cliente = getClienteAutenticado();

        // Verificar que la mascota pertenece a este cliente
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new ResourceNotFoundException("Mascota", "id", mascotaId);
        }
        boolean esMiMascota = mascotaRepository
                .findByClienteId(cliente.getId())
                .stream()
                .anyMatch(m -> m.getId().equals(mascotaId));

        if (!esMiMascota) {
            throw new IllegalArgumentException(
                    "No tienes acceso al historial de esta mascota.");
        }

        return historialClinicoService.obtenerPorMascota(mascotaId);
    }

    // ── Vacunas de una de mis mascotas ─────────────────────────────────────

    public Page<MascotaVacunaDTO> vacunasMascota(Integer mascotaId, Pageable pageable) {
        Cliente cliente = getClienteAutenticado();

        boolean esMiMascota = mascotaRepository
                .findByClienteId(cliente.getId())
                .stream()
                .anyMatch(m -> m.getId().equals(mascotaId));

        if (!esMiMascota) {
            throw new IllegalArgumentException("No tienes acceso a esta mascota.");
        }

        return mascotaVacunaRepository
                .findByMascotaId(mascotaId, pageable)
                .map(mv -> {
                    MascotaVacunaDTO dto = new MascotaVacunaDTO();
                    dto.setId(mv.getId());
                    dto.setMascotaId(mv.getMascota().getId());
                    dto.setMascotaNombre(mv.getMascota().getNombre());
                    dto.setVacunaId(mv.getVacuna().getId());
                    dto.setVacunaNombre(mv.getVacuna().getNombre());
                    dto.setFechaAplicacion(mv.getFechaAplicacion());
                    dto.setFechaProximaDosis(mv.getFechaProximaDosis());
                    dto.setEstado(mv.getEstado());
                    dto.setObservaciones(mv.getObservaciones());
                    return dto;
                });
    }

    // ── Mi perfil ──────────────────────────────────────────────────────────

    public ClienteDTO miPerfil() {
        Cliente cliente = getClienteAutenticado();
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setTipoDocumento(cliente.getTipoDocumento());
        dto.setNumeroDocumento(cliente.getNumeroDocumento());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setDireccion(cliente.getDireccion());
        dto.setCiudad(cliente.getCiudad());
        dto.setFechaNacimiento(cliente.getFechaNacimiento());
        dto.setEstado(cliente.getEstado());
        dto.setCreatedAt(cliente.getCreatedAt());
        return dto;
    }
}
