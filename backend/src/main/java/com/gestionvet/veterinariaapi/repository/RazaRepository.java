package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Raza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RazaRepository extends JpaRepository<Raza, Integer> {
    List<Raza> findByEspecieId(Integer especieId);
}
