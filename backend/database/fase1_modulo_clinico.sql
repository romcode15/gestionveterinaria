-- ============================================================
--  GestionVet — Fase 1: Módulo Clínico
--  Tablas: via_administracion, diagnostico, tratamiento, tratamiento_detalle
--  Ejecutar sobre la BD veterinaria_db ya existente
-- ============================================================

-- ── 1. Vías de administración (catálogo) ──────────────────────────────────

CREATE TABLE IF NOT EXISTS via_administracion (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO via_administracion (nombre, descripcion) VALUES
('Oral',           'Administración por la boca (comprimidos, jarabes, cápsulas)'),
('Inyectable IV',  'Vía intravenosa — efecto inmediato'),
('Inyectable IM',  'Vía intramuscular'),
('Inyectable SC',  'Vía subcutánea'),
('Tópica',         'Aplicación sobre la piel o mucosas'),
('Oftálmica',      'Gotas o ungüento ocular'),
('Ótica',          'Gotas en el oído'),
('Inhalatoria',    'Administración por vía respiratoria'),
('Rectal',         'Supositorios o enemas');

-- ── 2. Diagnóstico ────────────────────────────────────────────────────────
-- Relación 1:1 con citas — una cita tiene como máximo un diagnóstico

CREATE TABLE IF NOT EXISTS diagnostico (
    id           SERIAL PRIMARY KEY,
    cita_id      INTEGER      NOT NULL UNIQUE,
    mascota_id   INTEGER      NOT NULL,
    medico_id    INTEGER      NOT NULL,
    sintomas     TEXT         NOT NULL,
    diagnostico  TEXT         NOT NULL,
    pronostico   VARCHAR(50)
                     CHECK (pronostico IN ('favorable','reservado','grave','muerte')),
    peso_consulta  DECIMAL(5,2) CHECK (peso_consulta > 0),
    temperatura    DECIMAL(4,1) CHECK (temperatura > 0),
    observaciones  TEXT,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_diag_cita    FOREIGN KEY (cita_id)    REFERENCES citas(id),
    CONSTRAINT fk_diag_mascota FOREIGN KEY (mascota_id) REFERENCES mascotas(id),
    CONSTRAINT fk_diag_medico  FOREIGN KEY (medico_id)  REFERENCES medicos(id)
);

CREATE INDEX IF NOT EXISTS idx_diag_mascota ON diagnostico(mascota_id);
CREATE INDEX IF NOT EXISTS idx_diag_medico  ON diagnostico(medico_id);
CREATE INDEX IF NOT EXISTS idx_diag_cita    ON diagnostico(cita_id);

-- ── 3. Tratamiento ────────────────────────────────────────────────────────
-- Relación 1:1 con diagnóstico

CREATE TABLE IF NOT EXISTS tratamiento (
    id                    SERIAL PRIMARY KEY,
    diagnostico_id        INTEGER NOT NULL UNIQUE,
    instrucciones_generales TEXT,
    fecha_inicio          DATE    NOT NULL,
    fecha_fin             DATE,
    proxima_visita        DATE,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trat_diagnostico FOREIGN KEY (diagnostico_id)
        REFERENCES diagnostico(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_trat_diagnostico ON tratamiento(diagnostico_id);

-- ── 4. Detalle del tratamiento ────────────────────────────────────────────
-- Cada línea = un medicamento con su dosis, frecuencia y vía

CREATE TABLE IF NOT EXISTS tratamiento_detalle (
    id                    SERIAL PRIMARY KEY,
    tratamiento_id        INTEGER       NOT NULL,
    medicamento           VARCHAR(200)  NOT NULL,
    dosis                 VARCHAR(100)  NOT NULL,
    frecuencia            VARCHAR(100)  NOT NULL,
    duracion_dias         INTEGER       NOT NULL CHECK (duracion_dias > 0),
    via_administracion_id INTEGER       NOT NULL,
    instrucciones         TEXT,
    created_at            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_det_tratamiento FOREIGN KEY (tratamiento_id)
        REFERENCES tratamiento(id) ON DELETE CASCADE,
    CONSTRAINT fk_det_via FOREIGN KEY (via_administracion_id)
        REFERENCES via_administracion(id)
);

CREATE INDEX IF NOT EXISTS idx_det_tratamiento ON tratamiento_detalle(tratamiento_id);

-- ── 5. Vista: historial clínico ───────────────────────────────────────────
-- Consulta que consolida cita + diagnóstico + tratamiento por mascota

CREATE OR REPLACE VIEW vw_historial_clinico AS
SELECT
    m.id           AS mascota_id,
    m.nombre       AS mascota,
    es.nombre      AS especie,
    r.nombre       AS raza,
    cl.nombre || ' ' || cl.apellido AS propietario,
    c.fecha        AS fecha_consulta,
    c.hora_inicio,
    tc.nombre      AS tipo_cita,
    med.nombre || ' ' || med.apellido AS medico,
    c.motivo,
    d.id           AS diagnostico_id,
    d.sintomas,
    d.diagnostico,
    d.pronostico,
    d.peso_consulta,
    d.temperatura,
    d.observaciones AS observaciones_diagnostico,
    t.id           AS tratamiento_id,
    t.instrucciones_generales,
    t.fecha_inicio AS inicio_tratamiento,
    t.fecha_fin    AS fin_tratamiento,
    t.proxima_visita
FROM diagnostico d
INNER JOIN citas       c   ON d.cita_id    = c.id
INNER JOIN mascotas    m   ON d.mascota_id = m.id
INNER JOIN especies    es  ON m.especie_id = es.id
INNER JOIN razas       r   ON m.raza_id    = r.id
INNER JOIN clientes    cl  ON m.cliente_id = cl.id
INNER JOIN medicos     med ON d.medico_id  = med.id
INNER JOIN tipos_cita  tc  ON c.tipo_cita_id = tc.id
LEFT  JOIN tratamiento t   ON t.diagnostico_id = d.id
ORDER BY m.id, c.fecha DESC, c.hora_inicio DESC;

-- ============================================================
--  FIN DEL SCRIPT — FASE 1
-- ============================================================
