package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registro de cada movimiento de stock: entrada, salida, ajuste.
 * - ENTRADA: compra o recepción de lote
 * - SALIDA_TRATAMIENTO: descuento automático al registrar tratamiento
 * - SALIDA_MANUAL: dispensación directa sin tratamiento
 * - AJUSTE: corrección de inventario físico
 * - DEVOLUCION: producto devuelto al stock
 */
@Entity
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lote_id")
    private LoteProducto lote;

    @Column(name = "tipo_movimiento", nullable = false, length = 25)
    private String tipoMovimiento;
    // ENTRADA | SALIDA_TRATAMIENTO | SALIDA_MANUAL | AJUSTE | DEVOLUCION

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    // Stock del producto ANTES del movimiento (para auditoría)
    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    // Stock del producto DESPUÉS del movimiento
    @Column(name = "stock_posterior", nullable = false)
    private Integer stockPosterior;

    // Referencia al tratamiento si fue SALIDA_TRATAMIENTO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tratamiento_detalle_id")
    private TratamientoDetalle tratamientoDetalle;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public MovimientoInventario() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public LoteProducto getLote() { return lote; }
    public void setLote(LoteProducto lote) { this.lote = lote; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(Integer stockAnterior) { this.stockAnterior = stockAnterior; }

    public Integer getStockPosterior() { return stockPosterior; }
    public void setStockPosterior(Integer stockPosterior) { this.stockPosterior = stockPosterior; }

    public TratamientoDetalle getTratamientoDetalle() { return tratamientoDetalle; }
    public void setTratamientoDetalle(TratamientoDetalle tratamientoDetalle) { this.tratamientoDetalle = tratamientoDetalle; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
