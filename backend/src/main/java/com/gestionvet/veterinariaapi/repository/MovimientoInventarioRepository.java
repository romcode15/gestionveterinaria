package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.MovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    Page<MovimientoInventario> findByProductoId(Integer productoId, Pageable pageable);
    Page<MovimientoInventario> findByTipoMovimiento(String tipoMovimiento, Pageable pageable);

    // ── Productos más usados en tratamientos ──────────────────────────────
    // Devuelve [productoId, productoNombre, totalUnidades] ordenado desc
    @Query("""
        SELECT m.producto.id, m.producto.nombre, SUM(m.cantidad) AS total
        FROM MovimientoInventario m
        WHERE m.tipoMovimiento = 'SALIDA_TRATAMIENTO'
          AND m.createdAt BETWEEN :inicio AND :fin
        GROUP BY m.producto.id, m.producto.nombre
        ORDER BY total DESC
        """)
    List<Object[]> productosMasUsados(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin")    LocalDateTime fin);

    // ── Total de salidas por tipo en rango ────────────────────────────────
    @Query("""
        SELECT m.tipoMovimiento, SUM(m.cantidad) AS total
        FROM MovimientoInventario m
        WHERE m.createdAt BETWEEN :inicio AND :fin
        GROUP BY m.tipoMovimiento
        ORDER BY total DESC
        """)
    List<Object[]> movimientosPorTipo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin")    LocalDateTime fin);
}
