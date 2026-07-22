package com.gestionvet.veterinariaapi.controller;

import com.gestionvet.veterinariaapi.dto.*;
import com.gestionvet.veterinariaapi.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Gestión de productos, lotes, proveedores y movimientos de stock")
@SecurityRequirement(name = "bearerAuth")
public class InventarioController {

    @Autowired
    private InventarioService service;

    // ══════════════════════════════════════════════════════════════════════
    //  ALERTAS DASHBOARD
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/alertas")
    @Operation(summary = "Alertas de inventario para el dashboard",
               description = "Stock bajo, lotes próximos a vencer y lotes ya vencidos. " +
                             "El parámetro 'dias' define la ventana para próximos vencimientos (default: 30).")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<AlertaInventarioDTO> alertas(
            @Parameter(description = "Días hacia adelante para buscar vencimientos")
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(service.obtenerAlertas(dias));
    }

    // ══════════════════════════════════════════════════════════════════════
    //  CATEGORÍAS
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías de productos")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<List<CategoriaProductoDTO>> listarCategorias() {
        return ResponseEntity.ok(service.listarCategorias());
    }

    @GetMapping("/categorias/{id}")
    @Operation(summary = "Buscar categoría por ID")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<CategoriaProductoDTO> buscarCategoria(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarCategoriaPorId(id));
    }

    @PostMapping("/categorias")
    @Operation(summary = "Crear categoría de producto")
    @ApiResponse(responseCode = "201", description = "Creada correctamente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaProductoDTO> crearCategoria(@Valid @RequestBody CategoriaProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCategoria(dto));
    }

    @PutMapping("/categorias/{id}")
    @Operation(summary = "Actualizar categoría")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaProductoDTO> actualizarCategoria(
            @PathVariable Integer id, @Valid @RequestBody CategoriaProductoDTO dto) {
        return ResponseEntity.ok(service.actualizarCategoria(id, dto));
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PROVEEDORES
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/proveedores")
    @Operation(summary = "Listar proveedores paginados")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<Page<ProveedorDTO>> listarProveedores(
            @RequestParam(defaultValue = "0")        int page,
            @RequestParam(defaultValue = "20")       int size,
            @RequestParam(defaultValue = "nombre")   String sort,
            @RequestParam(defaultValue = "asc")      String dir) {

        Pageable pageable = PageRequest.of(page, size,
                dir.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(service.listarProveedores(pageable));
    }

    @GetMapping("/proveedores/{id}")
    @Operation(summary = "Buscar proveedor por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<ProveedorDTO> buscarProveedor(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarProveedorPorId(id));
    }

    @PostMapping("/proveedores")
    @Operation(summary = "Crear proveedor")
    @ApiResponse(responseCode = "201", description = "Creado correctamente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProveedorDTO> crearProveedor(@Valid @RequestBody ProveedorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearProveedor(dto));
    }

    @PutMapping("/proveedores/{id}")
    @Operation(summary = "Actualizar proveedor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProveedorDTO> actualizarProveedor(
            @PathVariable Integer id, @Valid @RequestBody ProveedorDTO dto) {
        return ResponseEntity.ok(service.actualizarProveedor(id, dto));
    }

    @DeleteMapping("/proveedores/{id}")
    @Operation(summary = "Desactivar proveedor (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Desactivado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarProveedor(@PathVariable Integer id) {
        service.desactivarProveedor(id);
        return ResponseEntity.noContent().build();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PRODUCTOS
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/productos")
    @Operation(summary = "Listar productos paginados")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<ProductoDTO>> listarProductos(
            @RequestParam(defaultValue = "0")        int page,
            @RequestParam(defaultValue = "20")       int size,
            @RequestParam(defaultValue = "nombre")   String sort,
            @RequestParam(defaultValue = "asc")      String dir) {

        Pageable pageable = PageRequest.of(page, size,
                dir.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ResponseEntity.ok(service.listarProductos(pageable));
    }

    @GetMapping("/productos/buscar")
    @Operation(summary = "Buscar productos por nombre (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<ProductoDTO>> buscarProductos(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return ResponseEntity.ok(service.buscarProductosPorNombre(nombre, pageable));
    }

    @GetMapping("/productos/categoria/{categoriaId}")
    @Operation(summary = "Listar productos por categoría (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<ProductoDTO>> porCategoria(
            @PathVariable Integer categoriaId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return ResponseEntity.ok(service.listarProductosPorCategoria(categoriaId, pageable));
    }

    @GetMapping("/productos/{id}")
    @Operation(summary = "Buscar producto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<ProductoDTO> buscarProducto(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarProductoPorId(id));
    }

    @PostMapping("/productos")
    @Operation(summary = "Crear producto",
               description = "El stock inicial es 0. Para agregar stock use el endpoint de lotes.")
    @ApiResponse(responseCode = "201", description = "Creado correctamente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearProducto(dto));
    }

    @PutMapping("/productos/{id}")
    @Operation(summary = "Actualizar producto")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable Integer id, @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(service.actualizarProducto(id, dto));
    }

    @DeleteMapping("/productos/{id}")
    @Operation(summary = "Desactivar producto (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Desactivado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Integer id) {
        service.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  LOTES
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/lotes/producto/{productoId}")
    @Operation(summary = "Listar lotes de un producto (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<LoteProductoDTO>> lotesPorProducto(
            @PathVariable Integer productoId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaVencimiento").ascending());
        return ResponseEntity.ok(service.listarLotesPorProducto(productoId, pageable));
    }

    @GetMapping("/lotes/{id}")
    @Operation(summary = "Buscar lote por ID")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<LoteProductoDTO> buscarLote(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarLotePorId(id));
    }

    @PostMapping("/lotes")
    @Operation(summary = "Registrar ingreso de lote (compra/recepción)",
               description = "Aumenta automáticamente el stock del producto.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Lote registrado y stock actualizado"),
        @ApiResponse(responseCode = "404", description = "Producto o proveedor no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ResponseEntity<LoteProductoDTO> registrarLote(@Valid @RequestBody LoteProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarLote(dto));
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MOVIMIENTOS
    // ══════════════════════════════════════════════════════════════════════

    @GetMapping("/movimientos/producto/{productoId}")
    @Operation(summary = "Historial de movimientos de un producto (paginado)")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<Page<MovimientoInventarioDTO>> movimientosPorProducto(
            @PathVariable Integer productoId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.movimientosPorProducto(productoId, pageable));
    }

    @PostMapping("/movimientos/salida")
    @Operation(summary = "Registrar salida manual de stock",
               description = "Para dispensación directa sin tratamiento asociado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Salida registrada"),
        @ApiResponse(responseCode = "400", description = "Stock insuficiente")
    })
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    public ResponseEntity<MovimientoInventarioDTO> salidaManual(@Valid @RequestBody MovimientoInventarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarSalidaManual(dto));
    }
}
