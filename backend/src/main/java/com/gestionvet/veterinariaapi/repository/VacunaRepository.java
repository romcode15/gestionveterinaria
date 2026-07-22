package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Vacuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacunaRepository extends JpaRepository<Vacuna, Integer> {

    Optional<Vacuna> findByNombreIgnoreCase(String nombre);

    List<Vacuna> findByActiva(Boolean activa);

    // Vacunas aplicables a una especie o sin restricción de especie
    List<Vacuna> findByEspecieIdOrEspecieIsNull(Integer especieId);
}
