package com.gestionvet.veterinariaapi.repository;

import com.gestionvet.veterinariaapi.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // ── Buscar por username ──────────────────────────────────────────────
    Optional<Usuario> findByUsername(String username);

    // ── Buscar por email (para login con email) ──────────────────────────
    Optional<Usuario> findByEmail(String email);

    // ── Buscar por username con roles y permisos precargados (para login) ──
    @EntityGraph(attributePaths = {"roles", "roles.permisos"})
    @Query("SELECT u FROM Usuario u WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithRolesAndPermisos(@Param("username") String username);

    // ── Buscar por email con roles y permisos precargados (login con email) ──
    @EntityGraph(attributePaths = {"roles", "roles.permisos"})
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Optional<Usuario> findByEmailWithRolesAndPermisos(@Param("email") String email);

    // ── Verificar existencia por username (para validaciones) ────────────
    boolean existsByUsername(String username);

    // ── Verificar existencia por email (para validaciones) ───────────────
    boolean existsByEmail(String email);

    // ── Búsqueda paginada con filtros opcionales ─────────────────────────
    @Query("""
        SELECT DISTINCT u FROM Usuario u
        LEFT JOIN u.roles r
        WHERE (:busqueda IS NULL OR :busqueda = ''
               OR LOWER(u.nombre)    LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(u.apellido)  LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(u.username)  LIKE LOWER(CONCAT('%', :busqueda, '%'))
               OR LOWER(u.email)     LIKE LOWER(CONCAT('%', :busqueda, '%')))
          AND (:rol IS NULL OR :rol = '' OR LOWER(r.nombre) = LOWER(:rol))
        """)
    Page<Usuario> buscarPaginado(
            @Param("busqueda") String busqueda,
            @Param("rol")      String rol,
            Pageable pageable);
}
