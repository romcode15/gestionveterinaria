package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Plan de tratamiento asociado a un diagnóstico.
 * Relación 1:1 con Diagnostico.
 * Contiene el conjunto de medicamentos/instrucciones en tratamiento_detalle.
 */
@Entity
@Table(name = "tratamiento")
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ── Relación 1:1 con Diagnóstico ───────────────────────────────────────
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "diagnostico_id", nullable = false, unique = true)
    private Diagnostico diagnostico;

    @Column(name = "instrucciones_generales", columnDefinition = "TEXT")
    private String instruccionesGenerales;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "proxima_visita")
    private LocalDate proximaVisita;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Detalle del tratamiento (líneas de medicamentos) ───────────────────
    @OneToMany(mappedBy = "tratamiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TratamientoDetalle> detalles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Tratamiento() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Diagnostico getDiagnostico() { return diagnostico; }
    public void setDiagnostico(Diagnostico diagnostico) { this.diagnostico = diagnostico; }

    public String getInstruccionesGenerales() { return instruccionesGenerales; }
    public void setInstruccionesGenerales(String instruccionesGenerales) { this.instruccionesGenerales = instruccionesGenerales; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public LocalDate getProximaVisita() { return proximaVisita; }
    public void setProximaVisita(LocalDate proximaVisita) { this.proximaVisita = proximaVisita; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<TratamientoDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<TratamientoDetalle> detalles) { this.detalles = detalles; }
}
