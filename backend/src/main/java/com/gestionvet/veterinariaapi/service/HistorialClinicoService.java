package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.HistorialClinicoDTO;
import com.gestionvet.veterinariaapi.dto.HistorialClinicoDTO.EntradaHistorialDTO;
import com.gestionvet.veterinariaapi.dto.TratamientoDetalleDTO;
import com.gestionvet.veterinariaapi.entity.Diagnostico;
import com.gestionvet.veterinariaapi.entity.Mascota;
import com.gestionvet.veterinariaapi.entity.Tratamiento;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.DiagnosticoRepository;
import com.gestionvet.veterinariaapi.repository.MascotaRepository;
import com.gestionvet.veterinariaapi.repository.TratamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HistorialClinicoService {

    @Autowired private MascotaRepository     mascotaRepository;
    @Autowired private DiagnosticoRepository diagnosticoRepository;
    @Autowired private TratamientoRepository tratamientoRepository;

    /**
     * Devuelve el historial clínico completo de una mascota:
     * cada consulta con su diagnóstico y tratamiento, ordenado de más reciente a más antiguo.
     */
    public HistorialClinicoDTO obtenerPorMascota(Integer mascotaId) {

        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", mascotaId));

        // Traer todos los diagnósticos de la mascota ordenados por fecha de cita desc
        List<Diagnostico> diagnosticos = diagnosticoRepository
                .findByMascotaId(mascotaId,
                        PageRequest.of(0, Integer.MAX_VALUE,
                                Sort.by("cita.fecha").descending()))
                .getContent();

        List<EntradaHistorialDTO> entradas = diagnosticos.stream().map(d -> {
            EntradaHistorialDTO entrada = new EntradaHistorialDTO();

            // Datos de la cita
            entrada.setCitaFecha(d.getCita().getFecha().toString());
            entrada.setTipoCita(d.getCita().getTipoCita().getNombre());
            entrada.setMedicoNombre(d.getMedico().getNombre() + " " + d.getMedico().getApellido());
            entrada.setMotivo(d.getCita().getMotivo());

            // Diagnóstico
            entrada.setDiagnosticoId(d.getId());
            entrada.setSintomas(d.getSintomas());
            entrada.setDiagnostico(d.getDiagnostico());
            entrada.setPronostico(d.getPronostico());
            entrada.setPesoConsulta(d.getPesoConsulta() != null ? d.getPesoConsulta().toPlainString() + " kg" : null);
            entrada.setTemperatura(d.getTemperatura() != null ? d.getTemperatura().toPlainString() + " °C" : null);
            entrada.setObservacionesDiagnostico(d.getObservaciones());

            // Tratamiento (puede no existir si el médico aún no lo registró)
            Optional<Tratamiento> tratOpt = tratamientoRepository.findByDiagnosticoId(d.getId());
            tratOpt.ifPresent(t -> {
                entrada.setTratamientoId(t.getId());
                entrada.setInstruccionesGenerales(t.getInstruccionesGenerales());
                entrada.setFechaInicioTratamiento(t.getFechaInicio() != null ? t.getFechaInicio().toString() : null);
                entrada.setFechaFinTratamiento(t.getFechaFin() != null ? t.getFechaFin().toString() : null);
                entrada.setProximaVisita(t.getProximaVisita() != null ? t.getProximaVisita().toString() : null);

                List<TratamientoDetalleDTO> meds = t.getDetalles().stream().map(det -> {
                    TratamientoDetalleDTO dd = new TratamientoDetalleDTO();
                    dd.setId(det.getId());
                    dd.setMedicamento(det.getMedicamento());
                    dd.setDosis(det.getDosis());
                    dd.setFrecuencia(det.getFrecuencia());
                    dd.setDuracionDias(det.getDuracionDias());
                    dd.setViaAdministracionId(det.getViaAdministracion().getId());
                    dd.setViaAdministracionNombre(det.getViaAdministracion().getNombre());
                    dd.setInstrucciones(det.getInstrucciones());
                    return dd;
                }).collect(Collectors.toList());

                entrada.setMedicamentos(meds);
            });

            return entrada;
        }).collect(Collectors.toList());

        // Armar respuesta
        HistorialClinicoDTO historial = new HistorialClinicoDTO();
        historial.setMascotaId(mascota.getId());
        historial.setMascotaNombre(mascota.getNombre());
        historial.setEspecieNombre(mascota.getEspecie().getNombre());
        historial.setRazaNombre(mascota.getRaza().getNombre());
        historial.setClienteNombre(mascota.getCliente().getNombre() + " " + mascota.getCliente().getApellido());
        historial.setHistorial(entradas);

        return historial;
    }
}
