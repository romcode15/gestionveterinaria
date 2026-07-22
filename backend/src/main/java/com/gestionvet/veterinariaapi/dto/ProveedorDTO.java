package com.gestionvet.veterinariaapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ProveedorDTO {

    private Integer id;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 150)
    private String nombre;

    @Size(max = 20)
    private String ruc;

    @Size(max = 100)
    private String contacto;

    @Size(max = 20)
    private String telefono;

    @Email(message = "El formato del email es inválido")
    private String email;

    @Size(max = 255)
    private String direccion;

    private String estado;

    private LocalDateTime createdAt;

    public ProveedorDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
