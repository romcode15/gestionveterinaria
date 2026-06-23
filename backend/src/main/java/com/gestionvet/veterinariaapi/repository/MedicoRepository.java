package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Integer> {

    List<Medico> findByDisponible(Boolean disponible);
    Optional<Medico> findByEmail(String email);
    Optional<Medico> findByNumeroLicencia(String numeroLicencia);
    List<Medico> findByEstado(String estado);
    List<Medico> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
}
