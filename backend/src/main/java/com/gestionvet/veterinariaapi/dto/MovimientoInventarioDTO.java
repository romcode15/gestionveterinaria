package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class MovimientoInventarioDTO {

    private Integer id;

    @NotNull(message = "El producto es obligatorio")
    private Integer productoId;
    private String  productoNombre;

    private Integer loteId;
    private String  loteNumero;

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipoMovimiento;
    // ENTRADA | SALIDA_TRATAMIENTO | SALIDA_MANUAL | AJUSTE | DEVOLUCION

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private Integer stockAnterior;
    private Integer stockPosterior;

    private Integer tratamientoDetalleId;

    @Size(max = 255)
    private String motivo;

    private Integer usuarioId;

    private LocalDateTime createdAt;

    public MovimientoInventarioDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public Integer getLoteId() { return loteId; }
    public void setLoteId(Integer loteId) { this.loteId = loteId; }

    public String getLoteNumero() { return loteNumero; }
    public void setLoteNumero(String loteNumero) { this.loteNumero = loteNumero; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(Integer stockAnterior) { this.stockAnterior = stockAnterior; }

    public Integer getStockPosterior() { return stockPosterior; }
    public void setStockPosterior(Integer stockPosterior) { this.stockPosterior = stockPosterior; }

    public Integer getTratamientoDetalleId() { return tratamientoDetalleId; }
    public void setTratamientoDetalleId(Integer tratamientoDetalleId) { this.tratamientoDetalleId = tratamientoDetalleId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
