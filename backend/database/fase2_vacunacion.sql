-- ============================================================
--  GestionVet — Fase 2: Módulo de Vacunación
--  Tablas: vacuna, mascota_vacuna
--  Ejecutar sobre la BD veterinaria_db ya existente
-- ============================================================

-- ── 1. Catálogo de vacunas ────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS vacuna (
    id                          SERIAL PRIMARY KEY,
    nombre                      VARCHAR(150) NOT NULL UNIQUE,
    descripcion                 TEXT,
    especie_id                  INTEGER,          -- NULL = aplica a todas las especies
    intervalo_dias_revacunacion INTEGER,          -- NULL = dosis única
    activa                      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vac_especie FOREIGN KEY (especie_id)
        REFERENCES especies(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_vacuna_especie ON vacuna(especie_id);
CREATE INDEX IF NOT EXISTS idx_vacuna_activa  ON vacuna(activa);

-- Datos base: vacunas comunes en veterinaria
INSERT INTO vacuna (nombre, descripcion, especie_id, intervalo_dias_revacunacion) VALUES
-- Perros (especie_id = 1)
('Parvovirus Canino',          'Vacuna contra el parvovirus',           1, 365),
('Moquillo Canino',            'Vacuna contra el distemper canino',     1, 365),
('Hepatitis Infecciosa',       'Vacuna contra adenovirus tipo 1 y 2',   1, 365),
('Leptospirosis',              'Vacuna contra leptospira',              1, 365),
('Rabia Canina',               'Vacuna antirrábica para perros',        1, 365),
('Bordetella',                 'Vacuna contra tos de las perreras',     1, 180),
('Polivalente Canina 5 en 1',  'DA2PP: Moquillo, Hepatitis, Parainfluenza, Parvovirus', 1, 365),
-- Gatos (especie_id = 2)
('Panleucopenia Felina',       'Vacuna contra la panleucopenia',        2, 365),
('Rinotraqueitis Felina',      'Vacuna contra herpesvirus felino',      2, 365),
('Calicivirus Felino',         'Vacuna contra calicivirus',             2, 365),
('Rabia Felina',               'Vacuna antirrábica para gatos',         2, 365),
('Trivalente Felina',          'FHV-1, FCV, FPV combinadas',           2, 365),
('Leucemia Felina',            'Vacuna contra FeLV',                    2, 365),
-- Aplica a todas las especies (especie_id = NULL)
('Rabia General',              'Vacuna antirrábica general',            NULL, 365);


-- ── 2. Registro de vacunas aplicadas a mascotas ───────────────────────────

CREATE TABLE IF NOT EXISTS mascota_vacuna (
    id                    SERIAL PRIMARY KEY,
    mascota_id            INTEGER      NOT NULL,
    vacuna_id             INTEGER      NOT NULL,
    medico_id             INTEGER      NOT NULL,
    cita_id               INTEGER,              -- opcional
    fecha_aplicacion      DATE         NOT NULL,
    fecha_proxima_dosis   DATE,
    lote_vacuna           VARCHAR(50),
    observaciones         TEXT,
    estado                VARCHAR(15)  NOT NULL DEFAULT 'vigente'
                              CHECK (estado IN ('vigente','vencida','revocada')),
    created_at            TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mv_mascota FOREIGN KEY (mascota_id) REFERENCES mascotas(id),
    CONSTRAINT fk_mv_vacuna  FOREIGN KEY (vacuna_id)  REFERENCES vacuna(id),
    CONSTRAINT fk_mv_medico  FOREIGN KEY (medico_id)  REFERENCES medicos(id),
    CONSTRAINT fk_mv_cita    FOREIGN KEY (cita_id)    REFERENCES citas(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_mv_mascota        ON mascota_vacuna(mascota_id);
CREATE INDEX IF NOT EXISTS idx_mv_vacuna         ON mascota_vacuna(vacuna_id);
CREATE INDEX IF NOT EXISTS idx_mv_proxima_dosis  ON mascota_vacuna(fecha_proxima_dosis);
CREATE INDEX IF NOT EXISTS idx_mv_estado         ON mascota_vacuna(estado);

-- ── 3. Vista: alertas de vacunación ──────────────────────────────────────
-- Útil para el dashboard: mascotas con vacunas próximas a vencer o vencidas

CREATE OR REPLACE VIEW vw_alertas_vacunacion AS
SELECT
    mv.id              AS mascota_vacuna_id,
    m.id               AS mascota_id,
    m.nombre           AS mascota,
    es.nombre          AS especie,
    cl.nombre || ' ' || cl.apellido AS propietario,
    cl.telefono        AS telefono_propietario,
    v.nombre           AS vacuna,
    mv.fecha_aplicacion,
    mv.fecha_proxima_dosis,
    mv.estado,
    (mv.fecha_proxima_dosis - CURRENT_DATE) AS dias_restantes
FROM mascota_vacuna mv
INNER JOIN mascotas m  ON mv.mascota_id = m.id
INNER JOIN vacuna   v  ON mv.vacuna_id  = v.id
INNER JOIN especies es ON m.especie_id  = es.id
INNER JOIN clientes cl ON m.cliente_id  = cl.id
WHERE mv.fecha_proxima_dosis IS NOT NULL
  AND m.estado = 'activo'
ORDER BY mv.fecha_proxima_dosis ASC;

-- ============================================================
--  FIN DEL SCRIPT — FASE 2
-- ============================================================
