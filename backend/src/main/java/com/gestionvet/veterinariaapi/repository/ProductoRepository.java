package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    Optional<Producto> findByCodigo(String codigo);
    Page<Producto> findByEstado(String estado, Pageable pageable);
    Page<Producto> findByCategoriaId(Integer categoriaId, Pageable pageable);
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Productos con stock bajo (stock_actual <= stock_minimo) y activos
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo AND p.estado = 'activo'")
    List<Producto> findStockBajo();

    // Actualizar stock directamente en BD (delta positivo = entrada, negativo = salida)
    @Modifying
    @Query("UPDATE Producto p SET p.stockActual = p.stockActual + :delta WHERE p.id = :id")
    void actualizarStock(@Param("id") Integer id, @Param("delta") int delta);
}
