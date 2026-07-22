package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registro clínico generado por el médico al completar una cita.
 * Relación 1:1 con Cita — una cita puede tener un solo diagnóstico.
 */
@Entity
@Table(name = "diagnostico")
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ── Relación 1:1 con Cita ──────────────────────────────────────────────
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

    // ── Relaciones de contexto (redundantes con cita, pero útiles para queries directas) ──
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    // ── Campos clínicos ────────────────────────────────────────────────────
    @Column(name = "sintomas", nullable = false, columnDefinition = "TEXT")
    private String sintomas;

    @Column(name = "diagnostico", nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "pronostico", length = 50)
    // favorable, reservado, grave, muerte
    private String pronostico;

    @Column(name = "peso_consulta", precision = 5, scale = 2)
    private BigDecimal pesoConsulta;

    @Column(name = "temperatura", precision = 4, scale = 1)
    private BigDecimal temperatura;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Diagnostico() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Cita getCita() { return cita; }
    public void setCita(Cita cita) { this.cita = cita; }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

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
