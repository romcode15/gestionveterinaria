package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    List<Cita> findByFecha(LocalDate fecha);
    List<Cita> findByClienteId(Integer clienteId);
    List<Cita> findByMedicoId(Integer medicoId);
    List<Cita> findByEstado(String estado);
    List<Cita> findByMascotaId(Integer mascotaId);
    List<Cita> findByFechaBetween(LocalDate inicio, LocalDate fin);
}
