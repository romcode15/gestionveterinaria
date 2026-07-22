package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Catálogo de vacunas disponibles en la veterinaria.
 * Independiente de mascota — es el nombre/tipo de vacuna.
 */
@Entity
@Table(name = "vacuna")
public class Vacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 150, unique = true)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    // Especie a la que aplica (puede ser null si aplica a todas)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especie_id")
    private Especie especie;

    // Intervalo en días para la revacunación (ej: 365 = anual)
    @Column(name = "intervalo_dias_revacunacion")
    private Integer intervaloDiasRevacunacion;

    @Column(name = "activa", nullable = false)
    private Boolean activa = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.activa == null) this.activa = true;
    }

    public Vacuna() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }

    public Integer getIntervaloDiasRevacunacion() { return intervaloDiasRevacunacion; }
    public void setIntervaloDiasRevacunacion(Integer intervaloDiasRevacunacion) { this.intervaloDiasRevacunacion = intervaloDiasRevacunacion; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
