-- ============================================================
-- FASE 6: Auto-creación de usuarios para clientes, médicos
--         y recepcionistas
-- ============================================================

-- 1. Tabla recepcionistas (nueva)
CREATE TABLE IF NOT EXISTS recepcionistas (
    id           SERIAL PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    apellido     VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    telefono     VARCHAR(20),
    estado       VARCHAR(10)  NOT NULL DEFAULT 'activo',
    created_at   TIMESTAMP DEFAULT NOW()
);

-- 2. FK usuario_id en clientes
ALTER TABLE clientes
    ADD COLUMN IF NOT EXISTS usuario_id INT UNIQUE
    REFERENCES usuarios(id) ON DELETE SET NULL;

-- 3. FK usuario_id en medicos
ALTER TABLE medicos
    ADD COLUMN IF NOT EXISTS usuario_id INT UNIQUE
    REFERENCES usuarios(id) ON DELETE SET NULL;

-- 4. FK recepcionista_id en usuarios
ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS recepcionista_id INT UNIQUE
    REFERENCES recepcionistas(id) ON DELETE SET NULL;
