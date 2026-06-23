package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tipos_cita")
public class TipoCita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    // ── Constructores ──────────────────────────────────────────────────────

    public TipoCita() {}

    public TipoCita(Integer id, String nombre, Integer duracionMinutos, String color, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.duracionMinutos = duracionMinutos;
        this.color = color;
        this.descripcion = descripcion;
    }

    // ── Getters y Setters ──────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
