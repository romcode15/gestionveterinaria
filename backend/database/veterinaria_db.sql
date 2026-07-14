-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 10-07-2026 a las 12:24:10
-- Versión del servidor: 8.4.7
-- Versión de PHP: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `veterinaria_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `citas`
--

DROP TABLE IF EXISTS `citas`;
CREATE TABLE IF NOT EXISTS `citas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `estado` enum('pendiente','confirmada','en_curso','completada','cancelada','no_asistio') COLLATE utf8mb4_unicode_ci DEFAULT 'pendiente',
  `tipo_cita_id` int NOT NULL,
  `medico_id` int NOT NULL,
  `mascota_id` int NOT NULL,
  `cliente_id` int NOT NULL,
  `motivo` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `tipo_cita_id` (`tipo_cita_id`),
  KEY `idx_cita_fecha` (`fecha`),
  KEY `idx_cita_estado` (`estado`),
  KEY `idx_cita_medico` (`medico_id`),
  KEY `idx_cita_cliente` (`cliente_id`),
  KEY `idx_cita_mascota` (`mascota_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `citas`
--

INSERT INTO `citas` (`id`, `fecha`, `hora_inicio`, `hora_fin`, `estado`, `tipo_cita_id`, `medico_id`, `mascota_id`, `cliente_id`, `motivo`, `observaciones`, `created_at`) VALUES
(1, '2026-06-22', '09:00:00', '09:30:00', 'confirmada', 1, 1, 1, 2, 'Consulta general de rutina', NULL, '2026-06-22 06:24:13'),
(2, '2026-06-22', '10:00:00', '10:20:00', 'confirmada', 2, 2, 2, 3, 'Vacunación anual', 'Se requiere revacunación de rabia', '2026-06-22 06:24:13'),
(3, '2026-06-23', '14:30:00', '15:00:00', 'pendiente', 1, 3, 3, 4, 'Consulta por tos persistente', 'Traer análisis de sangre recientes', '2026-06-22 06:24:13'),
(4, '2026-06-23', '16:00:00', '16:30:00', 'pendiente', 6, 1, 4, 2, 'Control post-esterilización', 'Revisión de cicatrización', '2026-06-22 06:24:13'),
(5, '2026-06-22', '09:00:00', '09:30:00', 'pendiente', 1, 1, 1, 2, 'Consulta general de rutina', 'Primera vez que se atiende', '2026-06-23 05:29:32');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clientes`
--

DROP TABLE IF EXISTS `clientes`;
CREATE TABLE IF NOT EXISTS `clientes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo_documento` enum('CC','CE','NIT','PP') COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_documento` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ciudad` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `estado` enum('activo','inactivo') COLLATE utf8mb4_unicode_ci DEFAULT 'activo',
  `numero_mascotas` int DEFAULT '0',
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  KEY `idx_cliente_documento` (`numero_documento`),
  KEY `idx_cliente_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `clientes`
--

INSERT INTO `clientes` (`id`, `tipo_documento`, `numero_documento`, `nombre`, `apellido`, `email`, `telefono`, `direccion`, `ciudad`, `fecha_nacimiento`, `estado`, `numero_mascotas`, `observaciones`, `created_at`) VALUES
(1, 'CC', '9999999999', 'Admin', 'Sistema', 'admin@veterinaria.com', '0999999999', NULL, NULL, NULL, 'activo', 0, 'Usuario administrador del sistema', '2026-06-22 06:23:03'),
(2, 'CC', '1111111111', 'Carlos', 'Rodríguez', 'carlos.r@email.com', '0981111111', 'Calle 123', 'Quito', '1980-05-15', 'activo', 0, NULL, '2026-06-22 06:23:54'),
(3, 'CC', '2222222222', 'Laura', 'Fernández', 'laura.f@email.com', '0982222222', 'Avenida 456', 'Guayaquil', '1992-08-22', 'activo', 0, NULL, '2026-06-22 06:23:54'),
(4, 'CC', '3333333333', 'Pedro', 'García', 'pedro.g@email.com', '0983333333', 'Calle 789', 'Cuenca', '1975-03-10', 'activo', 0, NULL, '2026-06-22 06:23:54');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `especialidades`
--

DROP TABLE IF EXISTS `especialidades`;
CREATE TABLE IF NOT EXISTS `especialidades` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `especialidades`
--

INSERT INTO `especialidades` (`id`, `nombre`, `descripcion`, `created_at`) VALUES
(1, 'Medicina General', 'Atención médica general para animales', '2026-06-22 06:22:13'),
(2, 'Cardiología', 'Especialidad en enfermedades del corazón', '2026-06-22 06:22:13'),
(3, 'Dermatología', 'Especialidad en enfermedades de la piel', '2026-06-22 06:22:13'),
(4, 'Oftalmología', 'Especialidad en enfermedades de los ojos', '2026-06-22 06:22:13'),
(5, 'Odontología', 'Especialidad en salud dental', '2026-06-22 06:22:13'),
(6, 'Ortopedia', 'Especialidad en huesos y articulaciones', '2026-06-22 06:22:13'),
(7, 'Neurología', 'Especialidad en sistema nervioso', '2026-06-22 06:22:13'),
(8, 'Oncología', 'Especialidad en cáncer', '2026-06-22 06:22:13'),
(9, 'Nutrición', 'Especialidad en alimentación y nutrición', '2026-06-22 06:22:13'),
(10, 'Comportamiento', 'Especialidad en comportamiento animal', '2026-06-22 06:22:13');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `especies`
--

DROP TABLE IF EXISTS `especies`;
CREATE TABLE IF NOT EXISTS `especies` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `especies`
--

INSERT INTO `especies` (`id`, `nombre`, `descripcion`, `created_at`) VALUES
(1, 'Perro', 'Canis lupus familiaris', '2026-06-22 06:22:25'),
(2, 'Gato', 'Felis catus', '2026-06-22 06:22:25'),
(3, 'Ave', 'Aves - diversas especies', '2026-06-22 06:22:25'),
(4, 'Reptil', 'Reptilia - diversas especies', '2026-06-22 06:22:25'),
(5, 'Roedor', 'Rodentia - diversas especies', '2026-06-22 06:22:25'),
(6, 'Conejo', 'Oryctolagus cuniculus', '2026-06-22 06:22:25'),
(7, 'Pez', 'Pisces - diversas especies', '2026-06-22 06:22:25'),
(8, 'Anfibio', 'Amphibia - diversas especies', '2026-06-22 06:22:25');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mascotas`
--

DROP TABLE IF EXISTS `mascotas`;
CREATE TABLE IF NOT EXISTS `mascotas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `especie_id` int NOT NULL,
  `raza_id` int NOT NULL,
  `sexo` enum('macho','hembra') COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `color` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `peso` decimal(5,2) DEFAULT NULL,
  `microchip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `esterilizado` tinyint(1) DEFAULT '0',
  `estado` enum('activo','fallecido','transferido') COLLATE utf8mb4_unicode_ci DEFAULT 'activo',
  `cliente_id` int NOT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `microchip` (`microchip`),
  KEY `especie_id` (`especie_id`),
  KEY `raza_id` (`raza_id`),
  KEY `idx_mascota_nombre` (`nombre`),
  KEY `idx_mascota_cliente` (`cliente_id`),
  KEY `idx_mascota_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `mascotas`
--

INSERT INTO `mascotas` (`id`, `nombre`, `especie_id`, `raza_id`, `sexo`, `fecha_nacimiento`, `color`, `peso`, `microchip`, `esterilizado`, `estado`, `cliente_id`, `observaciones`, `created_at`) VALUES
(1, 'Max', 1, 1, 'macho', '2020-01-15', 'Dorado', 28.50, 'MC001', 0, 'activo', 2, NULL, '2026-06-22 06:24:03'),
(2, 'Luna', 2, 12, 'hembra', '2021-06-20', 'Blanco y negro', 4.20, 'MC002', 1, 'activo', 3, NULL, '2026-06-22 06:24:03'),
(3, 'Rocky', 1, 5, 'macho', '2019-11-10', 'Negro', 32.00, 'MC003', 0, 'activo', 4, NULL, '2026-06-22 06:24:03'),
(4, 'Mimi', 2, 11, 'hembra', '2022-03-05', 'Blanco', 3.80, 'MC004', 1, 'activo', 2, NULL, '2026-06-22 06:24:03'),
(9, 'Max', 1, 1, 'macho', '2020-01-15', 'Dorado', 28.50, 'MC005', 0, 'activo', 2, 'Mascota registrada correctamente', '2026-06-23 05:26:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicos`
--

DROP TABLE IF EXISTS `medicos`;
CREATE TABLE IF NOT EXISTS `medicos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo_documento` enum('CC','CE','NIT','PP') COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_documento` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_licencia` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `disponible` tinyint(1) DEFAULT '1',
  `estado` enum('activo','inactivo') COLLATE utf8mb4_unicode_ci DEFAULT 'activo',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `numero_licencia` (`numero_licencia`),
  KEY `idx_medico_licencia` (`numero_licencia`),
  KEY `idx_medico_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `medicos`
--

INSERT INTO `medicos` (`id`, `tipo_documento`, `numero_documento`, `nombre`, `apellido`, `email`, `telefono`, `numero_licencia`, `disponible`, `estado`, `created_at`) VALUES
(1, 'CC', '1234567890', 'María', 'González', 'maria.g@veterinaria.com', '0991234567', 'LIC001', 1, 'activo', '2026-06-22 06:23:35'),
(2, 'CC', '0987654321', 'Juan', 'Pérez', 'juan.p@veterinaria.com', '0997654321', 'LIC002', 1, 'activo', '2026-06-22 06:23:35'),
(3, 'CC', '1122334455', 'Ana', 'Martínez', 'ana.m@veterinaria.com', '0991122334', 'LIC003', 1, 'activo', '2026-06-22 06:23:35');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicos_especialidades`
--

DROP TABLE IF EXISTS `medicos_especialidades`;
CREATE TABLE IF NOT EXISTS `medicos_especialidades` (
  `medico_id` int NOT NULL,
  `especialidad_id` int NOT NULL,
  PRIMARY KEY (`medico_id`,`especialidad_id`),
  KEY `especialidad_id` (`especialidad_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `medicos_especialidades`
--

INSERT INTO `medicos_especialidades` (`medico_id`, `especialidad_id`) VALUES
(1, 1),
(2, 1),
(3, 1),
(3, 3),
(1, 4),
(2, 6);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permisos`
--

DROP TABLE IF EXISTS `permisos`;
CREATE TABLE IF NOT EXISTS `permisos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `modulo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `idx_permiso_modulo` (`modulo`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `permisos`
--

INSERT INTO `permisos` (`id`, `nombre`, `descripcion`, `modulo`, `created_at`) VALUES
(1, 'clientes.ver', 'Ver listado de clientes', 'clientes', '2026-06-22 06:21:27'),
(2, 'clientes.crear', 'Crear nuevos clientes', 'clientes', '2026-06-22 06:21:27'),
(3, 'clientes.editar', 'Editar clientes existentes', 'clientes', '2026-06-22 06:21:27'),
(4, 'clientes.eliminar', 'Eliminar clientes', 'clientes', '2026-06-22 06:21:27'),
(5, 'mascotas.ver', 'Ver listado de mascotas', 'mascotas', '2026-06-22 06:21:27'),
(6, 'mascotas.crear', 'Crear nuevas mascotas', 'mascotas', '2026-06-22 06:21:27'),
(7, 'mascotas.editar', 'Editar mascotas existentes', 'mascotas', '2026-06-22 06:21:27'),
(8, 'mascotas.eliminar', 'Eliminar mascotas', 'mascotas', '2026-06-22 06:21:27'),
(9, 'citas.ver', 'Ver listado de citas', 'citas', '2026-06-22 06:21:27'),
(10, 'citas.crear', 'Crear nuevas citas', 'citas', '2026-06-22 06:21:27'),
(11, 'citas.editar', 'Editar citas existentes', 'citas', '2026-06-22 06:21:27'),
(12, 'citas.eliminar', 'Eliminar citas', 'citas', '2026-06-22 06:21:27'),
(13, 'citas.cambiar_estado', 'Cambiar estado de citas', 'citas', '2026-06-22 06:21:27'),
(14, 'medicos.ver', 'Ver listado de médicos', 'medicos', '2026-06-22 06:21:27'),
(15, 'medicos.crear', 'Crear nuevos médicos', 'medicos', '2026-06-22 06:21:27'),
(16, 'medicos.editar', 'Editar médicos existentes', 'medicos', '2026-06-22 06:21:27'),
(17, 'medicos.eliminar', 'Eliminar médicos', 'medicos', '2026-06-22 06:21:27'),
(18, 'admin.usuarios', 'Gestionar usuarios del sistema', 'admin', '2026-06-22 06:21:27'),
(19, 'admin.roles', 'Gestionar roles y permisos', 'admin', '2026-06-22 06:21:27'),
(20, 'admin.configuracion', 'Configuración del sistema', 'admin', '2026-06-22 06:21:27'),
(21, 'cliente.mis_mascotas', 'Ver sus propias mascotas', 'cliente', '2026-06-22 06:21:27'),
(22, 'cliente.mis_citas', 'Ver sus propias citas', 'cliente', '2026-06-22 06:21:27'),
(23, 'cliente.crear_cita', 'Crear citas como cliente', 'cliente', '2026-06-22 06:21:27');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `razas`
--

DROP TABLE IF EXISTS `razas`;
CREATE TABLE IF NOT EXISTS `razas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `especie_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `especie_id` (`especie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `razas`
--

INSERT INTO `razas` (`id`, `nombre`, `especie_id`, `created_at`) VALUES
(1, 'Labrador Retriever', 1, '2026-06-22 06:22:37'),
(2, 'Golden Retriever', 1, '2026-06-22 06:22:37'),
(3, 'Bulldog', 1, '2026-06-22 06:22:37'),
(4, 'Poodle', 1, '2026-06-22 06:22:37'),
(5, 'Pastor Alemán', 1, '2026-06-22 06:22:37'),
(6, 'Chihuahua', 1, '2026-06-22 06:22:37'),
(7, 'Husky Siberiano', 1, '2026-06-22 06:22:37'),
(8, 'Beagle', 1, '2026-06-22 06:22:37'),
(9, 'Doberman', 1, '2026-06-22 06:22:37'),
(10, 'Boxer', 1, '2026-06-22 06:22:37'),
(11, 'Persa', 2, '2026-06-22 06:22:37'),
(12, 'Siamés', 2, '2026-06-22 06:22:37'),
(13, 'Maine Coon', 2, '2026-06-22 06:22:37'),
(14, 'Bengalí', 2, '2026-06-22 06:22:37'),
(15, 'Angora', 2, '2026-06-22 06:22:37'),
(16, 'Esfinge', 2, '2026-06-22 06:22:37'),
(17, 'Ragdoll', 2, '2026-06-22 06:22:37'),
(18, 'Scottish Fold', 2, '2026-06-22 06:22:37'),
(19, 'Burmés', 2, '2026-06-22 06:22:37'),
(20, 'Abisinio', 2, '2026-06-22 06:22:37');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` enum('admin','veterinario','recepcionista','cliente') COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id`, `nombre`, `descripcion`, `created_at`) VALUES
(1, 'admin', 'Administrador del sistema con todos los permisos', '2026-06-22 06:21:45'),
(2, 'veterinario', 'Médico veterinario con permisos de atención', '2026-06-22 06:21:45'),
(3, 'recepcionista', 'Personal de recepción que agenda citas', '2026-06-22 06:21:45'),
(4, 'cliente', 'Cliente del sistema con acceso limitado', '2026-06-22 06:21:45');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles_permisos`
--

DROP TABLE IF EXISTS `roles_permisos`;
CREATE TABLE IF NOT EXISTS `roles_permisos` (
  `rol_id` int NOT NULL,
  `permiso_id` int NOT NULL,
  PRIMARY KEY (`rol_id`,`permiso_id`),
  KEY `permiso_id` (`permiso_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `roles_permisos`
--

INSERT INTO `roles_permisos` (`rol_id`, `permiso_id`) VALUES
(1, 1),
(2, 1),
(3, 1),
(1, 2),
(2, 2),
(3, 2),
(1, 3),
(2, 3),
(3, 3),
(1, 4),
(2, 4),
(1, 5),
(2, 5),
(3, 5),
(1, 6),
(2, 6),
(3, 6),
(1, 7),
(2, 7),
(3, 7),
(1, 8),
(2, 8),
(1, 9),
(2, 9),
(3, 9),
(1, 10),
(2, 10),
(3, 10),
(1, 11),
(2, 11),
(3, 11),
(1, 12),
(2, 12),
(1, 13),
(2, 13),
(3, 13),
(1, 14),
(2, 14),
(1, 15),
(1, 16),
(1, 17),
(1, 18),
(1, 19),
(1, 20),
(1, 21),
(4, 21),
(1, 22),
(4, 22),
(1, 23),
(4, 23);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipos_cita`
--

DROP TABLE IF EXISTS `tipos_cita`;
CREATE TABLE IF NOT EXISTS `tipos_cita` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `duracion_minutos` int NOT NULL DEFAULT '30',
  `color` varchar(7) COLLATE utf8mb4_unicode_ci DEFAULT '#007bff',
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `tipos_cita`
--

INSERT INTO `tipos_cita` (`id`, `nombre`, `duracion_minutos`, `color`, `descripcion`, `created_at`) VALUES
(1, 'Consulta General', 30, '#007bff', 'Consulta médica general', '2026-06-22 06:22:49'),
(2, 'Vacunación', 20, '#28a745', 'Aplicación de vacunas', '2026-06-22 06:22:49'),
(3, 'Desparasitación', 20, '#17a2b8', 'Desparasitación interna o externa', '2026-06-22 06:22:49'),
(4, 'Cirugía', 90, '#dc3545', 'Procedimiento quirúrgico', '2026-06-22 06:22:49'),
(5, 'Urgencia', 30, '#ffc107', 'Atención de urgencia inmediata', '2026-06-22 06:22:49'),
(6, 'Control', 15, '#6f42c1', 'Consulta de control o seguimiento', '2026-06-22 06:22:49'),
(7, 'Esterilización', 45, '#fd7e14', 'Procedimiento de esterilización', '2026-06-22 06:22:49'),
(8, 'Odontología', 40, '#e83e8c', 'Limpieza y cuidado dental', '2026-06-22 06:22:49'),
(9, 'Nutrición', 30, '#20c997', 'Consulta de nutrición', '2026-06-22 06:22:49'),
(10, 'Comportamiento', 45, '#6c757d', 'Consulta de comportamiento', '2026-06-22 06:22:49');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `cliente_id` int DEFAULT NULL,
  `ultimo_acceso` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `cliente_id` (`cliente_id`),
  KEY `idx_usuario_username` (`username`),
  KEY `idx_usuario_email` (`email`),
  KEY `idx_usuario_activo` (`activo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `username`, `password`, `email`, `nombre`, `apellido`, `activo`, `cliente_id`, `ultimo_acceso`, `created_at`) VALUES
(1, 'admin', '$2a$10$rE7nXxq3X0kYHXvX3X0kYHXvX3X0kYHXvX3X0kYHXvX3X0kYHXvX3X0kYHX', 'admin@veterinaria.com', 'Admin', 'Sistema', 1, 1, '2026-06-22 06:23:11', '2026-06-22 06:23:11');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios_roles`
--

DROP TABLE IF EXISTS `usuarios_roles`;
CREATE TABLE IF NOT EXISTS `usuarios_roles` (
  `usuario_id` int NOT NULL,
  `rol_id` int NOT NULL,
  PRIMARY KEY (`usuario_id`,`rol_id`),
  KEY `rol_id` (`rol_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `usuarios_roles`
--

INSERT INTO `usuarios_roles` (`usuario_id`, `rol_id`) VALUES
(1, 1);

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `citas`
--
ALTER TABLE `citas`
  ADD CONSTRAINT `citas_ibfk_1` FOREIGN KEY (`tipo_cita_id`) REFERENCES `tipos_cita` (`id`),
  ADD CONSTRAINT `citas_ibfk_2` FOREIGN KEY (`medico_id`) REFERENCES `medicos` (`id`),
  ADD CONSTRAINT `citas_ibfk_3` FOREIGN KEY (`mascota_id`) REFERENCES `mascotas` (`id`),
  ADD CONSTRAINT `citas_ibfk_4` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`);

--
-- Filtros para la tabla `mascotas`
--
ALTER TABLE `mascotas`
  ADD CONSTRAINT `mascotas_ibfk_1` FOREIGN KEY (`especie_id`) REFERENCES `especies` (`id`),
  ADD CONSTRAINT `mascotas_ibfk_2` FOREIGN KEY (`raza_id`) REFERENCES `razas` (`id`),
  ADD CONSTRAINT `mascotas_ibfk_3` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `medicos_especialidades`
--
ALTER TABLE `medicos_especialidades`
  ADD CONSTRAINT `medicos_especialidades_ibfk_1` FOREIGN KEY (`medico_id`) REFERENCES `medicos` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `medicos_especialidades_ibfk_2` FOREIGN KEY (`especialidad_id`) REFERENCES `especialidades` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `razas`
--
ALTER TABLE `razas`
  ADD CONSTRAINT `razas_ibfk_1` FOREIGN KEY (`especie_id`) REFERENCES `especies` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `roles_permisos`
--
ALTER TABLE `roles_permisos`
  ADD CONSTRAINT `roles_permisos_ibfk_1` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `roles_permisos_ibfk_2` FOREIGN KEY (`permiso_id`) REFERENCES `permisos` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`) ON DELETE SET NULL;

--
-- Filtros para la tabla `usuarios_roles`
--
ALTER TABLE `usuarios_roles`
  ADD CONSTRAINT `usuarios_roles_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `usuarios_roles_ibfk_2` FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
