package com.gestionvet.veterinariaapi.dto;

import java.util.List;

/**
 * Vista consolidada del historial clínico de una mascota.
 * Cada entrada representa una consulta completa:
 * datos de la cita + diagnóstico + tratamiento con sus líneas.
 */
public class HistorialClinicoDTO {

    // Datos de la mascota
    private Integer mascotaId;
    private String  mascotaNombre;
    private String  especieNombre;
    private String  razaNombre;
    private String  clienteNombre;

    // Entradas del historial ordenadas por fecha desc
    private List<EntradaHistorialDTO> historial;

    public HistorialClinicoDTO() {}

    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }

    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }

    public String getEspecieNombre() { return especieNombre; }
    public void setEspecieNombre(String especieNombre) { this.especieNombre = especieNombre; }

    public String getRazaNombre() { return razaNombre; }
    public void setRazaNombre(String razaNombre) { this.razaNombre = razaNombre; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public List<EntradaHistorialDTO> getHistorial() { return historial; }
    public void setHistorial(List<EntradaHistorialDTO> historial) { this.historial = historial; }

    // ── Clase interna: una consulta completa ───────────────────────────────
    public static class EntradaHistorialDTO {

        private String  citaFecha;
        private String  tipoCita;
        private String  medicoNombre;
        private String  motivo;

        // Diagnóstico
        private Integer diagnosticoId;
        private String  sintomas;
        private String  diagnostico;
        private String  pronostico;
        private String  pesoConsulta;
        private String  temperatura;
        private String  observacionesDiagnostico;

        // Tratamiento
        private Integer          tratamientoId;
        private String           instruccionesGenerales;
        private String           fechaInicioTratamiento;
        private String           fechaFinTratamiento;
        private String           proximaVisita;
        private List<TratamientoDetalleDTO> medicamentos;

        public EntradaHistorialDTO() {}

        public String getCitaFecha() { return citaFecha; }
        public void setCitaFecha(String citaFecha) { this.citaFecha = citaFecha; }

        public String getTipoCita() { return tipoCita; }
        public void setTipoCita(String tipoCita) { this.tipoCita = tipoCita; }

        public String getMedicoNombre() { return medicoNombre; }
        public void setMedicoNombre(String medicoNombre) { this.medicoNombre = medicoNombre; }

        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }

        public Integer getDiagnosticoId() { return diagnosticoId; }
        public void setDiagnosticoId(Integer diagnosticoId) { this.diagnosticoId = diagnosticoId; }

        public String getSintomas() { return sintomas; }
        public void setSintomas(String sintomas) { this.sintomas = sintomas; }

        public String getDiagnostico() { return diagnostico; }
        public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

        public String getPronostico() { return pronostico; }
        public void setPronostico(String pronostico) { this.pronostico = pronostico; }

        public String getPesoConsulta() { return pesoConsulta; }
        public void setPesoConsulta(String pesoConsulta) { this.pesoConsulta = pesoConsulta; }

        public String getTemperatura() { return temperatura; }
        public void setTemperatura(String temperatura) { this.temperatura = temperatura; }

        public String getObservacionesDiagnostico() { return observacionesDiagnostico; }
        public void setObservacionesDiagnostico(String observacionesDiagnostico) { this.observacionesDiagnostico = observacionesDiagnostico; }

        public Integer getTratamientoId() { return tratamientoId; }
        public void setTratamientoId(Integer tratamientoId) { this.tratamientoId = tratamientoId; }

        public String getInstruccionesGenerales() { return instruccionesGenerales; }
        public void setInstruccionesGenerales(String instruccionesGenerales) { this.instruccionesGenerales = instruccionesGenerales; }

        public String getFechaInicioTratamiento() { return fechaInicioTratamiento; }
        public void setFechaInicioTratamiento(String fechaInicioTratamiento) { this.fechaInicioTratamiento = fechaInicioTratamiento; }

        public String getFechaFinTratamiento() { return fechaFinTratamiento; }
        public void setFechaFinTratamiento(String fechaFinTratamiento) { this.fechaFinTratamiento = fechaFinTratamiento; }

        public String getProximaVisita() { return proximaVisita; }
        public void setProximaVisita(String proximaVisita) { this.proximaVisita = proximaVisita; }

        public List<TratamientoDetalleDTO> getMedicamentos() { return medicamentos; }
        public void setMedicamentos(List<TratamientoDetalleDTO> medicamentos) { this.medicamentos = medicamentos; }
    }
}
