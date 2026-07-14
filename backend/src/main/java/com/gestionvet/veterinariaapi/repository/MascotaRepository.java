package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Mascota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    Page<Mascota> findByClienteId(Integer clienteId, Pageable pageable);
    List<Mascota> findByClienteId(Integer clienteId);
    Page<Mascota> findByEstado(String estado, Pageable pageable);
    Page<Mascota> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    List<Mascota> findByEspecieId(Integer especieId);
}
