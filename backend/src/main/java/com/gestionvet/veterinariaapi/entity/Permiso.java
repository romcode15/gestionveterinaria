package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "permisos")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "modulo", length = 50)
    private String modulo;

    // ── Constructores ──────────────────────────────────────────────────────

    public Permiso() {}

    public Permiso(Integer id, String nombre, String descripcion, String modulo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.modulo = modulo;
    }

    // ── Getters y Setters ──────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
}
