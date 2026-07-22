package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Línea individual del tratamiento: un medicamento con su dosis,
 * frecuencia, duración y vía de administración.
 */
@Entity
@Table(name = "tratamiento_detalle")
public class TratamientoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tratamiento_id", nullable = false)
    private Tratamiento tratamiento;

    @Column(name = "medicamento", nullable = false, length = 200)
    private String medicamento;

    @Column(name = "dosis", nullable = false, length = 100)
    private String dosis;

    @Column(name = "frecuencia", nullable = false, length = 100)
    // Ej: "cada 8 horas", "una vez al día"
    private String frecuencia;

    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "via_administracion_id", nullable = false)
    private ViaAdministracion viaAdministracion;

    // Producto del inventario asociado (opcional).
    // Si se especifica, al guardar el tratamiento se descuenta stock automáticamente.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Cantidad a descontar del stock cuando producto_id está presente
    @Column(name = "cantidad_dispensada")
    private Integer cantidadDispensada;

    @Column(name = "instrucciones", columnDefinition = "TEXT")
    private String instrucciones;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public TratamientoDetalle() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Tratamiento getTratamiento() { return tratamiento; }
    public void setTratamiento(Tratamiento tratamiento) { this.tratamiento = tratamiento; }

    public String getMedicamento() { return medicamento; }
    public void setMedicamento(String medicamento) { this.medicamento = medicamento; }

    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

    public Integer getDuracionDias() { return duracionDias; }
    public void setDuracionDias(Integer duracionDias) { this.duracionDias = duracionDias; }

    public ViaAdministracion getViaAdministracion() { return viaAdministracion; }
    public void setViaAdministracion(ViaAdministracion viaAdministracion) { this.viaAdministracion = viaAdministracion; }

    public String getInstrucciones() { return instrucciones; }
    public void setInstrucciones(String instrucciones) { this.instrucciones = instrucciones; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidadDispensada() { return cantidadDispensada; }
    public void setCantidadDispensada(Integer cantidadDispensada) { this.cantidadDispensada = cantidadDispensada; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
