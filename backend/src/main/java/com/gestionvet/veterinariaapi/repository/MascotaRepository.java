package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Mascota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    Page<Mascota> findByClienteId(Integer clienteId, Pageable pageable);
    List<Mascota> findByClienteId(Integer clienteId);
    Page<Mascota> findByEstado(String estado, Pageable pageable);
    long countByEstado(String estado);
    Page<Mascota> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    List<Mascota> findByEspecieId(Integer especieId);
    long countByCliente_Id(Integer clienteId);

    // Mascotas que tienen citas con un médico específico (sin duplicados)
    @Query("""
        SELECT DISTINCT m FROM Mascota m
        WHERE m.id IN (
            SELECT ci.mascota.id FROM Cita ci WHERE ci.medico.id = :medicoId
        )
        ORDER BY m.nombre ASC
        """)
    Page<Mascota> findByMedicoId(@Param("medicoId") Integer medicoId, Pageable pageable);

    @Query("""
        SELECT DISTINCT m FROM Mascota m
        WHERE m.id IN (
            SELECT ci.mascota.id FROM Cita ci WHERE ci.medico.id = :medicoId
        )
        AND LOWER(m.nombre) LIKE LOWER(CONCAT('%',:q,'%'))
        ORDER BY m.nombre ASC
        """)
    Page<Mascota> findByMedicoIdAndNombre(@Param("medicoId") Integer medicoId,
                                          @Param("q") String q,
                                          Pageable pageable);
}
