# GestionVet — Backend API REST 🐾

API REST desarrollada con **Spring Boot 3** y **MySQL** para el Sistema de Gestión Veterinaria.

---

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.3.0 |
| Spring Data JPA | 3.3.0 |
| Spring Validation | 3.3.0 |
| MySQL Connector | 8.x |
| Springdoc OpenAPI (Swagger) | 2.5.0 |
| Maven | 3.x |

---

## Requisitos previos

- Java 17+
- Maven 3.8+
- MySQL 8.x corriendo en `localhost:3306`
- Base de datos `veterinaria_db` creada (ver sección de instalación)

---

## Instalación

### 1. Crear la base de datos

Importar el script SQL en phpMyAdmin o desde consola MySQL:

```bash
mysql -u root -p < database/veterinaria_db.sql
```

Esto crea:
- La base de datos `veterinaria_db`
- El usuario `admin_vet` con contraseña `Admin123!`
- Las 14 tablas con sus relaciones
- Todos los datos de prueba

### 2. Clonar e instalar dependencias

```bash
cd backend
mvn clean install
```

---

## Ejecución

```bash
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

---

## Documentación Swagger

Una vez levantada la aplicación, accede a:

```
http://localhost:8080/swagger-ui.html
```

---

## Endpoints disponibles

### Clientes `/api/clientes`
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/clientes` | Listar todos |
| GET | `/api/clientes/{id}` | Buscar por ID |
| GET | `/api/clientes/buscar?nombre=Ana` | Buscar por nombre |
| GET | `/api/clientes/estado/{estado}` | Filtrar por estado |
| POST | `/api/clientes` | Crear cliente |
| PUT | `/api/clientes/{id}` | Actualizar cliente |
| DELETE | `/api/clientes/{id}` | Eliminar cliente |

### Mascotas `/api/mascotas`
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/mascotas` | Listar todas |
| GET | `/api/mascotas/{id}` | Buscar por ID |
| GET | `/api/mascotas/cliente/{clienteId}` | Mascotas por cliente |
| GET | `/api/mascotas/buscar?nombre=Max` | Buscar por nombre |
| POST | `/api/mascotas` | Crear mascota |
| PUT | `/api/mascotas/{id}` | Actualizar mascota |
| DELETE | `/api/mascotas/{id}` | Eliminar mascota |

### Médicos `/api/medicos`
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/medicos` | Listar todos |
| GET | `/api/medicos/{id}` | Buscar por ID |
| GET | `/api/medicos/disponibles` | Solo disponibles |
| GET | `/api/medicos/buscar?nombre=Garcia` | Buscar por nombre |
| POST | `/api/medicos` | Crear médico |
| PUT | `/api/medicos/{id}` | Actualizar médico |
| DELETE | `/api/medicos/{id}` | Eliminar médico |

### Citas `/api/citas`
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/citas` | Listar todas |
| GET | `/api/citas/{id}` | Buscar por ID |
| GET | `/api/citas/fecha?fecha=2026-06-22` | Citas por fecha |
| GET | `/api/citas/cliente/{clienteId}` | Citas por cliente |
| GET | `/api/citas/medico/{medicoId}` | Citas por médico |
| GET | `/api/citas/estado/{estado}` | Citas por estado |
| POST | `/api/citas` | Crear cita |
| PUT | `/api/citas/{id}` | Actualizar cita |
| PATCH | `/api/citas/{id}/estado` | Cambiar estado |
| DELETE | `/api/citas/{id}` | Eliminar cita |

### Usuarios `/api/usuarios`
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/usuarios` | Listar todos |
| GET | `/api/usuarios/{id}` | Buscar por ID |
| POST | `/api/usuarios` | Crear usuario |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

### Catálogos `/api/catalogos`
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/catalogos/roles` | Listar roles |
| GET | `/api/catalogos/especies` | Listar especies |
| GET | `/api/catalogos/razas` | Listar razas |
| GET | `/api/catalogos/razas/especie/{id}` | Razas por especie |
| GET | `/api/catalogos/especialidades` | Listar especialidades |
| GET | `/api/catalogos/tipos-cita` | Listar tipos de cita |

---

## Estructura del proyecto

```
backend/
├── database/
│   └── veterinaria_db.sql          ← Script completo BD
├── src/main/java/com/gestionvet/veterinariaapi/
│   ├── VeterinariaApiApplication.java
│   ├── config/
│   │   ├── CorsConfig.java
│   │   └── SwaggerConfig.java
│   ├── controller/
│   │   ├── CitaController.java
│   │   ├── ClienteController.java
│   │   ├── MascotaController.java
│   │   ├── MedicoController.java
│   │   ├── UsuarioController.java
│   │   └── CatalogoController.java
│   ├── dto/
│   │   ├── CitaDTO.java
│   │   ├── ClienteDTO.java
│   │   ├── MascotaDTO.java
│   │   ├── MedicoDTO.java
│   │   └── UsuarioDTO.java
│   ├── entity/
│   │   ├── Cita.java
│   │   ├── Cliente.java
│   │   ├── Especialidad.java
│   │   ├── Especie.java
│   │   ├── Mascota.java
│   │   ├── Medico.java
│   │   ├── Permiso.java
│   │   ├── Raza.java
│   │   ├── Rol.java
│   │   ├── TipoCita.java
│   │   └── Usuario.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   ├── repository/
│   │   ├── CitaRepository.java
│   │   ├── ClienteRepository.java
│   │   ├── EspecialidadRepository.java
│   │   ├── EspecieRepository.java
│   │   ├── MascotaRepository.java
│   │   ├── MedicoRepository.java
│   │   ├── RazaRepository.java
│   │   ├── RolRepository.java
│   │   ├── TipoCitaRepository.java
│   │   └── UsuarioRepository.java
│   └── service/
│       ├── CatalogoService.java
│       ├── CitaService.java
│       ├── ClienteService.java
│       ├── MascotaService.java
│       ├── MedicoService.java
│       └── UsuarioService.java
└── src/main/resources/
    └── application.properties
```

---

## Códigos de respuesta HTTP

| Código | Significado |
|---|---|
| 200 | OK — consulta o actualización exitosa |
| 201 | Created — recurso creado correctamente |
| 204 | No Content — eliminación exitosa |
| 400 | Bad Request — validación fallida o datos inválidos |
| 404 | Not Found — recurso no encontrado |
| 500 | Internal Server Error — error inesperado |

---

> Proyecto académico — Fase 1 sin autenticación JWT. Los passwords se almacenan en texto plano intencionalmente para facilitar las pruebas con Swagger.
