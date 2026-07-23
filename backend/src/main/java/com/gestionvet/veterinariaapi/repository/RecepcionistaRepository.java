package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Recepcionista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecepcionistaRepository extends JpaRepository<Recepcionista, Integer> {

    Optional<Recepcionista> findByEmail(String email);

    Page<Recepcionista> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre, String apellido, Pageable pageable);

    Page<Recepcionista> findByEstado(String estado, Pageable pageable);

    boolean existsByEmail(String email);
}
