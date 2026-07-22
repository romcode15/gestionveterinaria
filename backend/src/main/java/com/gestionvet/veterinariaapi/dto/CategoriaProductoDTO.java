package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class CategoriaProductoDTO {

    private Integer id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    private LocalDateTime createdAt;

    public CategoriaProductoDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
