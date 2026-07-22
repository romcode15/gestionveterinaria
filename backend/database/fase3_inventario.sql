-- ============================================================
--  GestionVet — Fase 3: Módulo de Inventario
--  Tablas: categoria_producto, proveedor, producto,
--          lote_producto, movimiento_inventario
--  + columnas en tratamiento_detalle
--  Ejecutar sobre la BD veterinaria_db ya existente
-- ============================================================

-- ── 1. Categorías de productos ────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS categoria_producto (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO categoria_producto (nombre, descripcion) VALUES
('Medicamentos',        'Fármacos y antibióticos de uso veterinario'),
('Vacunas',             'Biológicos para inmunización animal'),
('Antiparasitarios',    'Productos para control de parásitos internos y externos'),
('Suplementos',         'Vitaminas, minerales y suplementos nutricionales'),
('Material Quirúrgico', 'Instrumental y material de uso quirúrgico'),
('Consumibles',         'Jeringas, guantes, algodón y material de uso único'),
('Alimentos',           'Dietas terapéuticas y alimentos especiales'),
('Desinfectantes',      'Productos de limpieza y desinfección'),
('Diagnóstico',         'Kits y reactivos para diagnóstico');

-- ── 2. Proveedores ────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS proveedor (
    id         SERIAL PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    ruc        VARCHAR(20)  UNIQUE,
    contacto   VARCHAR(100),
    telefono   VARCHAR(20),
    email      VARCHAR(150),
    direccion  VARCHAR(255),
    estado     VARCHAR(10)  NOT NULL DEFAULT 'activo'
                   CHECK (estado IN ('activo','inactivo')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_proveedor_estado ON proveedor(estado);

-- ── 3. Productos ──────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS producto (
    id               SERIAL PRIMARY KEY,
    codigo           VARCHAR(50)   UNIQUE,
    nombre           VARCHAR(200)  NOT NULL,
    descripcion      TEXT,
    categoria_id     INTEGER       NOT NULL,
    proveedor_id     INTEGER,
    unidad_medida    VARCHAR(30)   NOT NULL,
    precio_unitario  DECIMAL(10,2),
    stock_actual     INTEGER       NOT NULL DEFAULT 0,
    stock_minimo     INTEGER       NOT NULL DEFAULT 5,
    requiere_receta  BOOLEAN       NOT NULL DEFAULT FALSE,
    estado           VARCHAR(10)   NOT NULL DEFAULT 'activo'
                         CHECK (estado IN ('activo','inactivo')),
    created_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prod_categoria FOREIGN KEY (categoria_id)
        REFERENCES categoria_producto(id),
    CONSTRAINT fk_prod_proveedor FOREIGN KEY (proveedor_id)
        REFERENCES proveedor(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_producto_categoria   ON producto(categoria_id);
CREATE INDEX IF NOT EXISTS idx_producto_estado      ON producto(estado);
CREATE INDEX IF NOT EXISTS idx_producto_stock_bajo  ON producto(stock_actual, stock_minimo);

-- ── 4. Lotes de productos ─────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS lote_producto (
    id                SERIAL PRIMARY KEY,
    producto_id       INTEGER       NOT NULL,
    proveedor_id      INTEGER,
    numero_lote       VARCHAR(50)   NOT NULL,
    fecha_fabricacion DATE,
    fecha_vencimiento DATE,
    cantidad_inicial  INTEGER       NOT NULL CHECK (cantidad_inicial > 0),
    cantidad_actual   INTEGER       NOT NULL,
    precio_compra     DECIMAL(10,2),
    estado            VARCHAR(10)   NOT NULL DEFAULT 'activo'
                          CHECK (estado IN ('activo','agotado','vencido')),
    created_at        TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lote_producto  FOREIGN KEY (producto_id)
        REFERENCES producto(id),
    CONSTRAINT fk_lote_proveedor FOREIGN KEY (proveedor_id)
        REFERENCES proveedor(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_lote_producto    ON lote_producto(producto_id);
CREATE INDEX IF NOT EXISTS idx_lote_vencimiento ON lote_producto(fecha_vencimiento);
CREATE INDEX IF NOT EXISTS idx_lote_estado      ON lote_producto(estado);

-- ── 5. Movimientos de inventario ──────────────────────────────────────────

CREATE TABLE IF NOT EXISTS movimiento_inventario (
    id                      SERIAL PRIMARY KEY,
    producto_id             INTEGER     NOT NULL,
    lote_id                 INTEGER,
    tipo_movimiento         VARCHAR(25) NOT NULL
                                CHECK (tipo_movimiento IN (
                                    'ENTRADA','SALIDA_TRATAMIENTO',
                                    'SALIDA_MANUAL','AJUSTE','DEVOLUCION')),
    cantidad                INTEGER     NOT NULL CHECK (cantidad > 0),
    stock_anterior          INTEGER     NOT NULL,
    stock_posterior         INTEGER     NOT NULL,
    tratamiento_detalle_id  INTEGER,
    motivo                  VARCHAR(255),
    usuario_id              INTEGER,
    created_at              TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mov_producto    FOREIGN KEY (producto_id)  REFERENCES producto(id),
    CONSTRAINT fk_mov_lote        FOREIGN KEY (lote_id)      REFERENCES lote_producto(id) ON DELETE SET NULL,
    CONSTRAINT fk_mov_trat_det    FOREIGN KEY (tratamiento_detalle_id)
        REFERENCES tratamiento_detalle(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_mov_producto ON movimiento_inventario(producto_id);
CREATE INDEX IF NOT EXISTS idx_mov_tipo     ON movimiento_inventario(tipo_movimiento);
CREATE INDEX IF NOT EXISTS idx_mov_fecha    ON movimiento_inventario(created_at);

-- ── 6. Conectar tratamiento_detalle con inventario ────────────────────────

ALTER TABLE tratamiento_detalle
    ADD COLUMN IF NOT EXISTS producto_id          INTEGER
        REFERENCES producto(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS cantidad_dispensada  INTEGER
        CHECK (cantidad_dispensada > 0);

CREATE INDEX IF NOT EXISTS idx_det_producto ON tratamiento_detalle(producto_id);

-- ── 7. Vista: alertas de inventario ──────────────────────────────────────

CREATE OR REPLACE VIEW vw_alertas_inventario AS
SELECT
    'STOCK_BAJO'        AS tipo_alerta,
    p.id                AS referencia_id,
    p.nombre            AS descripcion,
    cp.nombre           AS categoria,
    p.stock_actual      AS valor_actual,
    p.stock_minimo      AS valor_referencia,
    NULL::DATE          AS fecha_relevante
FROM producto p
INNER JOIN categoria_producto cp ON p.categoria_id = cp.id
WHERE p.stock_actual <= p.stock_minimo
  AND p.estado = 'activo'
UNION ALL
SELECT
    'LOTE_VENCIDO'      AS tipo_alerta,
    l.id                AS referencia_id,
    p.nombre || ' (Lote: ' || l.numero_lote || ')' AS descripcion,
    cp.nombre           AS categoria,
    l.cantidad_actual   AS valor_actual,
    0                   AS valor_referencia,
    l.fecha_vencimiento AS fecha_relevante
FROM lote_producto l
INNER JOIN producto          p  ON l.producto_id  = p.id
INNER JOIN categoria_producto cp ON p.categoria_id = cp.id
WHERE l.fecha_vencimiento < CURRENT_DATE
  AND l.estado = 'activo'
  AND l.cantidad_actual > 0
ORDER BY tipo_alerta, fecha_relevante NULLS LAST;

-- ============================================================
--  FIN DEL SCRIPT — FASE 3
-- ============================================================
