package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
    List<Cliente> findByEstado(String estado);
}
