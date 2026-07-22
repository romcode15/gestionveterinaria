package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

public class UsuarioDTO {

    private Integer id;

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;

    // Solo se usa en creación/actualización, no se retorna en respuestas
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email es inválido")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    private Boolean activo;
    private LocalDateTime ultimoAcceso;
    private LocalDateTime createdAt;
    private Integer clienteId;
    private Integer medicoId;   // vinculación con tabla medicos (rol VETERINARIO)

    @NotEmpty(message = "Debe asignar al menos un rol")
    private Set<Integer> rolesIds;

    private Set<String> rolesNombres;

    // ── Constructores ──────────────────────────────────────────────────────

    public UsuarioDTO() {}

    // ── Getters y Setters ──────────────────────────────────────────────────

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public Integer getMedicoId() { return medicoId; }
    public void setMedicoId(Integer medicoId) { this.medicoId = medicoId; }

    public Set<Integer> getRolesIds() { return rolesIds; }
    public void setRolesIds(Set<Integer> rolesIds) { this.rolesIds = rolesIds; }

    public Set<String> getRolesNombres() { return rolesNombres; }
    public void setRolesNombres(Set<String> rolesNombres) { this.rolesNombres = rolesNombres; }
}
