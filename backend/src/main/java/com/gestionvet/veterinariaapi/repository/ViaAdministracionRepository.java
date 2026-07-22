package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.ViaAdministracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViaAdministracionRepository extends JpaRepository<ViaAdministracion, Integer> {

    Optional<ViaAdministracion> findByNombreIgnoreCase(String nombre);
}
