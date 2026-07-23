package com.gestionvet.veterinariaapi.dto;

import java.util.List;

public class RolDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private List<PermisoDTO> permisos;

    // ── Constructores ──────────────────────────────────────────────────

    public RolDTO() {}

    public RolDTO(Integer id, String nombre, String descripcion, List<PermisoDTO> permisos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = permisos;
    }

    // ── Getters y Setters ──────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<PermisoDTO> getPermisos() { return permisos; }
    public void setPermisos(List<PermisoDTO> permisos) { this.permisos = permisos; }
}