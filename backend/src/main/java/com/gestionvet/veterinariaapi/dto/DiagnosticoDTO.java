package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DiagnosticoDTO {

    private Integer id;

    @NotNull(message = "La cita es obligatoria")
    private Integer citaId;

    // Datos de contexto (solo lectura en respuesta)
    private Integer mascotaId;
    private String  mascotaNombre;
    private Integer medicoId;
    private String  medicoNombre;
    private String  citaFecha;

    @NotBlank(message = "Los síntomas son obligatorios")
    private String sintomas;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    private String pronostico; // favorable, reservado, grave, muerte

    @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor a 0")
    private BigDecimal pesoConsulta;

    @DecimalMin(value = "0.0", inclusive = false, message = "La temperatura debe ser mayor a 0")
    private BigDecimal temperatura;

    private String observaciones;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DiagnosticoDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCitaId() { return citaId; }
    public void setCitaId(Integer citaId) { this.citaId = citaId; }

    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }

    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }

    public Integer getMedicoId() { return medicoId; }
    public void setMedicoId(Integer medicoId) { this.medicoId = medicoId; }

    public String getMedicoNombre() { return medicoNombre; }
    public void setMedicoNombre(String medicoNombre) { this.medicoNombre = medicoNombre; }

    public String getCitaFecha() { return citaFecha; }
    public void setCitaFecha(String citaFecha) { this.citaFecha = citaFecha; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getPronostico() { return pronostico; }
    public void setPronostico(String pronostico) { this.pronostico = pronostico; }

    public BigDecimal getPesoConsulta() { return pesoConsulta; }
    public void setPesoConsulta(BigDecimal pesoConsulta) { this.pesoConsulta = pesoConsulta; }

    public BigDecimal getTemperatura() { return temperatura; }
    public void setTemperatura(BigDecimal temperatura) { this.temperatura = temperatura; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
