package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Page<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido, Pageable pageable);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
    Page<Cliente> findByEstado(String estado, Pageable pageable);

    // Incrementar o decrementar numero_mascotas directamente en BD
    // nativeQuery=true usa SQL puro en vez de JPQL — no depende del mapeo de la entidad
    @Modifying
    @Query(value = "UPDATE clientes SET numero_mascotas = numero_mascotas + :delta WHERE id = :id",
           nativeQuery = true)
    void actualizarNumeroMascotas(@Param("id") Integer id, @Param("delta") int delta);
}
