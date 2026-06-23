package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.TipoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoCitaRepository extends JpaRepository<TipoCita, Integer> {}
