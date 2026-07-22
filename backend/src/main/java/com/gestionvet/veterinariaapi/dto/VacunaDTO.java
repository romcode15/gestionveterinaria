package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class VacunaDTO {

    private Integer id;

    @NotBlank(message = "El nombre de la vacuna es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    private String nombre;

    private String descripcion;

    // null = aplica a todas las especies
    private Integer especieId;
    private String  especieNombre;

    @Min(value = 1, message = "El intervalo de revacunación debe ser al menos 1 día")
    private Integer intervaloDiasRevacunacion;

    private Boolean activa;

    private LocalDateTime createdAt;

    public VacunaDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getEspecieId() { return especieId; }
    public void setEspecieId(Integer especieId) { this.especieId = especieId; }

    public String getEspecieNombre() { return especieNombre; }
    public void setEspecieNombre(String especieNombre) { this.especieNombre = especieNombre; }

    public Integer getIntervaloDiasRevacunacion() { return intervaloDiasRevacunacion; }
    public void setIntervaloDiasRevacunacion(Integer intervaloDiasRevacunacion) { this.intervaloDiasRevacunacion = intervaloDiasRevacunacion; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
