package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lote de un producto: permite rastrear vencimiento y stock por lote.
 * El stock_actual del Producto es la suma de todos sus lotes activos.
 */
@Entity
@Table(name = "lote_producto")
public class LoteProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Column(name = "numero_lote", nullable = false, length = 50)
    private String numeroLote;

    @Column(name = "fecha_fabricacion")
    private LocalDate fechaFabricacion;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;

    @Column(name = "cantidad_actual", nullable = false)
    private Integer cantidadActual;

    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra;

    // activo = disponible, agotado = sin stock, vencido = fecha vencimiento pasó
    @Column(name = "estado", nullable = false, length = 10)
    private String estado = "activo";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) this.estado = "activo";
    }

    public LoteProducto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
