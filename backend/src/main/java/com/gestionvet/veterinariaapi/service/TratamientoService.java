package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.TratamientoDTO;
import com.gestionvet.veterinariaapi.dto.TratamientoDetalleDTO;
import com.gestionvet.veterinariaapi.entity.Diagnostico;
import com.gestionvet.veterinariaapi.entity.Tratamiento;
import com.gestionvet.veterinariaapi.entity.TratamientoDetalle;
import com.gestionvet.veterinariaapi.entity.ViaAdministracion;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.DiagnosticoRepository;
import com.gestionvet.veterinariaapi.repository.ProductoRepository;
import com.gestionvet.veterinariaapi.repository.TratamientoRepository;
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
public class TratamientoService {

    private static final Logger log = LoggerFactory.getLogger(TratamientoService.class);

    @Autowired private TratamientoRepository      tratamientoRepository;
    @Autowired private DiagnosticoRepository      diagnosticoRepository;
    @Autowired private ViaAdministracionRepository viaRepository;
    @Autowired private ProductoRepository          productoRepository;
    @Autowired private InventarioService           inventarioService;

    // ── Buscar por ID ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public TratamientoDTO buscarPorId(Integer id) {
        return toDTO(tratamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamiento", "id", id)));
    }

    // ── Buscar por diagnóstico ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public TratamientoDTO buscarPorDiagnostico(Integer diagnosticoId) {
        return toDTO(tratamientoRepository.findByDiagnosticoId(diagnosticoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tratamiento", "diagnosticoId", diagnosticoId)));
    }

    // ── Crear tratamiento con sus detalles (transacción completa) ──────────

    @Transactional(rollbackFor = Exception.class)
    public TratamientoDTO crear(TratamientoDTO dto) {

        Diagnostico diagnostico = diagnosticoRepository.findById(dto.getDiagnosticoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Diagnostico", "id", dto.getDiagnosticoId()));

        // Un diagnóstico solo puede tener un plan de tratamiento
        if (tratamientoRepository.existsByDiagnosticoId(diagnostico.getId())) {
            throw new IllegalArgumentException(
                    "El diagnóstico #" + diagnostico.getId() + " ya tiene un tratamiento registrado.");
        }

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setDiagnostico(diagnostico);
        tratamiento.setInstruccionesGenerales(dto.getInstruccionesGenerales());
        tratamiento.setFechaInicio(dto.getFechaInicio());
        tratamiento.setFechaFin(dto.getFechaFin());
        tratamiento.setProximaVisita(dto.getProximaVisita());

        // Construir líneas de detalle
        List<TratamientoDetalle> detalles = dto.getDetalles().stream().map(d -> {
            ViaAdministracion via = viaRepository.findById(d.getViaAdministracionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "ViaAdministracion", "id", d.getViaAdministracionId()));
            TratamientoDetalle detalle = new TratamientoDetalle();
            detalle.setTratamiento(tratamiento);
            detalle.setMedicamento(d.getMedicamento());
            detalle.setDosis(d.getDosis());
            detalle.setFrecuencia(d.getFrecuencia());
            detalle.setDuracionDias(d.getDuracionDias());
            detalle.setViaAdministracion(via);
            detalle.setInstrucciones(d.getInstrucciones());
            // Vincular producto del inventario si se especificó
            if (d.getProductoId() != null) {
                detalle.setProducto(productoRepository.findById(d.getProductoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", d.getProductoId())));
                detalle.setCantidadDispensada(d.getCantidadDispensada());
            }
            return detalle;
        }).collect(Collectors.toList());

        tratamiento.setDetalles(detalles);

        Tratamiento saved = tratamientoRepository.save(tratamiento);

        // Descontar stock del inventario por cada línea que tenga producto vinculado
        saved.getDetalles().stream()
                .filter(d -> d.getProducto() != null && d.getCantidadDispensada() != null)
                .forEach(inventarioService::descontarStockPorTratamiento);
        log.info("Tratamiento creado: id={}, diagnosticoId={}, medicamentos={}",
                saved.getId(), diagnostico.getId(), detalles.size());
        return toDTO(saved);
    }

    // ── Actualizar tratamiento ─────────────────────────────────────────────

    @Transactional(rollbackFor = Exception.class)
    public TratamientoDTO actualizar(Integer id, TratamientoDTO dto) {
        Tratamiento existente = tratamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamiento", "id", id));

        existente.setInstruccionesGenerales(dto.getInstruccionesGenerales());
        existente.setFechaInicio(dto.getFechaInicio());
        existente.setFechaFin(dto.getFechaFin());
        existente.setProximaVisita(dto.getProximaVisita());

        // Reemplazar detalles completos (orphanRemoval = true los elimina)
        existente.getDetalles().clear();

        List<TratamientoDetalle> nuevosDetalles = dto.getDetalles().stream().map(d -> {
            ViaAdministracion via = viaRepository.findById(d.getViaAdministracionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "ViaAdministracion", "id", d.getViaAdministracionId()));
            TratamientoDetalle detalle = new TratamientoDetalle();
            detalle.setTratamiento(existente);
            detalle.setMedicamento(d.getMedicamento());
            detalle.setDosis(d.getDosis());
            detalle.setFrecuencia(d.getFrecuencia());
            detalle.setDuracionDias(d.getDuracionDias());
            detalle.setViaAdministracion(via);
            detalle.setInstrucciones(d.getInstrucciones());
            return detalle;
        }).collect(Collectors.toList());

        existente.getDetalles().addAll(nuevosDetalles);

        return toDTO(tratamientoRepository.save(existente));
    }

    // ── Conversión ─────────────────────────────────────────────────────────

    private TratamientoDTO toDTO(Tratamiento t) {
        TratamientoDTO dto = new TratamientoDTO();
        dto.setId(t.getId());
        dto.setDiagnosticoId(t.getDiagnostico().getId());
        dto.setMascotaNombre(t.getDiagnostico().getMascota().getNombre());
        dto.setMedicoNombre(t.getDiagnostico().getMedico().getNombre()
                + " " + t.getDiagnostico().getMedico().getApellido());
        dto.setInstruccionesGenerales(t.getInstruccionesGenerales());
        dto.setFechaInicio(t.getFechaInicio());
        dto.setFechaFin(t.getFechaFin());
        dto.setProximaVisita(t.getProximaVisita());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());

        List<TratamientoDetalleDTO> detalles = t.getDetalles().stream().map(d -> {
            TratamientoDetalleDTO dd = new TratamientoDetalleDTO();
            dd.setId(d.getId());
            dd.setMedicamento(d.getMedicamento());
            dd.setDosis(d.getDosis());
            dd.setFrecuencia(d.getFrecuencia());
            dd.setDuracionDias(d.getDuracionDias());
            dd.setViaAdministracionId(d.getViaAdministracion().getId());
            dd.setViaAdministracionNombre(d.getViaAdministracion().getNombre());
            dd.setInstrucciones(d.getInstrucciones());
            if (d.getProducto() != null) {
                dd.setProductoId(d.getProducto().getId());
                dd.setProductoNombre(d.getProducto().getNombre());
                dd.setCantidadDispensada(d.getCantidadDispensada());
            }
            dd.setCreatedAt(d.getCreatedAt());
            return dd;
        }).collect(Collectors.toList());

        dto.setDetalles(detalles);
        return dto;
    }
}
