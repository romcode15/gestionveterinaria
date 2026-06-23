package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "razas")
public class Raza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especie_id", nullable = false)
    private Especie especie;

    // ── Constructores ──────────────────────────────────────────────────────

    public Raza() {}

    public Raza(Integer id, String nombre, Especie especie) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
    }

    // ── Getters y Setters ──────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }
}
