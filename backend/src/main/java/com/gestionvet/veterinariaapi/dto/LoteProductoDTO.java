package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoteProductoDTO {

    private Integer id;

    @NotNull(message = "El producto es obligatorio")
    private Integer productoId;
    private String  productoNombre;
    private String  productoUnidadMedida;

    private Integer proveedorId;
    private String  proveedorNombre;

    @NotBlank(message = "El número de lote es obligatorio")
    @Size(max = 50)
    private String numeroLote;

    private LocalDate fechaFabricacion;
    private LocalDate fechaVencimiento;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidadInicial;

    private Integer cantidadActual;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de compra debe ser mayor a 0")
    private BigDecimal precioCompra;

    private String estado;

    // Calculado: días hasta el vencimiento (negativo = ya venció)
    private Long diasParaVencer;

    private LocalDateTime createdAt;

    public LoteProductoDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public String getProductoUnidadMedida() { return productoUnidadMedida; }
    public void setProductoUnidadMedida(String productoUnidadMedida) { this.productoUnidadMedida = productoUnidadMedida; }

    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }

    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public LocalDate getFechaFabricacion() { return fechaFabricacion; }
    public void setFechaFabricacion(LocalDate fechaFabricacion) { this.fechaFabricacion = fechaFabricacion; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Integer getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(Integer cantidadInicial) { this.cantidadInicial = cantidadInicial; }

    public Integer getCantidadActual() { return cantidadActual; }
    public void setCantidadActual(Integer cantidadActual) { this.cantidadActual = cantidadActual; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getDiasParaVencer() { return diasParaVencer; }
    public void setDiasParaVencer(Long diasParaVencer) { this.diasParaVencer = diasParaVencer; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
