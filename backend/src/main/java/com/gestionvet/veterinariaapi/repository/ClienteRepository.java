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
    long countByEstado(String estado);

    /**
     * Búsqueda combinada con filtros opcionales.
     * Si un parámetro es null, esa condición se ignora (AND :param IS NULL OR ...).
     * Permite combinar búsqueda por texto + estado simultáneamente.
     */
    @Query(value = """
        SELECT * FROM clientes c
        WHERE (CAST(:busqueda AS TEXT) IS NULL
               OR LOWER(c.nombre)           LIKE LOWER('%' || CAST(:busqueda AS TEXT) || '%')
               OR LOWER(c.apellido)         LIKE LOWER('%' || CAST(:busqueda AS TEXT) || '%')
               OR LOWER(c.email)            LIKE LOWER('%' || CAST(:busqueda AS TEXT) || '%')
               OR LOWER(c.numero_documento) LIKE LOWER('%' || CAST(:busqueda AS TEXT) || '%'))
          AND (CAST(:estado AS TEXT) IS NULL OR c.estado = CAST(:estado AS TEXT))
        ORDER BY c.apellido ASC
        """,
        countQuery = """
        SELECT COUNT(*) FROM clientes c
        WHERE (CAST(:busqueda AS TEXT) IS NULL
               OR LOWER(c.nombre)           LIKE LOWER('%' || CAST(:busqueda AS TEXT) || '%')
               OR LOWER(c.apellido)         LIKE LOWER('%' || CAST(:busqueda AS TEXT) || '%')
               OR LOWER(c.email)            LIKE LOWER('%' || CAST(:busqueda AS TEXT) || '%')
               OR LOWER(c.numero_documento) LIKE LOWER('%' || CAST(:busqueda AS TEXT) || '%'))
          AND (CAST(:estado AS TEXT) IS NULL OR c.estado = CAST(:estado AS TEXT))
        """,
        nativeQuery = true)
    Page<Cliente> buscarCombinado(
            @Param("busqueda") String busqueda,
            @Param("estado")   String estado,
            Pageable pageable);

    // Incrementar o decrementar numero_mascotas directamente en BD
    @Modifying
    @Query(value = "UPDATE clientes SET numero_mascotas = numero_mascotas + :delta WHERE id = :id",
           nativeQuery = true)
    void actualizarNumeroMascotas(@Param("id") Integer id, @Param("delta") int delta);

    // Clientes que tienen citas con un médico específico (sin duplicados)
    @Query("""
        SELECT DISTINCT c FROM Cliente c
        WHERE c.id IN (
            SELECT ci.cliente.id FROM Cita ci WHERE ci.medico.id = :medicoId
        )
        ORDER BY c.apellido ASC
        """)
    Page<Cliente> findByMedicoId(@Param("medicoId") Integer medicoId, Pageable pageable);

    @Query("""
        SELECT DISTINCT c FROM Cliente c
        WHERE c.id IN (
            SELECT ci.cliente.id FROM Cita ci WHERE ci.medico.id = :medicoId
        )
        AND (LOWER(c.nombre) LIKE LOWER(CONCAT('%',:q,'%'))
          OR LOWER(c.apellido) LIKE LOWER(CONCAT('%',:q,'%')))
        ORDER BY c.apellido ASC
        """)
    Page<Cliente> findByMedicoIdAndNombre(@Param("medicoId") Integer medicoId,
                                          @Param("q") String q,
                                          Pageable pageable);
}
