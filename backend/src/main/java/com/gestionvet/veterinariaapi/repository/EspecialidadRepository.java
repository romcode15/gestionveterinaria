package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Integer> {}
