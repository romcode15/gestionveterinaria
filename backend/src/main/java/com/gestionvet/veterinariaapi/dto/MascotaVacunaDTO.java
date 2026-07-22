package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MascotaVacunaDTO {

    private Integer id;

    @NotNull(message = "La mascota es obligatoria")
    private Integer mascotaId;
    private String  mascotaNombre;
    private String  clienteNombre;

    @NotNull(message = "La vacuna es obligatoria")
    private Integer vacunaId;
    private String  vacunaNombre;

    @NotNull(message = "El médico que aplica es obligatorio")
    private Integer medicoId;
    private String  medicoNombre;

    // Cita opcional
    private Integer citaId;

    @NotNull(message = "La fecha de aplicación es obligatoria")
    private LocalDate fechaAplicacion;

    // Si no se envía, se calcula automáticamente con el intervalo de la vacuna
    private LocalDate fechaProximaDosis;

    @Size(max = 50, message = "El lote no puede superar 50 caracteres")
    private String loteVacuna;

    private String observaciones;

    private String estado;

    // Calculado: días restantes para la próxima dosis (negativo = vencida)
    private Long diasParaProximaDosis;

    private LocalDateTime createdAt;

    public MascotaVacunaDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getMascotaId() { return mascotaId; }
    public void setMascotaId(Integer mascotaId) { this.mascotaId = mascotaId; }

    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public Integer getVacunaId() { return vacunaId; }
    public void setVacunaId(Integer vacunaId) { this.vacunaId = vacunaId; }

    public String getVacunaNombre() { return vacunaNombre; }
    public void setVacunaNombre(String vacunaNombre) { this.vacunaNombre = vacunaNombre; }

    public Integer getMedicoId() { return medicoId; }
    public void setMedicoId(Integer medicoId) { this.medicoId = medicoId; }

    public String getMedicoNombre() { return medicoNombre; }
    public void setMedicoNombre(String medicoNombre) { this.medicoNombre = medicoNombre; }

    public Integer getCitaId() { return citaId; }
    public void setCitaId(Integer citaId) { this.citaId = citaId; }

    public LocalDate getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(LocalDate fechaAplicacion) { this.fechaAplicacion = fechaAplicacion; }

    public LocalDate getFechaProximaDosis() { return fechaProximaDosis; }
    public void setFechaProximaDosis(LocalDate fechaProximaDosis) { this.fechaProximaDosis = fechaProximaDosis; }

    public String getLoteVacuna() { return loteVacuna; }
    public void setLoteVacuna(String loteVacuna) { this.loteVacuna = loteVacuna; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getDiasParaProximaDosis() { return diasParaProximaDosis; }
    public void setDiasParaProximaDosis(Long diasParaProximaDosis) { this.diasParaProximaDosis = diasParaProximaDosis; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
