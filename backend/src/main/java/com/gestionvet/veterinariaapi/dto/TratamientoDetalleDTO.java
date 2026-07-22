package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class TratamientoDetalleDTO {

    private Integer id;

    @NotBlank(message = "El medicamento es obligatorio")
    @Size(max = 200, message = "El medicamento no puede superar 200 caracteres")
    private String medicamento;

    @NotBlank(message = "La dosis es obligatoria")
    @Size(max = 100, message = "La dosis no puede superar 100 caracteres")
    private String dosis;

    @NotBlank(message = "La frecuencia es obligatoria")
    @Size(max = 100, message = "La frecuencia no puede superar 100 caracteres")
    private String frecuencia;

    @NotNull(message = "La duración en días es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    private Integer duracionDias;

    @NotNull(message = "La vía de administración es obligatoria")
    private Integer viaAdministracionId;
    private String viaAdministracionNombre;

    private String instrucciones;

    // Producto del inventario (opcional)
    private Integer productoId;
    private String  productoNombre;
    private Integer cantidadDispensada;

    private LocalDateTime createdAt;

    public TratamientoDetalleDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMedicamento() { return medicamento; }
    public void setMedicamento(String medicamento) { this.medicamento = medicamento; }

    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

    public Integer getDuracionDias() { return duracionDias; }
    public void setDuracionDias(Integer duracionDias) { this.duracionDias = duracionDias; }

    public Integer getViaAdministracionId() { return viaAdministracionId; }
    public void setViaAdministracionId(Integer viaAdministracionId) { this.viaAdministracionId = viaAdministracionId; }

    public String getViaAdministracionNombre() { return viaAdministracionNombre; }
    public void setViaAdministracionNombre(String viaAdministracionNombre) { this.viaAdministracionNombre = viaAdministracionNombre; }

    public String getInstrucciones() { return instrucciones; }
    public void setInstrucciones(String instrucciones) { this.instrucciones = instrucciones; }

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public Integer getCantidadDispensada() { return cantidadDispensada; }
    public void setCantidadDispensada(Integer cantidadDispensada) { this.cantidadDispensada = cantidadDispensada; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
