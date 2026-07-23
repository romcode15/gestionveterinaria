package com.gestionvet.veterinariaapi.dto;

public class PermisoDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private String modulo;

    // ── Constructores ──────────────────────────────────────────────────

    public PermisoDTO() {}

    public PermisoDTO(Integer id, String nombre, String descripcion, String modulo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.modulo = modulo;
    }

    // ── Getters y Setters ──────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
}