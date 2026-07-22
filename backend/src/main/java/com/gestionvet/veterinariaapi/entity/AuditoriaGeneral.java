package com.gestionvet.veterinariaapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registro de auditoría inmutable.
 * Cada fila representa una acción realizada por un usuario en el sistema.
 * No se actualiza ni elimina — solo se inserta.
 */
@Entity
@Table(name = "auditoria_general")
public class AuditoriaGeneral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quién
    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "username", length = 100)
    private String username;

    // Qué
    @Column(name = "accion", nullable = false, length = 50)
    // CREATE | UPDATE | DELETE | LOGIN | LOGOUT
    private String accion;

    @Column(name = "entidad", nullable = false, length = 100)
    // Nombre de la entidad afectada: Cliente, Cita, Diagnostico, etc.
    private String entidad;

    @Column(name = "entidad_id", length = 50)
    // ID del registro afectado (puede ser nulo en acciones globales)
    private String entidadId;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    // Detalle legible: "Creó cliente Carlos Rodríguez (id=5)"
    private String descripcion;

    // Dónde
    @Column(name = "ip_origen", length = 50)
    private String ipOrigen;

    @Column(name = "endpoint", length = 255)
    private String endpoint;

    // Resultado
    @Column(name = "exitoso", nullable = false)
    private Boolean exitoso = true;

    @Column(name = "error_mensaje", columnDefinition = "TEXT")
    private String errorMensaje;

    // Cuándo
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.exitoso == null) this.exitoso = true;
    }

    public AuditoriaGeneral() {}

    // Constructor de conveniencia para uso en el aspecto
    public AuditoriaGeneral(String username, Integer usuarioId, String accion,
                             String entidad, String entidadId, String descripcion,
                             String ipOrigen, String endpoint, boolean exitoso,
                             String errorMensaje) {
        this.username      = username;
        this.usuarioId     = usuarioId;
        this.accion        = accion;
        this.entidad       = entidad;
        this.entidadId     = entidadId;
        this.descripcion   = descripcion;
        this.ipOrigen      = ipOrigen;
        this.endpoint      = endpoint;
        this.exitoso       = exitoso;
        this.errorMensaje  = errorMensaje;
    }

    public Long getId() { return id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }

    public String getEntidadId() { return entidadId; }
    public void setEntidadId(String entidadId) { this.entidadId = entidadId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getIpOrigen() { return ipOrigen; }
    public void setIpOrigen(String ipOrigen) { this.ipOrigen = ipOrigen; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public Boolean getExitoso() { return exitoso; }
    public void setExitoso(Boolean exitoso) { this.exitoso = exitoso; }

    public String getErrorMensaje() { return errorMensaje; }
    public void setErrorMensaje(String errorMensaje) { this.errorMensaje = errorMensaje; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
