package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Integer> {

    Optional<Tratamiento> findByDiagnosticoId(Integer diagnosticoId);

    boolean existsByDiagnosticoId(Integer diagnosticoId);
}
