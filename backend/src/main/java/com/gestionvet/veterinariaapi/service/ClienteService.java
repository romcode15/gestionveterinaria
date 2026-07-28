package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.ClienteDTO;
import com.gestionvet.veterinariaapi.entity.Cliente;
import com.gestionvet.veterinariaapi.entity.Usuario;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.ClienteRepository;
import com.gestionvet.veterinariaapi.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private UsuarioService usuarioService;

    // ── Listar todos (paginado) ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ClienteDTO> listarTodos(Pageable pageable) {
        return clienteRepository.findAll(pageable).map(this::toDTO);
    }

    // ── Buscar por ID ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ClienteDTO buscarPorId(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return toDTO(cliente);
    }

    // ── Buscar por nombre (paginado) ───────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ClienteDTO> buscarPorNombre(String nombre, Pageable pageable) {
        return clienteRepository
                .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(nombre, nombre, pageable)
                .map(this::toDTO);
    }

    // ── Listar por estado (paginado) ───────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<ClienteDTO> listarPorEstado(String estado, Pageable pageable) {
        return clienteRepository.findByEstado(estado, pageable).map(this::toDTO);
    }

    // ── Búsqueda combinada con filtros opcionales ──────────────────────────

    /**
     * Punto de entrada único para listar/filtrar clientes.
     * Cualquier combinación funciona simultáneamente:
     *   busqueda=null, estado=null    → todos
     *   busqueda="juan", estado=null  → solo por texto
     *   busqueda=null, estado="activo"→ solo por estado
     *   busqueda="juan", estado="activo" → texto AND estado (combinado real)
     */
    @Transactional(readOnly = true)
    public Page<ClienteDTO> buscarConFiltros(String busqueda, String estado, Pageable pageable) {
        String b = (busqueda != null && !busqueda.isBlank()) ? busqueda.trim() : null;
        String e = (estado   != null && !estado.isBlank() && !"todos".equals(estado)) ? estado.trim() : null;
        return clienteRepository.buscarCombinado(b, e, pageable).map(this::toDTO);
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
        cliente = clienteRepository.save(cliente);

        // Auto-crear usuario con rol 'cliente' — username y password = numeroDocumento
        Usuario usuario = usuarioService.crearUsuarioAutomatico(
                dto.getNombre(), dto.getApellido(),
                dto.getEmail(), dto.getNumeroDocumento(), "cliente");

        // Vincular bidireccionalmente
        usuario.setCliente(cliente);
        cliente.setUsuario(usuario);
        clienteRepository.save(cliente);

        return toDTO(cliente);
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

    // ── Desactivar (borrado lógico) ────────────────────────────────────────

    public void eliminar(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        cliente.setEstado("inactivo");
        clienteRepository.save(cliente);
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
        // Contar mascotas en tiempo real — navegando la relación cliente.id
        dto.setNumeroMascotas((int) mascotaRepository.countByCliente_Id(c.getId()));
        if (c.getUsuario() != null) {
            dto.setUsuarioId(c.getUsuario().getId());
            dto.setUsername(c.getUsuario().getUsername());
        }
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
