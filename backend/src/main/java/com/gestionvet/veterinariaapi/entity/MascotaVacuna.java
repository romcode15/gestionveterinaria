package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Registro de cada aplicación de vacuna a una mascota.
 * Vincula: mascota + vacuna + médico que la aplicó + fechas.
 */
@Entity
@Table(name = "mascota_vacuna")
public class MascotaVacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vacuna_id", nullable = false)
    private Vacuna vacuna;

    // Médico que aplicó la vacuna
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    // Cita en la que se aplicó (opcional — puede vacunarse sin cita previa registrada)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id")
    private Cita cita;

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    // Calculada automáticamente si la vacuna tiene intervalo_dias_revacunacion
    @Column(name = "fecha_proxima_dosis")
    private LocalDate fechaProximaDosis;

    @Column(name = "lote_vacuna", length = 50)
    private String loteVacuna;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // vigente = aplicada y sin vencer | vencida = pasó fecha proxima dosis
    @Column(name = "estado", nullable = false, length = 15)
    private String estado = "vigente";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) this.estado = "vigente";
    }

    public MascotaVacuna() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }

    public Vacuna getVacuna() { return vacuna; }
    public void setVacuna(Vacuna vacuna) { this.vacuna = vacuna; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    public Cita getCita() { return cita; }
    public void setCita(Cita cita) { this.cita = cita; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
