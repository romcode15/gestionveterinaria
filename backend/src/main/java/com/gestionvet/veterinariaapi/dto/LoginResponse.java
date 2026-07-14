package com.gestionvet.veterinariaapi.dto;

import java.util.Set;

public class LoginResponse {

    private String token;
    private String tipo = "Bearer";
    private Integer id;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private Set<String> roles;

    // ── Constructor completo ───────────────────────────────────────────────

    public LoginResponse() {}

    public LoginResponse(String token, Integer id, String username,
                         String email, String nombre, String apellido,
                         Set<String> roles) {
        this.token    = token;
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.nombre   = nombre;
        this.apellido = apellido;
        this.roles    = roles;
    }

    // ── Getters y Setters ──────────────────────────────────────────────────

    public String getToken()    { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo()     { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getId()      { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail()    { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre()   { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
