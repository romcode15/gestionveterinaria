package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    List<Mascota> findByClienteId(Integer clienteId);
    List<Mascota> findByEstado(String estado);
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
    List<Mascota> findByEspecieId(Integer especieId);
}
