package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.LoteProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteProductoRepository extends JpaRepository<LoteProducto, Integer> {

    Page<LoteProducto> findByProductoId(Integer productoId, Pageable pageable);

    // Lotes activos de un producto con stock > 0, ordenados por vencimiento (FEFO)
    @Query("""
        SELECT l FROM LoteProducto l
        WHERE l.producto.id = :productoId
          AND l.estado = 'activo'
          AND l.cantidadActual > 0
        ORDER BY l.fechaVencimiento ASC NULLS LAST
        """)
    List<LoteProducto> findLotesDisponibles(@Param("productoId") Integer productoId);

    // Lotes próximos a vencer (alerta dashboard)
    @Query("""
        SELECT l FROM LoteProducto l
        WHERE l.fechaVencimiento IS NOT NULL
          AND l.fechaVencimiento BETWEEN :desde AND :hasta
          AND l.estado = 'activo'
          AND l.cantidadActual > 0
        ORDER BY l.fechaVencimiento ASC
        """)
    List<LoteProducto> findLotesProximosAVencer(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    // Lotes ya vencidos con stock
    @Query("""
        SELECT l FROM LoteProducto l
        WHERE l.fechaVencimiento < :hoy
          AND l.estado = 'activo'
          AND l.cantidadActual > 0
        ORDER BY l.fechaVencimiento ASC
        """)
    List<LoteProducto> findLotesVencidos(@Param("hoy") LocalDate hoy);
}
