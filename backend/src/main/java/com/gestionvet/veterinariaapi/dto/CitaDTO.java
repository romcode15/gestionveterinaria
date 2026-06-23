package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CitaDTO {

    private Integer id;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    private LocalTime horaFin;
    private String estado;

    @NotNull(message = "El tipo de cita es obligatorio")
    private Integer tipoCitaId;
    private String tipoCitaNombre;
    private Integer tipoCitaDuracionMinutos;

    @NotNull(message = "El médico es obligatorio")
    private Integer medicoId;
    private String medicoNombre;

    @NotNull(message = "La mascota es obligatoria")
    private Integer mascotaId;
    private String mascotaNombre;

    @NotNull(message = "El cliente es obligatorio")
    private Integer clienteId;
    private String clienteNombre;

    @NotBlank(message = "El motivo de la consulta es obligatorio")
    @Size(max = 300, message = "El motivo no puede superar 300 caracteres")
    private String motivo;

    private String observaciones;
    private LocalDateTime createdAt;

    // ── Constructores ──────────────────────────────────────────────────────

    public CitaDTO() {}

    // ── Getters y Setters ──────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getTipoCitaId() { return tipoCitaId; }
    public void setTipoCitaId(Integer tipoCitaId) { this.tipoCitaId = tipoCitaId; }

    public String getTipoCitaNombre() { return tipoCitaNombre; }
    public void setTipoCitaNombre(String tipoCitaNombre) { this.tipoCitaNombre = tipoCitaNombre; }

    public Integer getTipoCitaDuracionMinutos() { return tipoCitaDuracionMinutos; }
    public void setTipoCitaDuracionMinutos(Integer tipoCitaDuracionMinutos) { this.tipoCitaDuracionMinutos = tipoCitaDuracionMinutos; }

    public Integer getMedicoId() { return medicoId; }
    public void setMedicoId(Integer medicoId) { this.medicoId = medicoId; }

    public String getMedicoNombre() { return medicoNombre; }
    public void setMedicoNombre(String medicoNombre) { this.medicoNombre = medicoNombre; }

    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }

    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
