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

    /**
     * Verifica si el médico ya tiene una cita activa que solape el horario propuesto.
     * Se excluyen citas canceladas y no_asistio para permitir reutilizar el slot.
     * Si citaIdExcluir es null (nueva cita) no excluye ninguna.
     */
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
