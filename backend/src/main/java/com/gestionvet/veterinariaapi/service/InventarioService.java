package com.gestionvet.veterinariaapi.service;

import com.gestionvet.veterinariaapi.dto.*;
import com.gestionvet.veterinariaapi.dto.AlertaInventarioDTO.*;
import com.gestionvet.veterinariaapi.entity.*;
import com.gestionvet.veterinariaapi.exception.ResourceNotFoundException;
import com.gestionvet.veterinariaapi.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventarioService {

    private static final Logger log = LoggerFactory.getLogger(InventarioService.class);

    @Autowired private ProductoRepository            productoRepository;
    @Autowired private CategoriaProductoRepository   categoriaRepository;
    @Autowired private ProveedorRepository           proveedorRepository;
    @Autowired private LoteProductoRepository        loteRepository;
    @Autowired private MovimientoInventarioRepository movimientoRepository;

    // ══════════════════════════════════════════════════════════════════════
    //  CATEGORÍAS
    // ══════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public List<CategoriaProductoDTO> listarCategorias() {
        return categoriaRepository.findAll().stream().map(this::toCategoriaDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaProductoDTO buscarCategoriaPorId(Integer id) {
        return toCategoriaDTO(categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CategoriaProducto", "id", id)));
    }

    public CategoriaProductoDTO crearCategoria(CategoriaProductoDTO dto) {
        if (categoriaRepository.findByNombreIgnoreCase(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + dto.getNombre());
        }
        CategoriaProducto cat = new CategoriaProducto();
        cat.setNombre(dto.getNombre());
        cat.setDescripcion(dto.getDescripcion());
        return toCategoriaDTO(categoriaRepository.save(cat));
    }

    public CategoriaProductoDTO actualizarCategoria(Integer id, CategoriaProductoDTO dto) {
        CategoriaProducto existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CategoriaProducto", "id", id));
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        return toCategoriaDTO(categoriaRepository.save(existente));
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PROVEEDORES
    // ══════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public Page<ProveedorDTO> listarProveedores(Pageable pageable) {
        return proveedorRepository.findAll(pageable).map(this::toProveedorDTO);
    }

    @Transactional(readOnly = true)
    public ProveedorDTO buscarProveedorPorId(Integer id) {
        return toProveedorDTO(proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id)));
    }

    public ProveedorDTO crearProveedor(ProveedorDTO dto) {
        if (dto.getRuc() != null && proveedorRepository.findByRuc(dto.getRuc()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un proveedor con el RUC: " + dto.getRuc());
        }
        Proveedor p = toProveedorEntity(dto);
        Proveedor saved = proveedorRepository.save(p);
        log.info("Proveedor creado: id={}, nombre={}", saved.getId(), saved.getNombre());
        return toProveedorDTO(saved);
    }

    public ProveedorDTO actualizarProveedor(Integer id, ProveedorDTO dto) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));
        existente.setNombre(dto.getNombre());
        existente.setRuc(dto.getRuc());
        existente.setContacto(dto.getContacto());
        existente.setTelefono(dto.getTelefono());
        existente.setEmail(dto.getEmail());
        existente.setDireccion(dto.getDireccion());
        if (dto.getEstado() != null) existente.setEstado(dto.getEstado());
        return toProveedorDTO(proveedorRepository.save(existente));
    }

    public void desactivarProveedor(Integer id) {
        Proveedor p = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));
        p.setEstado("inactivo");
        proveedorRepository.save(p);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PRODUCTOS
    // ══════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public Page<ProductoDTO> listarProductos(Pageable pageable) {
        return productoRepository.findAll(pageable).map(this::toProductoDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductoDTO> buscarProductosPorNombre(String nombre, Pageable pageable) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre, pageable).map(this::toProductoDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductoDTO> listarProductosPorCategoria(Integer categoriaId, Pageable pageable) {
        return productoRepository.findByCategoriaId(categoriaId, pageable).map(this::toProductoDTO);
    }

    @Transactional(readOnly = true)
    public ProductoDTO buscarProductoPorId(Integer id) {
        return toProductoDTO(productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id)));
    }

    public ProductoDTO crearProducto(ProductoDTO dto) {
        if (dto.getCodigo() != null && productoRepository.findByCodigo(dto.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + dto.getCodigo());
        }
        Producto p = toProductoEntity(dto);
        Producto saved = productoRepository.save(p);
        log.info("Producto creado: id={}, nombre={}", saved.getId(), saved.getNombre());
        return toProductoDTO(saved);
    }

    public ProductoDTO actualizarProducto(Integer id, ProductoDTO dto) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setUnidadMedida(dto.getUnidadMedida());
        existente.setPrecioUnitario(dto.getPrecioUnitario());
        existente.setStockMinimo(dto.getStockMinimo());
        if (dto.getRequiereReceta() != null) existente.setRequiereReceta(dto.getRequiereReceta());
        if (dto.getEstado() != null) existente.setEstado(dto.getEstado());
        existente.setCategoria(categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("CategoriaProducto", "id", dto.getCategoriaId())));
        if (dto.getProveedorId() != null) {
            existente.setProveedor(proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", dto.getProveedorId())));
        }
        return toProductoDTO(productoRepository.save(existente));
    }

    public void desactivarProducto(Integer id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        p.setEstado("inactivo");
        productoRepository.save(p);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  LOTES
    // ══════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public Page<LoteProductoDTO> listarLotesPorProducto(Integer productoId, Pageable pageable) {
        return loteRepository.findByProductoId(productoId, pageable).map(this::toLoteDTO);
    }

    @Transactional(readOnly = true)
    public LoteProductoDTO buscarLotePorId(Integer id) {
        return toLoteDTO(loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LoteProducto", "id", id)));
    }

    /**
     * Registrar ingreso de un lote (compra/recepción).
     * Actualiza automáticamente el stock_actual del producto.
     */
    @Transactional(rollbackFor = Exception.class)
    public LoteProductoDTO registrarLote(LoteProductoDTO dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", dto.getProductoId()));

        LoteProducto lote = new LoteProducto();
        lote.setProducto(producto);
        lote.setNumeroLote(dto.getNumeroLote());
        lote.setFechaFabricacion(dto.getFechaFabricacion());
        lote.setFechaVencimiento(dto.getFechaVencimiento());
        lote.setCantidadInicial(dto.getCantidadInicial());
        lote.setCantidadActual(dto.getCantidadInicial());
        lote.setPrecioCompra(dto.getPrecioCompra());
        lote.setEstado("activo");

        if (dto.getProveedorId() != null) {
            lote.setProveedor(proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", dto.getProveedorId())));
        }

        LoteProducto saved = loteRepository.save(lote);

        // Registrar movimiento de ENTRADA y actualizar stock del producto
        registrarMovimiento(producto, saved, "ENTRADA", dto.getCantidadInicial(),
                "Ingreso de lote " + dto.getNumeroLote(), null, null);

        log.info("Lote registrado: productoId={}, lote={}, cantidad={}",
                producto.getId(), dto.getNumeroLote(), dto.getCantidadInicial());
        return toLoteDTO(saved);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MOVIMIENTOS
    // ══════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public Page<MovimientoInventarioDTO> movimientosPorProducto(Integer productoId, Pageable pageable) {
        return movimientoRepository.findByProductoId(productoId, pageable).map(this::toMovimientoDTO);
    }

    /**
     * Salida manual de stock (dispensación sin tratamiento).
     */
    @Transactional(rollbackFor = Exception.class)
    public MovimientoInventarioDTO registrarSalidaManual(MovimientoInventarioDTO dto) {
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", dto.getProductoId()));

        if (producto.getStockActual() < dto.getCantidad()) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Disponible: " + producto.getStockActual()
                    + ", solicitado: " + dto.getCantidad());
        }

        LoteProducto lote = null;
        if (dto.getLoteId() != null) {
            lote = loteRepository.findById(dto.getLoteId())
                    .orElseThrow(() -> new ResourceNotFoundException("LoteProducto", "id", dto.getLoteId()));
            if (lote.getCantidadActual() < dto.getCantidad()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente en el lote " + lote.getNumeroLote()
                        + ". Disponible: " + lote.getCantidadActual());
            }
            lote.setCantidadActual(lote.getCantidadActual() - dto.getCantidad());
            if (lote.getCantidadActual() == 0) lote.setEstado("agotado");
            loteRepository.save(lote);
        }

        MovimientoInventario mov = registrarMovimiento(producto, lote, "SALIDA_MANUAL",
                dto.getCantidad(), dto.getMotivo(), null, dto.getUsuarioId());
        return toMovimientoDTO(mov);
    }

    /**
     * Descuento automático de stock al registrar un tratamiento.
     * Usa FEFO (First Expired, First Out) si no se especifica lote.
     */
    @Transactional(rollbackFor = Exception.class)
    public void descontarStockPorTratamiento(TratamientoDetalle detalle) {
        if (detalle.getProducto() == null || detalle.getCantidadDispensada() == null) return;

        Producto producto = detalle.getProducto();
        int cantidad = detalle.getCantidadDispensada();

        if (producto.getStockActual() < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente para '" + producto.getNombre()
                    + "'. Disponible: " + producto.getStockActual()
                    + ", requerido: " + cantidad);
        }

        // Seleccionar lote por FEFO (el que vence primero)
        List<LoteProducto> lotes = loteRepository.findLotesDisponibles(producto.getId());
        LoteProducto lote = lotes.isEmpty() ? null : lotes.get(0);

        if (lote != null) {
            int descuento = Math.min(cantidad, lote.getCantidadActual());
            lote.setCantidadActual(lote.getCantidadActual() - descuento);
            if (lote.getCantidadActual() == 0) lote.setEstado("agotado");
            loteRepository.save(lote);
        }

        registrarMovimiento(producto, lote, "SALIDA_TRATAMIENTO", cantidad,
                "Tratamiento #" + detalle.getTratamiento().getId(), detalle, null);

        log.info("Stock descontado por tratamiento: producto='{}', cantidad={}, stockRestante={}",
                producto.getNombre(), cantidad, producto.getStockActual() - cantidad);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ALERTAS DASHBOARD
    // ══════════════════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public AlertaInventarioDTO obtenerAlertas(int diasVentana) {
        LocalDate hoy   = LocalDate.now();
        LocalDate hasta = hoy.plusDays(diasVentana);

        List<Producto>     stockBajoList  = productoRepository.findStockBajo();
        List<LoteProducto> proximosLotes  = loteRepository.findLotesProximosAVencer(hoy, hasta);
        List<LoteProducto> vencidosLotes  = loteRepository.findLotesVencidos(hoy);

        // Marcar lotes vencidos automáticamente
        vencidosLotes.forEach(l -> {
            if ("activo".equals(l.getEstado())) {
                l.setEstado("vencido");
                loteRepository.save(l);
            }
        });

        List<ItemStockBajoDTO> stockBajoDTO = stockBajoList.stream().map(p -> {
            ItemStockBajoDTO item = new ItemStockBajoDTO();
            item.setProductoId(p.getId());
            item.setProductoNombre(p.getNombre());
            item.setCategoriaNombre(p.getCategoria().getNombre());
            item.setUnidadMedida(p.getUnidadMedida());
            item.setStockActual(p.getStockActual());
            item.setStockMinimo(p.getStockMinimo());
            item.setDiferencia(p.getStockMinimo() - p.getStockActual());
            return item;
        }).collect(Collectors.toList());

        List<ItemLoteAlertaDTO> proximosDTO = proximosLotes.stream()
                .map(l -> toLoteAlertaDTO(l, hoy)).collect(Collectors.toList());
        List<ItemLoteAlertaDTO> vencidosDTO = vencidosLotes.stream()
                .map(l -> toLoteAlertaDTO(l, hoy)).collect(Collectors.toList());

        AlertaInventarioDTO alerta = new AlertaInventarioDTO();
        alerta.setDiasVentanaConsultados(diasVentana);
        alerta.setTotalStockBajo(stockBajoList.size());
        alerta.setTotalLotesProximosAVencer(proximosLotes.size());
        alerta.setTotalLotesVencidos(vencidosLotes.size());
        alerta.setStockBajo(stockBajoDTO);
        alerta.setLotesProximosAVencer(proximosDTO);
        alerta.setLotesVencidos(vencidosDTO);

        return alerta;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HELPER INTERNO: registrar movimiento + actualizar stock
    // ══════════════════════════════════════════════════════════════════════

    private MovimientoInventario registrarMovimiento(
            Producto producto, LoteProducto lote, String tipo,
            int cantidad, String motivo, TratamientoDetalle detalle, Integer usuarioId) {

        int stockAntes = producto.getStockActual();
        int delta = tipo.startsWith("ENTRADA") || "DEVOLUCION".equals(tipo) ? cantidad : -cantidad;
        if ("AJUSTE".equals(tipo)) delta = cantidad; // puede ser positivo o negativo

        productoRepository.actualizarStock(producto.getId(), delta);
        producto.setStockActual(stockAntes + delta);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setLote(lote);
        mov.setTipoMovimiento(tipo);
        mov.setCantidad(Math.abs(cantidad));
        mov.setStockAnterior(stockAntes);
        mov.setStockPosterior(stockAntes + delta);
        mov.setMotivo(motivo);
        mov.setTratamientoDetalle(detalle);
        mov.setUsuarioId(usuarioId);

        return movimientoRepository.save(mov);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CONVERSIONES
    // ══════════════════════════════════════════════════════════════════════

    private CategoriaProductoDTO toCategoriaDTO(CategoriaProducto c) {
        CategoriaProductoDTO dto = new CategoriaProductoDTO();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setDescripcion(c.getDescripcion());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }

    private ProveedorDTO toProveedorDTO(Proveedor p) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setRuc(p.getRuc());
        dto.setContacto(p.getContacto());
        dto.setTelefono(p.getTelefono());
        dto.setEmail(p.getEmail());
        dto.setDireccion(p.getDireccion());
        dto.setEstado(p.getEstado());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }

    private Proveedor toProveedorEntity(ProveedorDTO dto) {
        Proveedor p = new Proveedor();
        p.setNombre(dto.getNombre());
        p.setRuc(dto.getRuc());
        p.setContacto(dto.getContacto());
        p.setTelefono(dto.getTelefono());
        p.setEmail(dto.getEmail());
        p.setDireccion(dto.getDireccion());
        p.setEstado(dto.getEstado() != null ? dto.getEstado() : "activo");
        return p;
    }

    public ProductoDTO toProductoDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setCodigo(p.getCodigo());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setCategoriaId(p.getCategoria().getId());
        dto.setCategoriaNombre(p.getCategoria().getNombre());
        if (p.getProveedor() != null) {
            dto.setProveedorId(p.getProveedor().getId());
            dto.setProveedorNombre(p.getProveedor().getNombre());
        }
        dto.setUnidadMedida(p.getUnidadMedida());
        dto.setPrecioUnitario(p.getPrecioUnitario());
        dto.setStockActual(p.getStockActual());
        dto.setStockMinimo(p.getStockMinimo());
        dto.setRequiereReceta(p.getRequiereReceta());
        dto.setEstado(p.getEstado());
        dto.setStockBajo(p.getStockActual() <= p.getStockMinimo());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }

    private Producto toProductoEntity(ProductoDTO dto) {
        Producto p = new Producto();
        p.setCodigo(dto.getCodigo());
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setUnidadMedida(dto.getUnidadMedida());
        p.setPrecioUnitario(dto.getPrecioUnitario());
        p.setStockActual(0);
        p.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 5);
        p.setRequiereReceta(dto.getRequiereReceta() != null ? dto.getRequiereReceta() : false);
        p.setEstado("activo");
        p.setCategoria(categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("CategoriaProducto", "id", dto.getCategoriaId())));
        if (dto.getProveedorId() != null) {
            p.setProveedor(proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", dto.getProveedorId())));
        }
        return p;
    }

    public LoteProductoDTO toLoteDTO(LoteProducto l) {
        LoteProductoDTO dto = new LoteProductoDTO();
        dto.setId(l.getId());
        dto.setProductoId(l.getProducto().getId());
        dto.setProductoNombre(l.getProducto().getNombre());
        dto.setProductoUnidadMedida(l.getProducto().getUnidadMedida());
        if (l.getProveedor() != null) {
            dto.setProveedorId(l.getProveedor().getId());
            dto.setProveedorNombre(l.getProveedor().getNombre());
        }
        dto.setNumeroLote(l.getNumeroLote());
        dto.setFechaFabricacion(l.getFechaFabricacion());
        dto.setFechaVencimiento(l.getFechaVencimiento());
        dto.setCantidadInicial(l.getCantidadInicial());
        dto.setCantidadActual(l.getCantidadActual());
        dto.setPrecioCompra(l.getPrecioCompra());
        dto.setEstado(l.getEstado());
        dto.setCreatedAt(l.getCreatedAt());
        if (l.getFechaVencimiento() != null) {
            dto.setDiasParaVencer(ChronoUnit.DAYS.between(LocalDate.now(), l.getFechaVencimiento()));
        }
        return dto;
    }

    private MovimientoInventarioDTO toMovimientoDTO(MovimientoInventario m) {
        MovimientoInventarioDTO dto = new MovimientoInventarioDTO();
        dto.setId(m.getId());
        dto.setProductoId(m.getProducto().getId());
        dto.setProductoNombre(m.getProducto().getNombre());
        if (m.getLote() != null) {
            dto.setLoteId(m.getLote().getId());
            dto.setLoteNumero(m.getLote().getNumeroLote());
        }
        dto.setTipoMovimiento(m.getTipoMovimiento());
        dto.setCantidad(m.getCantidad());
        dto.setStockAnterior(m.getStockAnterior());
        dto.setStockPosterior(m.getStockPosterior());
        if (m.getTratamientoDetalle() != null) dto.setTratamientoDetalleId(m.getTratamientoDetalle().getId());
        dto.setMotivo(m.getMotivo());
        dto.setUsuarioId(m.getUsuarioId());
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }

    private ItemLoteAlertaDTO toLoteAlertaDTO(LoteProducto l, LocalDate hoy) {
        ItemLoteAlertaDTO item = new ItemLoteAlertaDTO();
        item.setLoteId(l.getId());
        item.setNumeroLote(l.getNumeroLote());
        item.setProductoId(l.getProducto().getId());
        item.setProductoNombre(l.getProducto().getNombre());
        item.setCategoriaNombre(l.getProducto().getCategoria().getNombre());
        item.setCantidadActual(l.getCantidadActual());
        item.setUnidadMedida(l.getProducto().getUnidadMedida());
        item.setFechaVencimiento(l.getFechaVencimiento());
        item.setDiasRestantes(ChronoUnit.DAYS.between(hoy, l.getFechaVencimiento()));
        return item;
    }
}
