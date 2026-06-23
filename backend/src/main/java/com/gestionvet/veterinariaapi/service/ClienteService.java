package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.ClienteDTO;
import com.gestionvet.veterinariaapi.entity.Cliente;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // ── Listar todos ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Buscar por ID ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ClienteDTO buscarPorId(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return toDTO(cliente);
    }

    // ── Buscar por nombre ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ClienteDTO> buscarPorNombre(String nombre) {
        return clienteRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(nombre, nombre)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Listar por estado ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarPorEstado(String estado) {
        return clienteRepository.findByEstado(estado)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Crear ──────────────────────────────────────────────────────────────

    public ClienteDTO crear(ClienteDTO dto) {
        // Validar unicidad de documento
        if (clienteRepository.findByNumeroDocumento(dto.getNumeroDocumento()).isPresent()) {
            throw new IllegalArgumentException(
                "Ya existe un cliente con el documento: " + dto.getNumeroDocumento());
        }
        // Validar unicidad de email
        if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                "Ya existe un cliente con el email: " + dto.getEmail());
        }
        Cliente cliente = toEntity(dto);
        return toDTO(clienteRepository.save(cliente));
    }

    // ── Actualizar ─────────────────────────────────────────────────────────

    public ClienteDTO actualizar(Integer id, ClienteDTO dto) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // Validar email único si cambió
        if (!existente.getEmail().equalsIgnoreCase(dto.getEmail())) {
            if (clienteRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException(
                    "Ya existe un cliente con el email: " + dto.getEmail());
            }
        }

        existente.setTipoDocumento(dto.getTipoDocumento());
        existente.setNumeroDocumento(dto.getNumeroDocumento());
        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setEmail(dto.getEmail());
        existente.setTelefono(dto.getTelefono());
        existente.setDireccion(dto.getDireccion());
        existente.setCiudad(dto.getCiudad());
        existente.setFechaNacimiento(dto.getFechaNacimiento());
        if (dto.getEstado() != null) existente.setEstado(dto.getEstado());
        existente.setObservaciones(dto.getObservaciones());

        return toDTO(clienteRepository.save(existente));
    }

    // ── Eliminar ───────────────────────────────────────────────────────────

    public void eliminar(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", "id", id);
        }
        clienteRepository.deleteById(id);
    }

    // ── Conversiones ───────────────────────────────────────────────────────

    private ClienteDTO toDTO(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getId());
        dto.setTipoDocumento(c.getTipoDocumento());
        dto.setNumeroDocumento(c.getNumeroDocumento());
        dto.setNombre(c.getNombre());
        dto.setApellido(c.getApellido());
        dto.setEmail(c.getEmail());
        dto.setTelefono(c.getTelefono());
        dto.setDireccion(c.getDireccion());
        dto.setCiudad(c.getCiudad());
        dto.setFechaNacimiento(c.getFechaNacimiento());
        dto.setEstado(c.getEstado());
        dto.setObservaciones(c.getObservaciones());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }

    private Cliente toEntity(ClienteDTO dto) {
        Cliente c = new Cliente();
        c.setTipoDocumento(dto.getTipoDocumento());
        c.setNumeroDocumento(dto.getNumeroDocumento());
        c.setNombre(dto.getNombre());
        c.setApellido(dto.getApellido());
        c.setEmail(dto.getEmail());
        c.setTelefono(dto.getTelefono());
        c.setDireccion(dto.getDireccion());
        c.setCiudad(dto.getCiudad());
        c.setFechaNacimiento(dto.getFechaNacimiento());
        c.setEstado(dto.getEstado() != null ? dto.getEstado() : "activo");
        c.setObservaciones(dto.getObservaciones());
        return c;
    }
}
