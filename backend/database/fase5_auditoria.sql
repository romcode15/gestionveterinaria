-- ============================================================
--  GestionVet — Fase 5: Auditoría
--  Tabla: auditoria_general
--  Ejecutar sobre la BD veterinaria_db ya existente
-- ============================================================

CREATE TABLE IF NOT EXISTS auditoria_general (
    id              BIGSERIAL    PRIMARY KEY,
    usuario_id      INTEGER,
    username        VARCHAR(100),
    accion          VARCHAR(50)  NOT NULL,
    -- CREATE | UPDATE | DELETE | LOGIN | SALIDA_INVENTARIO
    entidad         VARCHAR(100) NOT NULL,
    -- Nombre de la clase/módulo afectado
    entidad_id      VARCHAR(50),
    -- ID del registro afectado (null en acciones globales)
    descripcion     TEXT,
    ip_origen       VARCHAR(50),
    endpoint        VARCHAR(255),
    exitoso         BOOLEAN      NOT NULL DEFAULT TRUE,
    error_mensaje   TEXT,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para consultas frecuentes del panel de auditoría
CREATE INDEX IF NOT EXISTS idx_aud_username   ON auditoria_general(username);
CREATE INDEX IF NOT EXISTS idx_aud_accion     ON auditoria_general(accion);
CREATE INDEX IF NOT EXISTS idx_aud_entidad    ON auditoria_general(entidad);
CREATE INDEX IF NOT EXISTS idx_aud_created_at ON auditoria_general(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_aud_exitoso    ON auditoria_general(exitoso);
CREATE INDEX IF NOT EXISTS idx_aud_usuario_id ON auditoria_general(usuario_id);

-- Nota: esta tabla crece indefinidamente.
-- En producción se recomienda una política de retención,
-- por ejemplo archivar o eliminar registros con más de 1 año:
--
-- DELETE FROM auditoria_general
-- WHERE created_at < NOW() - INTERVAL '1 year';
--
-- O usar particionamiento por fecha (PostgreSQL nativo):
-- PARTITION BY RANGE (created_at)

-- ── Vista: resumen de actividad de los últimos 7 días ─────────────────────

CREATE OR REPLACE VIEW vw_actividad_reciente AS
SELECT
    DATE(created_at)         AS dia,
    username,
    accion,
    COUNT(*)                 AS total,
    SUM(CASE WHEN exitoso = FALSE THEN 1 ELSE 0 END) AS errores
FROM auditoria_general
WHERE created_at >= CURRENT_DATE - INTERVAL '7 days'
GROUP BY DATE(created_at), username, accion
ORDER BY dia DESC, total DESC;

-- ── Vista: últimas 100 acciones críticas ──────────────────────────────────

CREATE OR REPLACE VIEW vw_acciones_criticas AS
SELECT
    id,
    username,
    accion,
    entidad,
    entidad_id,
    descripcion,
    ip_origen,
    exitoso,
    error_mensaje,
    created_at
FROM auditoria_general
WHERE accion IN ('DELETE', 'LOGIN')
   OR exitoso = FALSE
ORDER BY created_at DESC
LIMIT 100;

-- ============================================================
--  FIN DEL SCRIPT — FASE 5
-- ============================================================
