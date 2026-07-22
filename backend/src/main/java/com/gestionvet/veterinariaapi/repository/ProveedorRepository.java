package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    Optional<Proveedor> findByRuc(String ruc);
    Page<Proveedor> findByEstado(String estado, Pageable pageable);
    Page<Proveedor> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
