package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecieRepository extends JpaRepository<Especie, Integer> {}
