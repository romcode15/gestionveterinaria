package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.MascotaVacuna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MascotaVacunaRepository extends JpaRepository<MascotaVacuna, Integer> {

    // Historial de vacunas de una mascota (más reciente primero)
    Page<MascotaVacuna> findByMascotaId(Integer mascotaId, Pageable pageable);

    // Todas las vacunas de una mascota sin paginar (para exportar)
    List<MascotaVacuna> findByMascotaId(Integer mascotaId);

    // ── Alertas: vacunas próximas a vencer ────────────────────────────────
    // Busca registros donde la próxima dosis cae entre hoy y hoy+dias
    // y la mascota sigue activa
    @Query("""
        SELECT mv FROM MascotaVacuna mv
        WHERE mv.fechaProximaDosis IS NOT NULL
          AND mv.fechaProximaDosis BETWEEN :desde AND :hasta
          AND mv.mascota.estado = 'activo'
          AND mv.estado = 'vigente'
        ORDER BY mv.fechaProximaDosis ASC
        """)
    List<MascotaVacuna> alertasProximasDosis(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    // ── Vacunas vencidas (fecha proxima dosis ya pasó y estado sigue vigente) ──
    @Query("""
        SELECT mv FROM MascotaVacuna mv
        WHERE mv.fechaProximaDosis IS NOT NULL
          AND mv.fechaProximaDosis < :hoy
          AND mv.estado = 'vigente'
          AND mv.mascota.estado = 'activo'
        ORDER BY mv.fechaProximaDosis ASC
        """)
    List<MascotaVacuna> vacunasVencidas(@Param("hoy") LocalDate hoy);

    // Verificar si una mascota ya tiene una vacuna específica vigente
    @Query("""
        SELECT COUNT(mv) > 0 FROM MascotaVacuna mv
        WHERE mv.mascota.id = :mascotaId
          AND mv.vacuna.id  = :vacunaId
          AND mv.estado     = 'vigente'
          AND (mv.fechaProximaDosis IS NULL OR mv.fechaProximaDosis >= :hoy)
        """)
    boolean tieneVacunaVigente(
            @Param("mascotaId") Integer mascotaId,
            @Param("vacunaId")  Integer vacunaId,
            @Param("hoy")       LocalDate hoy);
}
