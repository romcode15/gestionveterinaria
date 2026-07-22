-- ============================================================
--  GestionVet — Fase 4: Dashboard y Reportes
--  No crea tablas nuevas. Agrega vistas de consulta agregada
--  para uso del dashboard y reportes operativos.
--  Ejecutar sobre la BD veterinaria_db ya existente
--  (requiere haber ejecutado fase1, fase2 y fase3 primero)
-- ============================================================

-- ── 1. Vista: resumen del día ─────────────────────────────────────────────

CREATE OR REPLACE VIEW vw_resumen_dia AS
SELECT
    CURRENT_DATE                                          AS fecha,
    COUNT(*)                                              AS total_citas,
    SUM(CASE WHEN estado = 'pendiente'   THEN 1 ELSE 0 END) AS pendientes,
    SUM(CASE WHEN estado = 'confirmada'  THEN 1 ELSE 0 END) AS confirmadas,
    SUM(CASE WHEN estado = 'en_curso'    THEN 1 ELSE 0 END) AS en_curso,
    SUM(CASE WHEN estado = 'completada'  THEN 1 ELSE 0 END) AS completadas,
    SUM(CASE WHEN estado = 'cancelada'   THEN 1 ELSE 0 END) AS canceladas,
    SUM(CASE WHEN estado = 'no_asistio'  THEN 1 ELSE 0 END) AS no_asistieron,
    COUNT(DISTINCT CASE WHEN estado = 'completada' THEN mascota_id END) AS mascotas_atendidas_hoy
FROM citas
WHERE fecha = CURRENT_DATE;

-- ── 2. Vista: médicos más activos (últimos 30 días) ───────────────────────

CREATE OR REPLACE VIEW vw_medicos_mas_activos AS
SELECT
    m.id                            AS medico_id,
    m.nombre || ' ' || m.apellido  AS medico,
    COUNT(c.id)                     AS total_citas,
    SUM(CASE WHEN c.estado = 'completada' THEN 1 ELSE 0 END) AS completadas,
    ROUND(
        SUM(CASE WHEN c.estado = 'completada' THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(c.id), 0),
        1
    )                               AS tasa_completacion_pct
FROM medicos m
INNER JOIN citas c ON m.id = c.medico_id
WHERE c.fecha >= CURRENT_DATE - INTERVAL '30 days'
  AND c.estado NOT IN ('cancelada','no_asistio')
GROUP BY m.id, m.nombre, m.apellido
ORDER BY total_citas DESC;

-- ── 3. Vista: mascotas más atendidas (últimos 30 días) ────────────────────

CREATE OR REPLACE VIEW vw_mascotas_mas_atendidas AS
SELECT
    ma.id                                AS mascota_id,
    ma.nombre                            AS mascota,
    es.nombre                            AS especie,
    cl.nombre || ' ' || cl.apellido     AS propietario,
    cl.telefono,
    COUNT(d.id)                          AS total_consultas
FROM diagnostico d
INNER JOIN mascotas  ma ON d.mascota_id = ma.id
INNER JOIN especies  es ON ma.especie_id = es.id
INNER JOIN clientes  cl ON ma.cliente_id = cl.id
WHERE d.created_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY ma.id, ma.nombre, es.nombre, cl.nombre, cl.apellido, cl.telefono
ORDER BY total_consultas DESC;

-- ── 4. Vista: citas por tipo (últimos 30 días) ────────────────────────────

CREATE OR REPLACE VIEW vw_citas_por_tipo AS
SELECT
    tc.nombre      AS tipo_cita,
    COUNT(c.id)    AS total,
    ROUND(COUNT(c.id) * 100.0 / NULLIF(SUM(COUNT(c.id)) OVER (), 0), 1) AS porcentaje
FROM citas c
INNER JOIN tipos_cita tc ON c.tipo_cita_id = tc.id
WHERE c.fecha >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY tc.nombre
ORDER BY total DESC;

-- ── 5. Vista: citas por día de la semana (últimos 30 días) ────────────────

CREATE OR REPLACE VIEW vw_citas_por_dia_semana AS
SELECT
    EXTRACT(DOW FROM fecha)          AS num_dia,
    TO_CHAR(fecha, 'Day')            AS dia_semana,
    COUNT(*)                         AS total_citas
FROM citas
WHERE fecha >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY EXTRACT(DOW FROM fecha), TO_CHAR(fecha, 'Day')
ORDER BY num_dia;

-- ── 6. Vista: productos más usados en tratamientos ────────────────────────

CREATE OR REPLACE VIEW vw_productos_mas_usados AS
SELECT
    p.id                  AS producto_id,
    p.nombre              AS producto,
    cp.nombre             AS categoria,
    p.unidad_medida,
    SUM(mi.cantidad)      AS total_dispensado,
    COUNT(mi.id)          AS num_movimientos
FROM movimiento_inventario mi
INNER JOIN producto          p  ON mi.producto_id   = p.id
INNER JOIN categoria_producto cp ON p.categoria_id  = cp.id
WHERE mi.tipo_movimiento = 'SALIDA_TRATAMIENTO'
  AND mi.created_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY p.id, p.nombre, cp.nombre, p.unidad_medida
ORDER BY total_dispensado DESC;

-- ── 7. Vista: resumen general de alertas ─────────────────────────────────

CREATE OR REPLACE VIEW vw_resumen_alertas AS
SELECT
    -- Vacunas próximas a vencer (30 días)
    (SELECT COUNT(*) FROM mascota_vacuna mv
     INNER JOIN mascotas m ON mv.mascota_id = m.id
     WHERE mv.fecha_proxima_dosis BETWEEN CURRENT_DATE AND CURRENT_DATE + 30
       AND mv.estado = 'vigente'
       AND m.estado  = 'activo')                  AS vacunas_proximas_a_vencer,

    -- Vacunas ya vencidas
    (SELECT COUNT(*) FROM mascota_vacuna mv
     INNER JOIN mascotas m ON mv.mascota_id = m.id
     WHERE mv.fecha_proxima_dosis < CURRENT_DATE
       AND mv.estado = 'vigente'
       AND m.estado  = 'activo')                  AS vacunas_vencidas,

    -- Productos con stock bajo
    (SELECT COUNT(*) FROM producto
     WHERE stock_actual <= stock_minimo
       AND estado = 'activo')                     AS productos_stock_bajo,

    -- Lotes próximos a vencer (30 días)
    (SELECT COUNT(*) FROM lote_producto
     WHERE fecha_vencimiento BETWEEN CURRENT_DATE AND CURRENT_DATE + 30
       AND estado = 'activo'
       AND cantidad_actual > 0)                   AS lotes_proximos_a_vencer,

    -- Lotes ya vencidos con stock
    (SELECT COUNT(*) FROM lote_producto
     WHERE fecha_vencimiento < CURRENT_DATE
       AND estado = 'activo'
       AND cantidad_actual > 0)                   AS lotes_vencidos;

-- ============================================================
--  FIN DEL SCRIPT — FASE 4
-- ============================================================
