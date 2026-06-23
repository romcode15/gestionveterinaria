package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MascotaDTO {

    private Integer id;

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotNull(message = "La especie es obligatoria")
    private Integer especieId;
    private String especieNombre;

    @NotNull(message = "La raza es obligatoria")
    private Integer razaId;
    private String razaNombre;

    @NotBlank(message = "El sexo es obligatorio")
    private String sexo;

    private LocalDate fechaNacimiento;
    private String color;

    @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor a 0")
    private BigDecimal peso;

    private String microchip;
    private Boolean esterilizado;
    private String estado;

    @NotNull(message = "El propietario es obligatorio")
    private Integer clienteId;
    private String clienteNombre;

    private String observaciones;
    private LocalDateTime createdAt;

    // ── Constructores ──────────────────────────────────────────────────────

    public MascotaDTO() {}

    // ── Getters y Setters ──────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getEspecieId() { return especieId; }
    public void setEspecieId(Integer especieId) { this.especieId = especieId; }

    public String getEspecieNombre() { return especieNombre; }
    public void setEspecieNombre(String especieNombre) { this.especieNombre = especieNombre; }

    public Integer getRazaId() { return razaId; }
    public void setRazaId(Integer razaId) { this.razaId = razaId; }

    public String getRazaNombre() { return razaNombre; }
    public void setRazaNombre(String razaNombre) { this.razaNombre = razaNombre; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public String getMicrochip() { return microchip; }
    public void setMicrochip(String microchip) { this.microchip = microchip; }

    public Boolean getEsterilizado() { return esterilizado; }
    public void setEsterilizado(Boolean esterilizado) { this.esterilizado = esterilizado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
