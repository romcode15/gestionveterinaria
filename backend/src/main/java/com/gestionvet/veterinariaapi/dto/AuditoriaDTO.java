package com.gestionvet.veterinariaapi.dto;

import java.time.LocalDateTime;

public class AuditoriaDTO {

    private Long          id;
    private Integer       usuarioId;
    private String        username;
    private String        accion;
    private String        entidad;
    private String        entidadId;
    private String        descripcion;
    private String        ipOrigen;
    private String        endpoint;
    private Boolean       exitoso;
    private String        errorMensaje;
    private LocalDateTime createdAt;

    public AuditoriaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
