package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Integer> {

    Page<Medico> findByDisponible(Boolean disponible, Pageable pageable);
    long countByDisponibleTrue();
    Optional<Medico> findByEmail(String email);
    Optional<Medico> findByNumeroLicencia(String numeroLicencia);
    Page<Medico> findByEstado(String estado, Pageable pageable);
    Page<Medico> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido, Pageable pageable);
}
