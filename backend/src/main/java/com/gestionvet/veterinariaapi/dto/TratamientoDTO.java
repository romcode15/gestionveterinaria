package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TratamientoDTO {

    private Integer id;

    @NotNull(message = "El diagnóstico es obligatorio")
    private Integer diagnosticoId;

    // Datos de contexto (solo lectura en respuesta)
    private String mascotaNombre;
    private String medicoNombre;

    private String instruccionesGenerales;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private LocalDate proximaVisita;

    @NotEmpty(message = "El tratamiento debe tener al menos un medicamento")
    @Valid
    private List<TratamientoDetalleDTO> detalles;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TratamientoDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getDiagnosticoId() { return diagnosticoId; }
    public void setDiagnosticoId(Integer diagnosticoId) { this.diagnosticoId = diagnosticoId; }

    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }

    public String getMedicoNombre() { return medicoNombre; }
    public void setMedicoNombre(String medicoNombre) { this.medicoNombre = medicoNombre; }

    public String getInstruccionesGenerales() { return instruccionesGenerales; }
    public void setInstruccionesGenerales(String instruccionesGenerales) { this.instruccionesGenerales = instruccionesGenerales; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public LocalDate getProximaVisita() { return proximaVisita; }
    public void setProximaVisita(LocalDate proximaVisita) { this.proximaVisita = proximaVisita; }

    public List<TratamientoDetalleDTO> getDetalles() { return detalles; }
    public void setDetalles(List<TratamientoDetalleDTO> detalles) { this.detalles = detalles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
