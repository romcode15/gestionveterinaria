package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.AuditoriaGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaGeneral, Long> {

    Page<AuditoriaGeneral> findByUsername(String username, Pageable pageable);

    Page<AuditoriaGeneral> findByEntidad(String entidad, Pageable pageable);

    Page<AuditoriaGeneral> findByAccion(String accion, Pageable pageable);

    Page<AuditoriaGeneral> findByCreatedAtBetween(
            LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    Page<AuditoriaGeneral> findByExitoso(Boolean exitoso, Pageable pageable);
}
