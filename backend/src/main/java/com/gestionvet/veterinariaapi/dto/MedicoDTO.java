package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

public class MedicoDTO {

    private Integer id;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    private String numeroDocumento;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email es inválido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "El número de licencia es obligatorio")
    private String numeroLicencia;

    private Boolean disponible;
    private String estado;
    private LocalDateTime createdAt;

    @NotEmpty(message = "Debe asignar al menos una especialidad")
    private Set<Integer> especialidadesIds;

    // Para la respuesta: nombres de especialidades
    private Set<String> especialidadesNombres;

    // ── Constructores ──────────────────────────────────────────────────────

    public MedicoDTO() {}

    // ── Getters y Setters ──────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNumeroLicencia() { return numeroLicencia; }
    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<Integer> getEspecialidadesIds() { return especialidadesIds; }
    public void setEspecialidadesIds(Set<Integer> especialidadesIds) { this.especialidadesIds = especialidadesIds; }

    public Set<String> getEspecialidadesNombres() { return especialidadesNombres; }
    public void setEspecialidadesNombres(Set<String> especialidadesNombres) { this.especialidadesNombres = especialidadesNombres; }
}
