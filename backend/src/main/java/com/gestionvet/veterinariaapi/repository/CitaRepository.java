package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Cita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    Page<Cita> findByFecha(LocalDate fecha, Pageable pageable);
    Page<Cita> findByClienteId(Integer clienteId, Pageable pageable);
    Page<Cita> findByMedicoId(Integer medicoId, Pageable pageable);
    Page<Cita> findByEstado(String estado, Pageable pageable);
    List<Cita> findByMascotaId(Integer mascotaId);
    List<Cita> findByFechaBetween(LocalDate inicio, LocalDate fin);

    // ── Conteos para dashboard ─────────────────────────────────────────────

    long countByFecha(LocalDate fecha);

    long countByFechaAndEstado(LocalDate fecha, String estado);

    @Query("SELECT COUNT(DISTINCT c.mascota.id) FROM Cita c WHERE c.fecha = :fecha AND c.estado = 'completada'")
    long countMascotasAtendidasEnFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(c) FROM Cita c WHERE c.fecha BETWEEN :inicio AND :fin")
    long countEnRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(c) FROM Cita c WHERE c.fecha BETWEEN :inicio AND :fin AND c.estado = :estado")
    long countEnRangoYEstado(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, @Param("estado") String estado);

    // ── Médico más activo ──────────────────────────────────────────────────
    // Devuelve [medicoId, medicoNombre, totalCitas] ordenado desc
    @Query("""
        SELECT c.medico.id, c.medico.nombre, c.medico.apellido, COUNT(c) AS total
        FROM Cita c
        WHERE c.fecha BETWEEN :inicio AND :fin
          AND c.estado NOT IN ('cancelada','no_asistio')
        GROUP BY c.medico.id, c.medico.nombre, c.medico.apellido
        ORDER BY total DESC
        """)
    List<Object[]> medicosConMasCitas(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ── Citas por estado en un rango ──────────────────────────────────────
    @Query("""
        SELECT c.estado, COUNT(c) AS total
        FROM Cita c
        WHERE c.fecha BETWEEN :inicio AND :fin
        GROUP BY c.estado
        ORDER BY total DESC
        """)
    List<Object[]> citasPorEstadoEnRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ── Citas por tipo en un rango ─────────────────────────────────────────
    @Query("""
        SELECT c.tipoCita.nombre, COUNT(c) AS total
        FROM Cita c
        WHERE c.fecha BETWEEN :inicio AND :fin
        GROUP BY c.tipoCita.nombre
        ORDER BY total DESC
        """)
    List<Object[]> citasPorTipoEnRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // ── Citas por día de la semana ─────────────────────────────────────────
    @Query(value = """
        SELECT TO_CHAR(fecha, 'Day') AS dia, COUNT(*) AS total
        FROM citas
        WHERE fecha BETWEEN :inicio AND :fin
        GROUP BY TO_CHAR(fecha, 'Day'), EXTRACT(DOW FROM fecha)
        ORDER BY EXTRACT(DOW FROM fecha)
        """, nativeQuery = true)
    List<Object[]> citasPorDiaSemana(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("""
        SELECT COUNT(c) > 0 FROM Cita c
        WHERE c.medico.id = :medicoId
          AND c.fecha      = :fecha
          AND c.estado NOT IN ('cancelada', 'no_asistio')
          AND (:citaIdExcluir IS NULL OR c.id <> :citaIdExcluir)
          AND c.horaInicio  < :horaFin
          AND c.horaFin     > :horaInicio
        """)
    boolean existeConflictoHorario(
            @Param("medicoId")       Integer medicoId,
            @Param("fecha")          LocalDate fecha,
            @Param("horaInicio")     LocalTime horaInicio,
            @Param("horaFin")        LocalTime horaFin,
            @Param("citaIdExcluir")  Integer citaIdExcluir);
}
