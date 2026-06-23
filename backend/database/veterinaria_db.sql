-- ============================================================
--  GestionVet — Script completo de base de datos
--  Motor: MySQL 8.x
--  Base de datos: veterinaria_db
--  Usuario: admin_vet / Admin123!
-- ============================================================

CREATE DATABASE IF NOT EXISTS veterinaria_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE veterinaria_db;

-- Crear usuario si no existe y otorgar permisos
CREATE USER IF NOT EXISTS 'admin_vet'@'localhost' IDENTIFIED BY 'Admin123!';
GRANT ALL PRIVILEGES ON veterinaria_db.* TO 'admin_vet'@'localhost';
FLUSH PRIVILEGES;

-- ============================================================
--  1. PERMISOS
-- ============================================================
CREATE TABLE IF NOT EXISTS permisos (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    modulo      VARCHAR(50)
) ENGINE=InnoDB;

-- ============================================================
--  2. ROLES
-- ============================================================
CREATE TABLE IF NOT EXISTS roles (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200)
) ENGINE=InnoDB;

-- ============================================================
--  3. ROLES_PERMISOS (N:M)
-- ============================================================
CREATE TABLE IF NOT EXISTS roles_permisos (
    rol_id     INT NOT NULL,
    permiso_id INT NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    CONSTRAINT fk_rp_rol     FOREIGN KEY (rol_id)     REFERENCES roles(id)    ON DELETE CASCADE,
    CONSTRAINT fk_rp_permiso FOREIGN KEY (permiso_id) REFERENCES permisos(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
--  4. CLIENTES
-- ============================================================
CREATE TABLE IF NOT EXISTS clientes (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento    VARCHAR(5)   NOT NULL,
    numero_documento  VARCHAR(20)  NOT NULL UNIQUE,
    nombre            VARCHAR(100) NOT NULL,
    apellido          VARCHAR(100) NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,
    telefono          VARCHAR(20)  NOT NULL,
    direccion         VARCHAR(200),
    ciudad            VARCHAR(100),
    fecha_nacimiento  DATE,
    estado            VARCHAR(10)  NOT NULL DEFAULT 'activo',
    observaciones     TEXT,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
--  5. USUARIOS
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    nombre        VARCHAR(100) NOT NULL,
    apellido      VARCHAR(100) NOT NULL,
    activo        TINYINT(1)   NOT NULL DEFAULT 1,
    ultimo_acceso DATETIME,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente_id    INT,
    CONSTRAINT fk_usuario_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
--  6. USUARIOS_ROLES (N:M)
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios_roles (
    usuario_id INT NOT NULL,
    rol_id     INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol     FOREIGN KEY (rol_id)     REFERENCES roles(id)    ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
--  7. ESPECIALIDADES
-- ============================================================
CREATE TABLE IF NOT EXISTS especialidades (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200)
) ENGINE=InnoDB;

-- ============================================================
--  8. MEDICOS
-- ============================================================
CREATE TABLE IF NOT EXISTS medicos (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento   VARCHAR(5)   NOT NULL,
    numero_documento VARCHAR(20)  NOT NULL UNIQUE,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    telefono         VARCHAR(20)  NOT NULL,
    numero_licencia  VARCHAR(30)  NOT NULL UNIQUE,
    disponible       TINYINT(1)   NOT NULL DEFAULT 1,
    estado           VARCHAR(10)  NOT NULL DEFAULT 'activo',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
--  9. MEDICOS_ESPECIALIDADES (N:M)
-- ============================================================
CREATE TABLE IF NOT EXISTS medicos_especialidades (
    medico_id       INT NOT NULL,
    especialidad_id INT NOT NULL,
    PRIMARY KEY (medico_id, especialidad_id),
    CONSTRAINT fk_me_medico       FOREIGN KEY (medico_id)       REFERENCES medicos(id)       ON DELETE CASCADE,
    CONSTRAINT fk_me_especialidad FOREIGN KEY (especialidad_id) REFERENCES especialidades(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
--  10. ESPECIES
-- ============================================================
CREATE TABLE IF NOT EXISTS especies (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200)
) ENGINE=InnoDB;

-- ============================================================
--  11. RAZAS
-- ============================================================
CREATE TABLE IF NOT EXISTS razas (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    especie_id INT NOT NULL,
    CONSTRAINT fk_raza_especie FOREIGN KEY (especie_id) REFERENCES especies(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
--  12. MASCOTAS
-- ============================================================
CREATE TABLE IF NOT EXISTS mascotas (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100)   NOT NULL,
    especie_id       INT            NOT NULL,
    raza_id          INT            NOT NULL,
    sexo             VARCHAR(10)    NOT NULL,
    fecha_nacimiento DATE,
    color            VARCHAR(50),
    peso             DECIMAL(5, 2),
    microchip        VARCHAR(20),
    esterilizado     TINYINT(1)     NOT NULL DEFAULT 0,
    estado           VARCHAR(15)    NOT NULL DEFAULT 'activo',
    cliente_id       INT            NOT NULL,
    observaciones    TEXT,
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mascota_especie FOREIGN KEY (especie_id) REFERENCES especies(id),
    CONSTRAINT fk_mascota_raza    FOREIGN KEY (raza_id)    REFERENCES razas(id),
    CONSTRAINT fk_mascota_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
--  13. TIPOS_CITA
-- ============================================================
CREATE TABLE IF NOT EXISTS tipos_cita (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL,
    duracion_minutos  INT          NOT NULL,
    color             VARCHAR(20),
    descripcion       VARCHAR(200)
) ENGINE=InnoDB;

-- ============================================================
--  14. CITAS
-- ============================================================
CREATE TABLE IF NOT EXISTS citas (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    fecha        DATE        NOT NULL,
    hora_inicio  TIME        NOT NULL,
    hora_fin     TIME        NOT NULL,
    estado       VARCHAR(15) NOT NULL DEFAULT 'pendiente',
    tipo_cita_id INT         NOT NULL,
    medico_id    INT         NOT NULL,
    mascota_id   INT         NOT NULL,
    cliente_id   INT         NOT NULL,
    motivo       VARCHAR(300) NOT NULL,
    observaciones TEXT,
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cita_tipo_cita FOREIGN KEY (tipo_cita_id) REFERENCES tipos_cita(id),
    CONSTRAINT fk_cita_medico    FOREIGN KEY (medico_id)    REFERENCES medicos(id),
    CONSTRAINT fk_cita_mascota   FOREIGN KEY (mascota_id)   REFERENCES mascotas(id),
    CONSTRAINT fk_cita_cliente   FOREIGN KEY (cliente_id)   REFERENCES clientes(id)
) ENGINE=InnoDB;


-- ============================================================
--  DATOS DE PRUEBA
-- ============================================================

-- Permisos
INSERT INTO permisos (nombre, descripcion, modulo) VALUES
('clientes.ver',        'Ver clientes',               'personas'),
('clientes.crear',      'Crear clientes',             'personas'),
('clientes.editar',     'Editar clientes',            'personas'),
('medicos.ver',         'Ver médicos',                'personas'),
('medicos.crear',       'Crear médicos',              'personas'),
('mascotas.ver',        'Ver mascotas (todas)',        'mascotas'),
('mascotas.crear',      'Crear mascotas',             'mascotas'),
('mascotas.editar',     'Editar mascotas',            'mascotas'),
('citas.ver',           'Ver citas (todas)',           'citas'),
('citas.crear',         'Crear citas',                'citas'),
('citas.editar',        'Editar citas',               'citas'),
('citas.cancelar',      'Cancelar citas',             'citas'),
('admin.usuarios',      'Administrar usuarios',       'admin'),
('cliente.mis-mascotas','Ver mis mascotas propias',   'cliente'),
('cliente.mis-citas',   'Ver mis citas propias',      'cliente');

-- Roles
INSERT INTO roles (nombre, descripcion) VALUES
('admin',          'Administrador del sistema'),
('veterinario',    'Médico veterinario'),
('recepcionista',  'Recepcionista'),
('cliente',        'Cliente de la veterinaria');

-- Roles_Permisos
-- admin: todos los permisos (1-13)
INSERT INTO roles_permisos (rol_id, permiso_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13);
-- veterinario: 1,6,8,9,11
INSERT INTO roles_permisos (rol_id, permiso_id) VALUES (2,1),(2,6),(2,8),(2,9),(2,11);
-- recepcionista: 1,2,3,6,7,9,10,12
INSERT INTO roles_permisos (rol_id, permiso_id) VALUES (3,1),(3,2),(3,3),(3,6),(3,7),(3,9),(3,10),(3,12);
-- cliente: 14,15
INSERT INTO roles_permisos (rol_id, permiso_id) VALUES (4,14),(4,15);

-- Especialidades
INSERT INTO especialidades (nombre, descripcion) VALUES
('Medicina General',  'Atención veterinaria general'),
('Cirugía',           'Procedimientos quirúrgicos'),
('Dermatología',      'Enfermedades de la piel'),
('Cardiología',       'Enfermedades del corazón'),
('Oftalmología',      'Enfermedades de los ojos'),
('Odontología',       'Salud dental veterinaria'),
('Nutrición',         'Nutrición y dietética animal'),
('Oncología',         'Tratamiento del cáncer');

-- Especies
INSERT INTO especies (nombre, descripcion) VALUES
('Perro',   'Canis lupus familiaris'),
('Gato',    'Felis catus'),
('Conejo',  'Oryctolagus cuniculus'),
('Ave',     'Aves domésticas'),
('Reptil',  'Reptiles domésticos'),
('Hámster', 'Cricetinae');

-- Razas
INSERT INTO razas (nombre, especie_id) VALUES
('Labrador Retriever', 1),
('Golden Retriever',   1),
('Bulldog Francés',    1),
('Poodle',             1),
('Beagle',             1),
('Chihuahua',          1),
('Mestizo',            1),
('Persa',              2),
('Siamés',             2),
('Maine Coon',         2),
('Bengalí',            2),
('Doméstico',          2),
('Holland Lop',        3),
('Mini Rex',           3),
('Periquito',          4),
('Loro',               4),
('Canario',            4);

-- Tipos de cita
INSERT INTO tipos_cita (nombre, duracion_minutos, color, descripcion) VALUES
('Consulta General',        30,  '#10b981', 'Revisión general del paciente'),
('Vacunación',              15,  '#3b82f6', 'Aplicación de vacunas'),
('Cirugía',                 120, '#f43f5e', 'Procedimiento quirúrgico'),
('Control Post-operatorio', 20,  '#8b5cf6', 'Seguimiento post cirugía'),
('Desparasitación',         15,  '#f59e0b', 'Tratamiento antiparasitario'),
('Baño y Peluquería',       60,  '#06b6d4', 'Servicio de estética'),
('Urgencia',                45,  '#ef4444', 'Atención de emergencia');

-- Clientes
INSERT INTO clientes (tipo_documento, numero_documento, nombre, apellido, email, telefono, direccion, ciudad, fecha_nacimiento, estado, observaciones) VALUES
('CC', '12345678', 'Ana',       'Martínez',  'ana.martinez@email.com',    '3001234567', 'Calle 45 #12-34', 'Bogotá',   '1985-03-15', 'activo',   'Cliente frecuente'),
('CC', '87654321', 'Pedro',     'Rodríguez', 'pedro.rodriguez@email.com', '3109876543', 'Carrera 7 #89-12','Medellín', NULL,         'activo',   NULL),
('CE', '55566677', 'Sofía',     'Hernández', 'sofia.h@email.com',         '3205556677', NULL,              'Cali',     NULL,         'activo',   NULL),
('CC', '11223344', 'Luis',      'Gómez',     'luis.gomez@email.com',      '3151122334', NULL,              'Bogotá',   NULL,         'inactivo', NULL),
('CC', '99887766', 'Valentina', 'Torres',    'vale.torres@email.com',     '3009988776', 'Av. 68 #45-67',  'Bogotá',   NULL,         'activo',   NULL);

-- Usuarios
INSERT INTO usuarios (username, password, email, nombre, apellido, activo, cliente_id) VALUES
('admin',           'admin123', 'admin@vetclinic.com',         'Carlos',    'Administrador', 1, NULL),
('dra.garcia',      'vet123',   'garcia@vetclinic.com',        'Laura',     'García',        1, NULL),
('recepcion',       'rec123',   'recepcion@vetclinic.com',     'María',     'López',         1, NULL),
('ana.martinez',    'cli123',   'ana.martinez@email.com',      'Ana',       'Martínez',      1, 1),
('pedro.rodriguez', 'cli123',   'pedro.rodriguez@email.com',   'Pedro',     'Rodríguez',     1, 2),
('sofia.hernandez', 'cli123',   'sofia.h@email.com',           'Sofía',     'Hernández',     1, 3),
('vale.torres',     'cli123',   'vale.torres@email.com',       'Valentina', 'Torres',        1, 5);

-- Usuarios_Roles
INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES
(1, 1), -- admin     → admin
(2, 2), -- dra.garcia → veterinario
(3, 3), -- recepcion  → recepcionista
(4, 4), -- ana.martinez → cliente
(5, 4), -- pedro.rodriguez → cliente
(6, 4), -- sofia.hernandez → cliente
(7, 4); -- vale.torres → cliente

-- Médicos
INSERT INTO medicos (tipo_documento, numero_documento, nombre, apellido, email, telefono, numero_licencia, disponible, estado) VALUES
('CC', '44556677', 'Laura',  'García', 'garcia@vetclinic.com', '3004455667', 'VET-2018-001', 1, 'activo'),
('CC', '33445566', 'Andrés', 'Vargas', 'vargas@vetclinic.com', '3113344556', 'VET-2020-045', 1, 'activo'),
('CC', '22334455', 'Camila', 'Reyes',  'reyes@vetclinic.com',  '3202233445', 'VET-2019-023', 0, 'activo');

-- Médicos_Especialidades
INSERT INTO medicos_especialidades (medico_id, especialidad_id) VALUES
(1, 1),(1, 2),  -- Laura García: Medicina General, Cirugía
(2, 3),(2, 5),  -- Andrés Vargas: Dermatología, Oftalmología
(3, 4),(3, 8);  -- Camila Reyes: Cardiología, Oncología

-- Mascotas
INSERT INTO mascotas (nombre, especie_id, raza_id, sexo, fecha_nacimiento, color, peso, microchip, esterilizado, estado, cliente_id, observaciones) VALUES
('Max',   1, 1,  'macho',  '2020-05-10', 'Amarillo',   28.5, '985112345678901', 1, 'activo', 1, 'Alérgico al pollo'),
('Luna',  2, 8,  'hembra', '2021-08-22', 'Blanco',      4.2, NULL,              1, 'activo', 1, NULL),
('Rocky', 1, 3,  'macho',  '2022-03-14', 'Atigrado',   12.0, NULL,              0, 'activo', 2, NULL),
('Mia',   2, 9,  'hembra', '2019-11-30', 'Seal point',  3.8, NULL,              1, 'activo', 3, NULL),
('Toby',  1, 5,  'macho',  '2023-01-05', 'Tricolor',    9.5, NULL,              0, 'activo', 3, NULL),
('Coco',  3, 13, 'macho',  '2024-02-14', 'Gris',        1.8, NULL,              0, 'activo', 3, NULL),
('Bella', 1, 2,  'hembra', '2021-07-18', 'Dorado',     25.0, '985198765432100', 1, 'activo', 5, NULL);

-- Citas
INSERT INTO citas (fecha, hora_inicio, hora_fin, estado, tipo_cita_id, medico_id, mascota_id, cliente_id, motivo, observaciones) VALUES
('2026-05-07', '09:00', '09:30', 'confirmada', 1, 1, 1, 1, 'Revisión anual y vacunas',             NULL),
('2026-05-07', '10:00', '10:15', 'pendiente',  2, 1, 4, 3, 'Vacuna antirrábica',                   NULL),
('2026-05-07', '11:00', '13:00', 'en_curso',   3, 2, 3, 2, 'Esterilización',                       NULL),
('2026-05-08', '09:30', '09:50', 'pendiente',  4, 2, 3, 2, 'Control post-operatorio esterilización',NULL),
('2026-05-08', '14:00', '14:15', 'pendiente',  5, 1, 7, 5, 'Desparasitación trimestral',           NULL),
('2026-05-06', '10:00', '10:30', 'completada', 1, 1, 2, 1, 'Revisión dermatológica',               'Se recetó shampoo medicado'),
('2026-05-06', '15:00', '15:45', 'cancelada',  7, 2, 5, 3, 'Urgencia - vómitos',                   'Cliente canceló'),
('2026-05-09', '09:00', '10:00', 'pendiente',  6, 1, 1, 1, 'Baño y corte de uñas',                 NULL);
