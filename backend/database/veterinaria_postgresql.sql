-- ============================================================
--  GestionVet — Migración MySQL → PostgreSQL
--  Versión: PostgreSQL 15+
--  Abrir con pgAdmin 4 → Query Tool
-- ============================================================

-- Eliminar y recrear esquema limpio
DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

-- ============================================================
--  1. CATÁLOGOS BASE (sin dependencias)
-- ============================================================

CREATE TABLE permisos (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    modulo      VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE
                    CHECK (nombre IN ('admin','veterinario','recepcionista','cliente')),
    descripcion VARCHAR(255),
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE especialidades (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion TEXT,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE especies (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tipos_cita (
    id               SERIAL PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    duracion_minutos INTEGER      NOT NULL DEFAULT 30
                         CHECK (duracion_minutos > 0),
    color            VARCHAR(7)   DEFAULT '#007bff',
    descripcion      TEXT,
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE razas (
    id         SERIAL PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    especie_id INTEGER      NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_raza_especie FOREIGN KEY (especie_id)
        REFERENCES especies(id) ON DELETE CASCADE
);

-- ============================================================
--  2. ENTIDADES PRINCIPALES
-- ============================================================

CREATE TABLE clientes (
    id               SERIAL PRIMARY KEY,
    tipo_documento   VARCHAR(5)   NOT NULL
                         CHECK (tipo_documento IN ('CC','CE','NIT','PP')),
    numero_documento VARCHAR(20)  NOT NULL UNIQUE,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    email            VARCHAR(100) NOT NULL,
    telefono         VARCHAR(20)  NOT NULL,
    direccion        VARCHAR(255),
    ciudad           VARCHAR(100),
    fecha_nacimiento DATE,
    estado           VARCHAR(10)  NOT NULL DEFAULT 'activo'
                         CHECK (estado IN ('activo','inactivo')),
    numero_mascotas  INTEGER      DEFAULT 0,
    observaciones    TEXT,
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cliente_documento ON clientes(numero_documento);
CREATE INDEX idx_cliente_email     ON clientes(email);
CREATE INDEX idx_cliente_estado    ON clientes(estado);

CREATE TABLE usuarios (
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    nombre        VARCHAR(100) NOT NULL,
    apellido      VARCHAR(100) NOT NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    cliente_id    INTEGER,
    ultimo_acceso TIMESTAMP,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_cliente FOREIGN KEY (cliente_id)
        REFERENCES clientes(id) ON DELETE SET NULL
);

CREATE INDEX idx_usuario_username ON usuarios(username);
CREATE INDEX idx_usuario_email    ON usuarios(email);
CREATE INDEX idx_usuario_activo   ON usuarios(activo);

CREATE TABLE medicos (
    id               SERIAL PRIMARY KEY,
    tipo_documento   VARCHAR(5)   NOT NULL
                         CHECK (tipo_documento IN ('CC','CE','NIT','PP')),
    numero_documento VARCHAR(20)  NOT NULL UNIQUE,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    email            VARCHAR(100) NOT NULL UNIQUE,
    telefono         VARCHAR(20)  NOT NULL,
    numero_licencia  VARCHAR(50)  NOT NULL UNIQUE,
    disponible       BOOLEAN      NOT NULL DEFAULT TRUE,
    estado           VARCHAR(10)  NOT NULL DEFAULT 'activo'
                         CHECK (estado IN ('activo','inactivo')),
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_medico_licencia ON medicos(numero_licencia);
CREATE INDEX idx_medico_estado   ON medicos(estado);
CREATE INDEX idx_medico_nombre   ON medicos(nombre, apellido);

CREATE TABLE mascotas (
    id               SERIAL PRIMARY KEY,
    nombre           VARCHAR(100)   NOT NULL,
    especie_id       INTEGER        NOT NULL,
    raza_id          INTEGER        NOT NULL,
    sexo             VARCHAR(10)    NOT NULL
                         CHECK (sexo IN ('macho','hembra')),
    fecha_nacimiento DATE,
    color            VARCHAR(50),
    peso             DECIMAL(5,2)   CHECK (peso > 0),
    microchip        VARCHAR(50)    UNIQUE,
    esterilizado     BOOLEAN        NOT NULL DEFAULT FALSE,
    estado           VARCHAR(15)    NOT NULL DEFAULT 'activo'
                         CHECK (estado IN ('activo','fallecido','transferido')),
    cliente_id       INTEGER        NOT NULL,
    observaciones    TEXT,
    created_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mascota_especie FOREIGN KEY (especie_id)
        REFERENCES especies(id),
    CONSTRAINT fk_mascota_raza    FOREIGN KEY (raza_id)
        REFERENCES razas(id),
    CONSTRAINT fk_mascota_cliente FOREIGN KEY (cliente_id)
        REFERENCES clientes(id) ON DELETE CASCADE
);

CREATE INDEX idx_mascota_nombre   ON mascotas(nombre);
CREATE INDEX idx_mascota_cliente  ON mascotas(cliente_id);
CREATE INDEX idx_mascota_estado   ON mascotas(estado);
CREATE INDEX idx_mascota_especie  ON mascotas(especie_id);


CREATE TABLE citas (
    id           SERIAL PRIMARY KEY,
    fecha        DATE         NOT NULL,
    hora_inicio  TIME         NOT NULL,
    hora_fin     TIME         NOT NULL,
    estado       VARCHAR(15)  NOT NULL DEFAULT 'pendiente'
                     CHECK (estado IN ('pendiente','confirmada','en_curso',
                                       'completada','cancelada','no_asistio')),
    tipo_cita_id INTEGER      NOT NULL,
    medico_id    INTEGER      NOT NULL,
    mascota_id   INTEGER      NOT NULL,
    cliente_id   INTEGER      NOT NULL,
    motivo       TEXT         NOT NULL,
    observaciones TEXT,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cita_tipo_cita FOREIGN KEY (tipo_cita_id)
        REFERENCES tipos_cita(id),
    CONSTRAINT fk_cita_medico    FOREIGN KEY (medico_id)
        REFERENCES medicos(id),
    CONSTRAINT fk_cita_mascota   FOREIGN KEY (mascota_id)
        REFERENCES mascotas(id),
    CONSTRAINT fk_cita_cliente   FOREIGN KEY (cliente_id)
        REFERENCES clientes(id),
    CONSTRAINT chk_hora CHECK (hora_fin > hora_inicio)
);

CREATE INDEX idx_cita_fecha    ON citas(fecha);
CREATE INDEX idx_cita_estado   ON citas(estado);
CREATE INDEX idx_cita_medico   ON citas(medico_id);
CREATE INDEX idx_cita_cliente  ON citas(cliente_id);
CREATE INDEX idx_cita_mascota  ON citas(mascota_id);
CREATE INDEX idx_cita_fecha_estado ON citas(fecha, estado);

-- ============================================================
--  3. TABLAS DE RELACIÓN N:M
-- ============================================================

CREATE TABLE roles_permisos (
    rol_id     INTEGER NOT NULL,
    permiso_id INTEGER NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    CONSTRAINT fk_rp_rol     FOREIGN KEY (rol_id)
        REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permiso FOREIGN KEY (permiso_id)
        REFERENCES permisos(id) ON DELETE CASCADE
);

CREATE TABLE usuarios_roles (
    usuario_id INTEGER NOT NULL,
    rol_id     INTEGER NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol     FOREIGN KEY (rol_id)
        REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE medicos_especialidades (
    medico_id       INTEGER NOT NULL,
    especialidad_id INTEGER NOT NULL,
    PRIMARY KEY (medico_id, especialidad_id),
    CONSTRAINT fk_me_medico       FOREIGN KEY (medico_id)
        REFERENCES medicos(id) ON DELETE CASCADE,
    CONSTRAINT fk_me_especialidad FOREIGN KEY (especialidad_id)
        REFERENCES especialidades(id) ON DELETE CASCADE
);


-- ============================================================
--  4. DATOS BASE DE CATÁLOGOS
-- ============================================================

-- Permisos
INSERT INTO permisos (id, nombre, descripcion, modulo) VALUES
(1,  'clientes.ver',         'Ver listado de clientes',         'clientes'),
(2,  'clientes.crear',       'Crear nuevos clientes',           'clientes'),
(3,  'clientes.editar',      'Editar clientes existentes',      'clientes'),
(4,  'clientes.eliminar',    'Eliminar clientes',               'clientes'),
(5,  'mascotas.ver',         'Ver listado de mascotas',         'mascotas'),
(6,  'mascotas.crear',       'Crear nuevas mascotas',           'mascotas'),
(7,  'mascotas.editar',      'Editar mascotas existentes',      'mascotas'),
(8,  'mascotas.eliminar',    'Eliminar mascotas',               'mascotas'),
(9,  'citas.ver',            'Ver listado de citas',            'citas'),
(10, 'citas.crear',          'Crear nuevas citas',              'citas'),
(11, 'citas.editar',         'Editar citas existentes',         'citas'),
(12, 'citas.eliminar',       'Eliminar citas',                  'citas'),
(13, 'citas.cambiar_estado', 'Cambiar estado de citas',         'citas'),
(14, 'medicos.ver',          'Ver listado de médicos',          'medicos'),
(15, 'medicos.crear',        'Crear nuevos médicos',            'medicos'),
(16, 'medicos.editar',       'Editar médicos existentes',       'medicos'),
(17, 'medicos.eliminar',     'Eliminar médicos',                'medicos'),
(18, 'admin.usuarios',       'Gestionar usuarios del sistema',  'admin'),
(19, 'admin.roles',          'Gestionar roles y permisos',      'admin'),
(20, 'admin.configuracion',  'Configuración del sistema',       'admin'),
(21, 'cliente.mis_mascotas', 'Ver sus propias mascotas',        'cliente'),
(22, 'cliente.mis_citas',    'Ver sus propias citas',           'cliente'),
(23, 'cliente.crear_cita',   'Crear citas como cliente',        'cliente');

SELECT setval('permisos_id_seq', 23);

-- Roles
INSERT INTO roles (id, nombre, descripcion) VALUES
(1, 'admin',          'Administrador del sistema con todos los permisos'),
(2, 'veterinario',    'Médico veterinario con permisos de atención'),
(3, 'recepcionista',  'Personal de recepción que agenda citas'),
(4, 'cliente',        'Cliente del sistema con acceso limitado');

SELECT setval('roles_id_seq', 4);

-- Roles_Permisos
INSERT INTO roles_permisos (rol_id, permiso_id) VALUES
(1,1),(2,1),(3,1),(1,2),(2,2),(3,2),(1,3),(2,3),(3,3),(1,4),(2,4),
(1,5),(2,5),(3,5),(1,6),(2,6),(3,6),(1,7),(2,7),(3,7),(1,8),(2,8),
(1,9),(2,9),(3,9),(1,10),(2,10),(3,10),(1,11),(2,11),(3,11),(1,12),(2,12),
(1,13),(2,13),(3,13),(1,14),(2,14),(1,15),(1,16),(1,17),
(1,18),(1,19),(1,20),(1,21),(4,21),(1,22),(4,22),(1,23),(4,23);

-- Especialidades
INSERT INTO especialidades (id, nombre, descripcion) VALUES
(1,  'Medicina General', 'Atención médica general para animales'),
(2,  'Cardiología',      'Especialidad en enfermedades del corazón'),
(3,  'Dermatología',     'Especialidad en enfermedades de la piel'),
(4,  'Oftalmología',     'Especialidad en enfermedades de los ojos'),
(5,  'Odontología',      'Especialidad en salud dental'),
(6,  'Ortopedia',        'Especialidad en huesos y articulaciones'),
(7,  'Neurología',       'Especialidad en sistema nervioso'),
(8,  'Oncología',        'Especialidad en cáncer'),
(9,  'Nutrición',        'Especialidad en alimentación y nutrición'),
(10, 'Comportamiento',   'Especialidad en comportamiento animal');

SELECT setval('especialidades_id_seq', 10);

-- Especies
INSERT INTO especies (id, nombre, descripcion) VALUES
(1, 'Perro',   'Canis lupus familiaris'),
(2, 'Gato',    'Felis catus'),
(3, 'Ave',     'Aves - diversas especies'),
(4, 'Reptil',  'Reptilia - diversas especies'),
(5, 'Roedor',  'Rodentia - diversas especies'),
(6, 'Conejo',  'Oryctolagus cuniculus'),
(7, 'Pez',     'Pisces - diversas especies'),
(8, 'Anfibio', 'Amphibia - diversas especies');

SELECT setval('especies_id_seq', 8);

-- Razas
INSERT INTO razas (id, nombre, especie_id) VALUES
(1,  'Labrador Retriever', 1), (2,  'Golden Retriever',  1),
(3,  'Bulldog',            1), (4,  'Poodle',            1),
(5,  'Pastor Alemán',      1), (6,  'Chihuahua',         1),
(7,  'Husky Siberiano',    1), (8,  'Beagle',            1),
(9,  'Doberman',           1), (10, 'Boxer',             1),
(11, 'Persa',              2), (12, 'Siamés',            2),
(13, 'Maine Coon',         2), (14, 'Bengalí',           2),
(15, 'Angora',             2), (16, 'Esfinge',           2),
(17, 'Ragdoll',            2), (18, 'Scottish Fold',     2),
(19, 'Burmés',             2), (20, 'Abisinio',          2);

SELECT setval('razas_id_seq', 20);

-- Tipos de cita
INSERT INTO tipos_cita (id, nombre, duracion_minutos, color, descripcion) VALUES
(1,  'Consulta General', 30, '#007bff', 'Consulta médica general'),
(2,  'Vacunación',       20, '#28a745', 'Aplicación de vacunas'),
(3,  'Desparasitación',  20, '#17a2b8', 'Desparasitación interna o externa'),
(4,  'Cirugía',          90, '#dc3545', 'Procedimiento quirúrgico'),
(5,  'Urgencia',         30, '#ffc107', 'Atención de urgencia inmediata'),
(6,  'Control',          15, '#6f42c1', 'Consulta de control o seguimiento'),
(7,  'Esterilización',   45, '#fd7e14', 'Procedimiento de esterilización'),
(8,  'Odontología',      40, '#e83e8c', 'Limpieza y cuidado dental'),
(9,  'Nutrición',        30, '#20c997', 'Consulta de nutrición'),
(10, 'Comportamiento',   45, '#6c757d', 'Consulta de comportamiento');

SELECT setval('tipos_cita_id_seq', 10);

-- ============================================================
--  5. INSERCIÓN DE DATOS MASIVOS (100+ usuarios, 1000+ clientes, 10000+ citas)
-- ============================================================

-- 5.1 Insertar 1000 clientes
DO $$
DECLARE
    i INTEGER;
    tipos_doc VARCHAR[] := ARRAY['CC', 'CE', 'NIT', 'PP'];
    nombres VARCHAR[] := ARRAY['Carlos', 'Laura', 'Pedro', 'Ana', 'Luis', 'Maria', 'Jose', 'Elena', 'Miguel', 'Sofia',
                              'Andres', 'Carmen', 'Jorge', 'Lucia', 'Fernando', 'Patricia', 'Ricardo', 'Isabel', 'Alberto', 'Rosa',
                              'Daniel', 'Marta', 'David', 'Natalia', 'Alejandro', 'Paula', 'Hector', 'Eva', 'Oscar', 'Silvia',
                              'Raul', 'Mónica', 'Adrian', 'Cristina', 'Sergio', 'Beatriz', 'Ivan', 'Alicia', 'Ruben', 'Mireia'];
    apellidos VARCHAR[] := ARRAY['García', 'Rodríguez', 'Fernández', 'Martínez', 'Pérez', 'Sánchez', 'Gómez', 'Jiménez', 'Ruiz', 'Hernández',
                                 'Díaz', 'Álvarez', 'Muñoz', 'Romero', 'Alonso', 'Gutiérrez', 'Moreno', 'Navarro', 'Torres', 'Domínguez',
                                 'Vázquez', 'Ramos', 'Gil', 'Serrano', 'Molina', 'Blanco', 'Sanz', 'Castro', 'Suárez', 'Ortega',
                                 'Rubio', 'Marín', 'Iglesias', 'Garrido', 'Parra', 'Benítez', 'Cruz', 'Prieto', 'Rey', 'Herrera'];
    ciudades VARCHAR[] := ARRAY['Quito', 'Guayaquil', 'Cuenca', 'Ambato', 'Manta', 'Ibarra', 'Latacunga', 'Riobamba', 'Tulcán', 'Machala'];
BEGIN
    FOR i IN 1..1000 LOOP
        INSERT INTO clientes (
            tipo_documento, numero_documento, nombre, apellido, email, telefono, 
            direccion, ciudad, fecha_nacimiento, estado, observaciones
        ) VALUES (
            tipos_doc[1 + (i % 4)],
            LPAD(i::VARCHAR, 10, '0'),
            nombres[1 + (i % 40)],
            apellidos[1 + (i % 40)],
            'cliente' || i || '@email.com',
            '098' || LPAD(i::VARCHAR, 7, '0'),
            'Calle ' || (i % 100 + 1) || ' #' || (i % 50 + 1) || '-' || (i % 20 + 1),
            ciudades[1 + (i % 10)],
            CURRENT_DATE - (INTERVAL '1 year' * (10 + (i % 50))),
            CASE WHEN i % 20 = 0 THEN 'inactivo' ELSE 'activo' END,
            CASE WHEN i % 50 = 0 THEN 'Cliente con observaciones especiales' ELSE NULL END
        );
    END LOOP;
END $$;

-- 5.2 Insertar 100 usuarios (asociados a clientes)
DO $$
DECLARE
    i INTEGER;
BEGIN
    FOR i IN 1..100 LOOP
        INSERT INTO usuarios (
            username, password, email, nombre, apellido, activo, cliente_id, ultimo_acceso
        ) VALUES (
            'usuario' || i,
            '$2a$10$rE7nXxq3X0kYHXvX3X0kYHXvX3X0kYHXvX3X0kYHXvX3X0kYHXvX3X0kYHX',
            'usuario' || i || '@email.com',
            (SELECT nombre FROM clientes WHERE id = i),
            (SELECT apellido FROM clientes WHERE id = i),
            TRUE,
            i,
            CURRENT_TIMESTAMP - (INTERVAL '1 day' * (i % 30))
        );
    END LOOP;
END $$;

-- 5.3 Insertar 50 médicos
DO $$
DECLARE
    i INTEGER;
    tipos_doc VARCHAR[] := ARRAY['CC', 'CE', 'NIT', 'PP'];
    nombres VARCHAR[] := ARRAY['Maria', 'Juan', 'Ana', 'Luis', 'Elena', 'Miguel', 'Carmen', 'Jose', 'Patricia', 'Carlos',
                               'Laura', 'Pedro', 'Isabel', 'Andres', 'Sofia', 'Jorge', 'Lucia', 'Fernando', 'Rosa', 'Daniel'];
    apellidos VARCHAR[] := ARRAY['González', 'Pérez', 'Martínez', 'Fernández', 'Rodríguez', 'García', 'Sánchez', 'Gómez', 'Jiménez', 'Ruiz',
                                  'Hernández', 'Díaz', 'Álvarez', 'Muñoz', 'Romero', 'Alonso', 'Gutiérrez', 'Moreno', 'Navarro', 'Torres'];
BEGIN
    FOR i IN 1..50 LOOP
        INSERT INTO medicos (
            tipo_documento, numero_documento, nombre, apellido, email, telefono, 
            numero_licencia, disponible, estado
        ) VALUES (
            tipos_doc[1 + (i % 4)],
            'DOC' || LPAD(i::VARCHAR, 10, '0'),
            nombres[1 + (i % 20)],
            apellidos[1 + (i % 20)],
            'medico' || i || '@veterinaria.com',
            '099' || LPAD(i::VARCHAR, 7, '0'),
            'LIC' || LPAD(i::VARCHAR, 6, '0'),
            TRUE,
            CASE WHEN i % 15 = 0 THEN 'inactivo' ELSE 'activo' END
        );
    END LOOP;
END $$;

-- 5.4 Insertar médicos_especialidades (cada médico tiene 1-3 especialidades)
DO $$
DECLARE
    i INTEGER;
    esp_count INTEGER;
BEGIN
    FOR i IN 1..50 LOOP
        esp_count := 1 + (i % 3);
        FOR j IN 1..esp_count LOOP
            INSERT INTO medicos_especialidades (medico_id, especialidad_id)
            VALUES (i, 1 + ((i + j) % 10));
        END LOOP;
    END LOOP;
END $$;

-- 5.5 Insertar 5000 mascotas
DO $$
DECLARE
    i INTEGER;
    nombres VARCHAR[] := ARRAY['Max', 'Luna', 'Rocky', 'Mimi', 'Toby', 'Coco', 'Daisy', 'Zeus', 'Nala', 'Simba',
                               'Milo', 'Lola', 'Leo', 'Bella', 'Charlie', 'Lucy', 'Oliver', 'Chloe', 'Jack', 'Lily',
                               'Thor', 'Sasha', 'Apollo', 'Loki', 'Hera', 'Atlas', 'Juno', 'Odin', 'Freya', 'Ares'];
BEGIN
    FOR i IN 1..5000 LOOP
        INSERT INTO mascotas (
            nombre, especie_id, raza_id, sexo, fecha_nacimiento, color, peso, 
            microchip, esterilizado, estado, cliente_id, observaciones
        ) VALUES (
            nombres[1 + (i % 30)],
            -- especie_id solo 1 (Perro) y 2 (Gato) para mantener coherencia con las 20 razas disponibles
            1 + (i % 2),
            1 + (i % 20),
            CASE WHEN i % 2 = 0 THEN 'macho' ELSE 'hembra' END,
            CURRENT_DATE - (INTERVAL '1 year' * (1 + (i % 15))),
            CASE (i % 5)
                WHEN 0 THEN 'Negro'
                WHEN 1 THEN 'Blanco'
                WHEN 2 THEN 'Marrón'
                WHEN 3 THEN 'Gris'
                ELSE 'Dorado'
            END,
            ROUND((2 + (i % 30))::NUMERIC, 2),
            'MC' || LPAD(i::VARCHAR, 6, '0'),
            CASE WHEN i % 3 = 0 THEN TRUE ELSE FALSE END,
            CASE 
                WHEN i % 50 = 0 THEN 'fallecido'
                WHEN i % 30 = 0 THEN 'transferido'
                ELSE 'activo'
            END,
            1 + (i % 1000),
            CASE WHEN i % 100 = 0 THEN 'Mascota con observaciones especiales' ELSE NULL END
        );
    END LOOP;
END $$;

-- 5.6 Insertar 15000 citas
DO $$
DECLARE
    i INTEGER;
    horas VARCHAR[] := ARRAY['08:00:00', '08:30:00', '09:00:00', '09:30:00', '10:00:00', '10:30:00', 
                             '11:00:00', '11:30:00', '12:00:00', '12:30:00', '14:00:00', '14:30:00',
                             '15:00:00', '15:30:00', '16:00:00', '16:30:00', '17:00:00', '17:30:00'];
    estados VARCHAR[] := ARRAY['pendiente', 'confirmada', 'en_curso', 'completada', 'cancelada', 'no_asistio'];
    motivos TEXT[] := ARRAY['Consulta general de rutina', 'Vacunación anual', 'Desparasitación', 
                             'Control post-esterilización', 'Consulta por tos persistente', 'Revisión de cicatrización',
                             'Problemas digestivos', 'Dolor en articulaciones', 'Infección en oídos', 'Problemas dentales',
                             'Pérdida de apetito', 'Fiebre', 'Heridas', 'Cambio de comportamiento', 'Control de peso'];
BEGIN
    FOR i IN 1..15000 LOOP
        INSERT INTO citas (
            fecha, hora_inicio, hora_fin, estado, tipo_cita_id, 
            medico_id, mascota_id, cliente_id, motivo, observaciones
        ) VALUES (
            CURRENT_DATE - (INTERVAL '1 day' * (i % 365)),
            horas[1 + (i % 18)]::TIME,
            (horas[1 + (i % 18)]::TIME + INTERVAL '30 minutes')::TIME,
            estados[1 + (i % 6)],
            1 + (i % 10),
            1 + (i % 50),
            1 + (i % 5000),
            1 + (i % 1000),
            motivos[1 + (i % 15)],
            CASE WHEN i % 20 = 0 THEN 'Observaciones de la cita' ELSE NULL END
        );
    END LOOP;
END $$;

-- 5.7 Asignar roles a usuarios (100 usuarios con diferentes roles)
DO $$
DECLARE
    i INTEGER;
BEGIN
    -- Usuario 1: admin
    INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES (1, 1);
    
    -- Usuarios 2-10: veterinarios
    FOR i IN 2..10 LOOP
        INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES (i, 2);
    END LOOP;
    
    -- Usuarios 11-20: recepcionistas
    FOR i IN 11..20 LOOP
        INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES (i, 3);
    END LOOP;
    
    -- Usuarios 21-100: clientes
    FOR i IN 21..100 LOOP
        INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES (i, 4);
    END LOOP;
END $$;

-- ============================================================
--  6. CONSULTAS DE VERIFICACIÓN (10 consultas SQL complejas)
-- ============================================================

-- Consulta 1: JOIN + GROUP BY + COUNT
-- Resumen de citas por médico con total y promedio de duración
SELECT 
    m.nombre || ' ' || m.apellido AS medico,
    COUNT(c.id) AS total_citas,
    ROUND(AVG(EXTRACT(EPOCH FROM (c.hora_fin - c.hora_inicio))/60)::NUMERIC, 2) AS duracion_promedio_min
FROM medicos m
INNER JOIN citas c ON m.id = c.medico_id
WHERE c.estado IN ('completada', 'confirmada')
GROUP BY m.id, m.nombre, m.apellido
ORDER BY total_citas DESC
LIMIT 10;

-- Consulta 2: JOIN múltiples + ILIKE + filtros
-- Clientes y sus mascotas con filtros por nombre y estado
SELECT 
    cl.nombre || ' ' || cl.apellido AS cliente,
    cl.telefono,
    m.nombre AS mascota,
    es.nombre AS especie,
    r.nombre AS raza,
    m.estado
FROM clientes cl
INNER JOIN mascotas m ON cl.id = m.cliente_id
INNER JOIN especies es ON m.especie_id = es.id
INNER JOIN razas r ON m.raza_id = r.id
WHERE cl.estado = 'activo' 
    AND m.estado = 'activo'
    AND (cl.nombre ILIKE '%a%' OR m.nombre ILIKE '%a%')
ORDER BY cl.nombre, m.nombre
LIMIT 20;

-- Consulta 3: JOIN + GROUP BY + COUNT + filtro de fecha
-- Citas por día de la semana y mes
SELECT 
    EXTRACT(DOW FROM fecha) AS dia_semana,
    EXTRACT(MONTH FROM fecha) AS mes,
    COUNT(*) AS total_citas,
    SUM(CASE WHEN estado = 'completada' THEN 1 ELSE 0 END) AS completadas
FROM citas
WHERE fecha >= CURRENT_DATE - INTERVAL '6 months'
GROUP BY EXTRACT(DOW FROM fecha), EXTRACT(MONTH FROM fecha)
ORDER BY mes, dia_semana;

-- Consulta 4: JOIN + GROUP BY + HAVING + SUM
-- Clientes con más de 3 mascotas y su total de citas
SELECT 
    cl.nombre || ' ' || cl.apellido AS cliente,
    COUNT(DISTINCT m.id) AS num_mascotas,
    COUNT(c.id) AS total_citas
FROM clientes cl
INNER JOIN mascotas m ON cl.id = m.cliente_id
LEFT JOIN citas c ON m.id = c.mascota_id
GROUP BY cl.id, cl.nombre, cl.apellido
HAVING COUNT(DISTINCT m.id) > 3
ORDER BY total_citas DESC
LIMIT 15;

-- Consulta 5: JOIN + fecha_nacimiento + edad calculada
-- Mascotas con su edad y dueños
SELECT 
    m.nombre AS mascota,
    es.nombre AS especie,
    m.sexo,
    EXTRACT(YEAR FROM AGE(CURRENT_DATE, m.fecha_nacimiento)) AS edad,
    cl.nombre || ' ' || cl.apellido AS dueno
FROM mascotas m
INNER JOIN especies es ON m.especie_id = es.id
INNER JOIN clientes cl ON m.cliente_id = cl.id
WHERE m.fecha_nacimiento IS NOT NULL 
    AND m.estado = 'activo'
    AND EXTRACT(YEAR FROM AGE(CURRENT_DATE, m.fecha_nacimiento)) BETWEEN 2 AND 10
ORDER BY edad DESC
LIMIT 20;

-- Consulta 6: JOIN + COUNT + filtro de rango de fecha
-- Médicos con citas en el último mes y su especialidad principal
SELECT 
    m.nombre || ' ' || m.apellido AS medico,
    e.nombre AS especialidad,
    COUNT(c.id) AS citas_ultimo_mes
FROM medicos m
INNER JOIN medicos_especialidades me ON m.id = me.medico_id
INNER JOIN especialidades e ON me.especialidad_id = e.id
INNER JOIN citas c ON m.id = c.medico_id
WHERE c.fecha >= CURRENT_DATE - INTERVAL '1 month'
    AND m.estado = 'activo'
GROUP BY m.id, m.nombre, m.apellido, e.nombre
ORDER BY citas_ultimo_mes DESC
LIMIT 10;

-- Consulta 7: Subconsulta + JOIN + filtro avanzado
-- Clientes con mayor número de citas no completadas
SELECT 
    cl.nombre || ' ' || cl.apellido AS cliente,
    cl.telefono,
    (
        SELECT COUNT(*) 
        FROM citas c2 
        WHERE c2.cliente_id = cl.id 
            AND c2.estado NOT IN ('completada', 'cancelada')
    ) AS citas_pendientes
FROM clientes cl
WHERE EXISTS (
    SELECT 1 
    FROM citas c 
    WHERE c.cliente_id = cl.id 
        AND c.estado NOT IN ('completada', 'cancelada')
)
ORDER BY citas_pendientes DESC
LIMIT 10;

-- Consulta 8: JOIN + WITH (CTE) + análisis de tendencia
-- Análisis de tendencia de citas por tipo en los últimos 3 meses
WITH citas_por_mes AS (
    SELECT 
        DATE_TRUNC('month', fecha) AS mes,
        tc.nombre AS tipo_cita,
        COUNT(*) AS total
    FROM citas c
    INNER JOIN tipos_cita tc ON c.tipo_cita_id = tc.id
    WHERE fecha >= CURRENT_DATE - INTERVAL '3 months'
    GROUP BY DATE_TRUNC('month', fecha), tc.nombre
)
SELECT 
    mes,
    tipo_cita,
    total,
    LAG(total, 1, 0) OVER (PARTITION BY tipo_cita ORDER BY mes) AS mes_anterior,
    total - LAG(total, 1, 0) OVER (PARTITION BY tipo_cita ORDER BY mes) AS cambio
FROM citas_por_mes
ORDER BY mes DESC, total DESC;

-- Consulta 9: JOIN múltiples + ORDER BY + LIMIT + filtros
-- Últimas 10 citas completadas con detalles de mascota y médico
SELECT 
    c.fecha,
    c.hora_inicio,
    c.estado,
    m.nombre AS mascota,
    es.nombre AS especie,
    med.nombre || ' ' || med.apellido AS medico,
    tc.nombre AS tipo_cita,
    c.motivo
FROM citas c
INNER JOIN mascotas m ON c.mascota_id = m.id
INNER JOIN especies es ON m.especie_id = es.id
INNER JOIN medicos med ON c.medico_id = med.id
INNER JOIN tipos_cita tc ON c.tipo_cita_id = tc.id
WHERE c.estado = 'completada'
ORDER BY c.fecha DESC, c.hora_inicio DESC
LIMIT 10;

-- Consulta 10: JOIN + agregación con múltiples funciones
-- Estadísticas completas de la clínica
SELECT 
    'Total Clientes' AS concepto,
    COUNT(DISTINCT id) AS valor
FROM clientes
UNION ALL
SELECT 
    'Clientes Activos',
    COUNT(DISTINCT id)
FROM clientes
WHERE estado = 'activo'
UNION ALL
SELECT 
    'Total Mascotas',
    COUNT(DISTINCT id)
FROM mascotas
UNION ALL
SELECT 
    'Mascotas Activas',
    COUNT(DISTINCT id)
FROM mascotas
WHERE estado = 'activo'
UNION ALL
SELECT 
    'Total Médicos',
    COUNT(DISTINCT id)
FROM medicos
UNION ALL
SELECT 
    'Médicos Activos',
    COUNT(DISTINCT id)
FROM medicos
WHERE estado = 'activo'
UNION ALL
SELECT 
    'Total Especialidades',
    COUNT(DISTINCT id)
FROM especialidades
UNION ALL
SELECT 
    'Total Citas',
    COUNT(DISTINCT id)
FROM citas
UNION ALL
SELECT 
    'Citas Completadas',
    COUNT(DISTINCT id)
FROM citas
WHERE estado = 'completada'
UNION ALL
SELECT 
    'Citas Pendientes',
    COUNT(DISTINCT id)
FROM citas
WHERE estado IN ('pendiente', 'confirmada');

-- ============================================================
--  FIN DEL SCRIPT DE MIGRACIÓN
-- ============================================================