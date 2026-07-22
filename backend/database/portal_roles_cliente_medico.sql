-- ============================================================
--  GestionVet — Portal Cliente y Médico
--  Agrega columna medico_id a usuarios y actualiza el UsuarioDTO
--  Ejecutar sobre la BD veterinaria_db ya existente
-- ============================================================

-- ── 1. Agregar FK medico_id a la tabla usuarios ───────────────────────────

ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS medico_id INTEGER
        REFERENCES medicos(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_usuario_medico_id  ON usuarios(medico_id);
CREATE INDEX IF NOT EXISTS idx_usuario_cliente_id ON usuarios(cliente_id);

-- ── 2. Cómo vincular un usuario existente a un médico ─────────────────────
-- Ejemplo: vincular usuario 'dr_gonzalez' al médico con id=1
-- UPDATE usuarios SET medico_id = 1 WHERE username = 'dr_gonzalez';

-- ── 3. Cómo crear un usuario para un médico ya existente ──────────────────
-- INSERT INTO usuarios (username, password, email, nombre, apellido, activo, medico_id)
-- VALUES (
--     'dr_maria_gonzalez',
--     '$2a$10$...',   -- password hasheada con BCrypt
--     'maria.g@veterinaria.com',
--     'María', 'González',
--     TRUE,
--     1  -- id del médico en la tabla medicos
-- );
--
-- -- Asignar rol veterinario al usuario recién creado
-- INSERT INTO usuarios_roles (usuario_id, rol_id)
-- VALUES (
--     (SELECT id FROM usuarios WHERE username = 'dr_maria_gonzalez'),
--     (SELECT id FROM roles    WHERE nombre   = 'veterinario')
-- );

-- ── 4. Cómo crear un usuario para un cliente ya existente ─────────────────
-- INSERT INTO usuarios (username, password, email, nombre, apellido, activo, cliente_id)
-- VALUES (
--     'carlos_rodriguez',
--     '$2a$10$...',   -- password hasheada con BCrypt
--     'carlos.r@email.com',
--     'Carlos', 'Rodríguez',
--     TRUE,
--     2  -- id del cliente en la tabla clientes
-- );
--
-- -- Asignar rol cliente al usuario
-- INSERT INTO usuarios_roles (usuario_id, rol_id)
-- VALUES (
--     (SELECT id FROM usuarios WHERE username = 'carlos_rodriguez'),
--     (SELECT id FROM roles    WHERE nombre   = 'cliente')
-- );

-- ── 5. Vista: usuarios con su rol y vinculación ───────────────────────────

CREATE OR REPLACE VIEW vw_usuarios_roles AS
SELECT
    u.id            AS usuario_id,
    u.username,
    u.email,
    u.nombre || ' ' || u.apellido AS nombre_completo,
    u.activo,
    r.nombre        AS rol,
    CASE
        WHEN u.cliente_id IS NOT NULL
            THEN 'Cliente #' || u.cliente_id
        WHEN u.medico_id IS NOT NULL
            THEN 'Médico #'  || u.medico_id
        ELSE 'Sin vinculación'
    END             AS vinculacion
FROM usuarios u
LEFT JOIN usuarios_roles ur ON u.id   = ur.usuario_id
LEFT JOIN roles          r  ON ur.rol_id = r.id
ORDER BY r.nombre, u.username;

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
