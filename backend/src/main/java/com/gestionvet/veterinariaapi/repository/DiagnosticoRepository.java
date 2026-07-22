package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Diagnostico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Integer> {

    Optional<Diagnostico> findByCitaId(Integer citaId);
    Page<Diagnostico> findByMascotaId(Integer mascotaId, Pageable pageable);
    Page<Diagnostico> findByMedicoId(Integer medicoId, Pageable pageable);
    boolean existsByCitaId(Integer citaId);

    // ── Mascotas más atendidas en un rango ────────────────────────────────
    @Query("""
        SELECT d.mascota.id, d.mascota.nombre, COUNT(d) AS total
        FROM Diagnostico d
        WHERE d.cita.fecha BETWEEN :inicio AND :fin
        GROUP BY d.mascota.id, d.mascota.nombre
        ORDER BY total DESC
        """)
    List<Object[]> mascotasMasAtendidas(
            @Param("inicio") LocalDate inicio,
            @Param("fin")    LocalDate fin);

    // ── Diagnósticos registrados en un rango ──────────────────────────────
    long countByCitaFechaBetween(LocalDate inicio, LocalDate fin);

    // ── Pronósticos más frecuentes ─────────────────────────────────────────
    @Query("""
        SELECT d.pronostico, COUNT(d) AS total
        FROM Diagnostico d
        WHERE d.cita.fecha BETWEEN :inicio AND :fin
          AND d.pronostico IS NOT NULL
        GROUP BY d.pronostico
        ORDER BY total DESC
        """)
    List<Object[]> pronosticosFrecuentes(
            @Param("inicio") LocalDate inicio,
            @Param("fin")    LocalDate fin);
}
