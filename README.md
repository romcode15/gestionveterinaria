# GestionVeterinaria 🐾

Sistema web empresarial para la administración integral de clínicas veterinarias.

---

## 1. Descripción del proyecto

**GestionVeterinaria** es una aplicación web moderna, modular y escalable desarrollada con Vue.js 3, orientada a centralizar y automatizar los procesos clínicos y administrativos de una clínica veterinaria. La plataforma permite gestionar el acceso de usuarios con roles diferenciados, el registro de clientes y sus mascotas, la agenda de citas médicas y la disponibilidad del equipo veterinario, todo desde una única interfaz responsiva con soporte para modo oscuro.

El sistema está construido en su primera fase como un prototipo funcional de frontend con datos en memoria, estructurado para escalar hacia una arquitectura cliente-servidor completa en fases posteriores.

### Problema que resuelve

Las clínicas veterinarias medianas y pequeñas suelen operar con registros en papel, hojas de cálculo dispersas o sistemas desconectados entre sí. Esto genera pérdida de información clínica, dificultad para coordinar la agenda entre médicos y procesos administrativos lentos y propensos a errores. GestionVeterinaria resuelve estos problemas ofreciendo una plataforma centralizada que conecta todas las áreas de la clínica.

---

## 2. Características principales

- **Autenticación completa** con login, logout y sesión persistente en `localStorage`.
- **Control de acceso basado en roles y permisos (RBAC)** con 4 roles y 15 permisos granulares.
- **Portal diferenciado por rol**: staff ve el dashboard administrativo; clientes acceden a su portal personal con solo sus mascotas y citas.
- **Módulo de clientes**: CRUD completo con búsqueda de texto libre y filtro por estado.
- **Módulo de mascotas**: CRUD con vista dual (tabla / tarjetas), filtro por especie y búsqueda por nombre y propietario.
- **Módulo de médicos**: CRUD con asignación múltiple de especialidades y control de disponibilidad.
- **Módulo de citas**: creación, edición y cambio de estado inline con filtros por médico y estado.
- **Dashboard administrativo** con estadísticas calculadas en tiempo real y listado de citas del día.
- **Portal del cliente** con resumen de sus mascotas y próximas citas.
- **Modo oscuro / claro** persistente basado en variables CSS semánticas, con detección automática de preferencia del sistema operativo.
- **Diseño responsivo** mobile-first adaptado a móvil, tablet y escritorio.
- **Creación automática de usuario** al registrar un nuevo cliente.

---

## 3. Tecnologías utilizadas

### Frontend

| Tecnología | Versión | Rol |
|---|---|---|
| [Vue.js](https://vuejs.org/) | 3.5 | Framework principal de UI (Composition API + `<script setup>`) |
| [TypeScript](https://www.typescriptlang.org/) | 5.x | Tipado estático en toda la aplicación |
| [Vite](https://vite.dev/) | 6.x | Bundler y servidor de desarrollo |
| [Vue Router](https://router.vuejs.org/) | 5.0 | Enrutamiento SPA con navigation guards |
| [Pinia](https://pinia.vuejs.org/) | 3.0 | Gestión de estado global reactivo |
| [Tailwind CSS](https://tailwindcss.com/) | 4.2 | Estilos utilitarios con tema personalizado |
| [ESLint](https://eslint.org/) + [Oxlint](https://oxc.rs/docs/guide/usage/linter) | 10 / 1.6 | Análisis estático de código |
| [Prettier](https://prettier.io/) | 3.x | Formateo automático de código |
| [vue-tsc](https://github.com/vuejs/language-tools) | 3.x | Verificación de tipos en archivos `.vue` |

### Herramientas de desarrollo

- **Node.js** `>= 20.19.0`
- **npm** `>= 10`
- **VS Code** con extensión [Vue - Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar)

### Backend

> El directorio `/backend` existe en el repositorio pero está pendiente de implementación. En la Fase 1 todos los datos son gestionados en memoria mediante archivos JSON y servicios mock en el frontend.

---

## 4. Estructura del proyecto

```
gestionveterinaria/
├── backend/                        # Backend (en desarrollo)
└── frontend/                       # Aplicación Vue.js
    ├── public/
    │   └── favicon.ico
    ├── src/
    │   ├── components/             # Componentes reutilizables
    │   │   ├── ui/                 # Sistema de diseño — componentes base
    │   │   │   ├── AppAlert.vue        # Alertas de éxito, error, advertencia, info
    │   │   │   ├── AppBadge.vue        # Etiquetas de estado con variantes de color
    │   │   │   ├── AppButton.vue       # Botón con variantes y estado de carga
    │   │   │   ├── AppCard.vue         # Contenedor de superficie con sombra
    │   │   │   ├── AppConfirmDialog.vue# Diálogo de confirmación reutilizable
    │   │   │   ├── AppInput.vue        # Campo de texto con label y error
    │   │   │   ├── AppModal.vue        # Modal accesible con Teleport
    │   │   │   ├── AppSearchInput.vue  # Input de búsqueda con ícono
    │   │   │   ├── AppSelect.vue       # Select con label y validación
    │   │   │   ├── AppTable.vue        # Tabla genérica con skeleton loader
    │   │   │   └── AppTextarea.vue     # Área de texto con label y error
    │   │   ├── citas/
    │   │   │   ├── AgendaSidebar.vue   # Panel lateral de citas por día
    │   │   │   ├── CitaCalendar.vue    # Componente de calendario (preparado)
    │   │   │   ├── CitaForm.vue        # Formulario de creación/edición de cita
    │   │   │   └── CitaStatusBadge.vue # Badge de estado de cita
    │   │   ├── clientes/
    │   │   │   ├── ClienteForm.vue     # Formulario de creación/edición de cliente
    │   │   │   └── ClienteTable.vue    # Tabla de clientes con acciones
    │   │   ├── mascotas/
    │   │   │   ├── MascotaAvatar.vue   # Avatar con emoji según especie
    │   │   │   ├── MascotaCard.vue     # Tarjeta de mascota para vista grid
    │   │   │   ├── MascotaForm.vue     # Formulario de creación/edición de mascota
    │   │   │   └── MascotaTable.vue    # Tabla de mascotas con acciones
    │   │   └── medicos/
    │   │       ├── EspecialidadSelect.vue # Selector múltiple de especialidades
    │   │       ├── MedicoCard.vue         # Tarjeta de médico con contacto
    │   │       └── MedicoForm.vue         # Formulario de creación/edición de médico
    │   ├── data/
    │   │   └── json/               # Archivos JSON — fuente de datos mock
    │   │       ├── usuarios.json
    │   │       ├── roles.json
    │   │       ├── permisos.json
    │   │       ├── clientes.json
    │   │       ├── mascotas.json
    │   │       ├── medicos.json
    │   │       ├── citas.json
    │   │       ├── especialidades.json
    │   │       ├── especies.json
    │   │       ├── razas.json
    │   │       └── tipos-cita.json
    │   ├── layouts/
    │   │   ├── AuthLayout.vue      # Layout para pantallas de autenticación
    │   │   └── DashboardLayout.vue # Layout principal con sidebar y toggle de tema
    │   ├── router/
    │   │   └── index.ts            # Rutas y navigation guards (RBAC)
    │   ├── services/               # Capa de acceso a datos (mock de API REST)
    │   │   ├── auth.service.ts
    │   │   ├── citas.service.ts
    │   │   ├── clientes.service.ts
    │   │   ├── mascotas.service.ts
    │   │   └── medicos.service.ts
    │   ├── stores/                 # Estado global con Pinia
    │   │   ├── auth.store.ts
    │   │   ├── citas.store.ts
    │   │   ├── clientes.store.ts
    │   │   ├── mascotas.store.ts
    │   │   ├── medicos.store.ts
    │   │   └── theme.store.ts
    │   ├── types/                  # Interfaces y tipos TypeScript
    │   │   ├── auth.types.ts       # Usuario, Rol, Permiso, LoginCredentials
    │   │   ├── cita.types.ts       # Cita, CitaFormData, EstadoCita, TipoCita
    │   │   ├── mascota.types.ts    # Mascota, MascotaFormData, Especie, Raza
    │   │   ├── persona.types.ts    # Cliente, Medico, ClienteFormData, MedicoFormData
    │   │   └── index.ts            # Re-exportaciones centralizadas
    │   ├── views/
    │   │   ├── auth/
    │   │   │   ├── LoginView.vue
    │   │   │   └── UnauthorizedView.vue
    │   │   ├── citas/
    │   │   │   ├── AgendaView.vue
    │   │   │   └── CitasView.vue
    │   │   ├── cliente/            # Portal exclusivo del rol cliente
    │   │   │   ├── ClienteDashboardView.vue
    │   │   │   └── MisCitasView.vue
    │   │   ├── clientes/
    │   │   │   └── ClientesView.vue
    │   │   ├── mascotas/
    │   │   │   └── MascotasView.vue
    │   │   ├── medicos/
    │   │   │   └── MedicosView.vue
    │   │   └── DashboardView.vue
    │   ├── App.vue
    │   ├── main.ts
    │   └── style.css               # Variables CSS semánticas + dark mode
    ├── index.html
    ├── package.json
    ├── tsconfig.json
    └── vite.config.ts
```

---

## 5. Requisitos previos

Antes de instalar el proyecto asegúrate de tener instalado:

- **Node.js** `v20.19.0` o superior — [descargar](https://nodejs.org/)
- **npm** `v10` o superior (incluido con Node.js)
- **Git** — [descargar](https://git-scm.com/)
- **VS Code** (recomendado) con la extensión [Vue - Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar)

Verificar versiones instaladas:

```bash
node --version   # debe mostrar v20.x.x o superior
npm --version    # debe mostrar 10.x.x o superior
```

---

## 6. Instalación

```bash
# 1. Clonar el repositorio
git clone <url-del-repositorio>
cd gestionveterinaria

# 2. Ingresar al directorio del frontend
cd frontend

# 3. Instalar dependencias
npm install
```

---

## 7. Ejecución en modo desarrollo

```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`

Comandos adicionales de desarrollo:

```bash
# Verificar tipos TypeScript
npm run type-check

# Ejecutar linter (oxlint + eslint) con corrección automática
npm run lint

# Formatear código con Prettier
npm run format
```

---

## 8. Compilación para producción

```bash
npm run build
```

Este comando ejecuta en paralelo la verificación de tipos con `vue-tsc` y la compilación con Vite. El resultado se genera en el directorio `frontend/dist/`.

---

## 9. Vista previa de producción

```bash
npm run preview
```

Levanta un servidor estático local que sirve el contenido compilado del directorio `dist/`, simulando el comportamiento en un servidor de producción real. Disponible en `http://localhost:4173`.

---

## 10. Usuarios de prueba

En la pantalla de login están disponibles los siguientes usuarios de demostración. También puedes usar los botones de acceso rápido que aparecen en la parte inferior del formulario de login:

| Usuario | Contraseña | Rol | Acceso |
|---|---|---|---|
| `admin` | `admin123` | Administrador | Acceso total al sistema |
| `dra.garcia` | `vet123` | Veterinario | Clientes (lectura), mascotas, citas |
| `recepcion` | `rec123` | Recepcionista | Clientes, mascotas, citas (crear/cancelar) |
| `ana.martinez` | `cli123` | Cliente | Portal personal — sus mascotas y citas |
| `pedro.rodriguez` | `cli123` | Cliente | Portal personal — sus mascotas y citas |
| `sofia.hernandez` | `cli123` | Cliente | Portal personal — sus mascotas y citas |

> Los usuarios con rol **cliente** son redirigidos automáticamente a `/mi-portal` y no tienen acceso al dashboard administrativo ni a los datos de otros usuarios.

---

## 11. Archivos JSON utilizados

Todos los datos del sistema se encuentran en `src/data/json/`. Estos archivos son importados como módulos ES estáticos por los servicios correspondientes y copiados en memoria al iniciar cada servicio.

| Archivo | Contenido | Registros |
|---|---|---|
| `usuarios.json` | Usuarios del sistema con credenciales y asignación de roles | 7 |
| `roles.json` | Definición de los 4 roles con sus `permisosIds` asociados | 4 |
| `permisos.json` | Catálogo de 15 permisos granulares organizados por módulo | 15 |
| `clientes.json` | Clientes registrados de la clínica | 5 |
| `mascotas.json` | Mascotas con referencias a especie, raza y propietario | 7 |
| `medicos.json` | Médicos veterinarios con especialidades y disponibilidad | 3 |
| `citas.json` | Citas con estado, médico, mascota, cliente y tipo de cita | 8 |
| `especialidades.json` | Catálogo de especialidades veterinarias | 8 |
| `especies.json` | Especies animales (Perro, Gato, Ave) | 3 |
| `razas.json` | Razas organizadas por `especieId` | 13 |
| `tipos-cita.json` | Tipos de cita con duración en minutos y color de identificación | 7 |

> **Nota técnica:** Los cambios realizados en tiempo de ejecución (crear, editar registros) se almacenan en el array `db` de cada servicio y se pierden al recargar la página. La sesión de autenticación sí persiste en `localStorage` entre recargas.

---

## 12. Funcionalidades implementadas

### Autenticación
- Login con usuario y contraseña validados contra `usuarios.json`
- Logout con limpieza de `localStorage`
- Sesión persistente: recuperación del token y usuario al recargar
- Redirección automática según rol al iniciar sesión

### Dashboard administrativo
- Estadísticas en tiempo real: clientes activos, mascotas, citas del día, médicos disponibles
- Listado de citas programadas para hoy ordenadas por hora
- Resumen de estados de citas
- Accesos rápidos a crear cliente, mascota y cita

### Portal del cliente
- Vista personalizada con saludo y resumen de sus propias mascotas
- Listado de próximas citas (pendientes y confirmadas)
- Historial completo de citas en `/mis-citas`

### Módulo de clientes
- Listado con búsqueda por nombre, apellido, email, documento y teléfono
- Filtro por estado (todos / activos / inactivos)
- Crear cliente con validación de campos obligatorios y formato de email
- Editar cliente existente (formulario hidratado automáticamente)
- Activar / inactivar cliente con confirmación visual

### Módulo de mascotas
- Listado en vista tabla o vista tarjetas (toggle)
- Búsqueda por nombre, propietario, especie y raza
- Filtro por especie (catálogo dinámico)
- Selección de raza en cascada dependiente de la especie seleccionada
- Crear y editar mascota con datos clínicos completos

### Módulo de médicos
- Listado en vista tarjetas con especialidades y datos de contacto
- Búsqueda por nombre, apellido, número de licencia y especialidad
- Estadísticas: total, disponibles, no disponibles, número de especialidades
- Selector múltiple de especialidades mediante chips interactivos
- Control de disponibilidad mediante toggle switch

### Módulo de citas
- Listado con filtro combinado por médico y por estado
- Estadísticas de citas: total, pendientes, confirmadas, completadas, canceladas
- Crear cita con selección de fecha, hora, tipo de cita, médico y mascota
- Cálculo automático de hora de fin según duración del tipo de cita
- Cambio de estado inline desde la lista (sin abrir modal)
- Edición completa mediante modal

### Modo oscuro
- Toggle disponible en el sidebar (expandido e icónico)
- Persistencia de preferencia en `localStorage` bajo la clave `vg-theme`
- Detección automática de preferencia del sistema operativo al primer acceso

---

## 13. Estructura de roles y permisos

El sistema implementa un modelo RBAC (Role-Based Access Control) con 4 roles y 15 permisos granulares.

### Roles

| ID | Rol | Descripción |
|---|---|---|
| 1 | `admin` | Acceso total al sistema |
| 2 | `veterinario` | Acceso a datos clínicos y citas |
| 3 | `recepcionista` | Gestión administrativa de clientes, mascotas y citas |
| 4 | `cliente` | Acceso exclusivo a su propio portal |

### Permisos por módulo

| ID | Permiso | Módulo |
|---|---|---|
| 1 | `clientes.ver` | personas |
| 2 | `clientes.crear` | personas |
| 3 | `clientes.editar` | personas |
| 4 | `medicos.ver` | personas |
| 5 | `medicos.crear` | personas |
| 6 | `mascotas.ver` | mascotas |
| 7 | `mascotas.crear` | mascotas |
| 8 | `mascotas.editar` | mascotas |
| 9 | `citas.ver` | citas |
| 10 | `citas.crear` | citas |
| 11 | `citas.editar` | citas |
| 12 | `citas.cancelar` | citas |
| 13 | `admin.usuarios` | admin |
| 14 | `cliente.mis-mascotas` | cliente |
| 15 | `cliente.mis-citas` | cliente |

### Permisos asignados por rol

| Rol | Permisos |
|---|---|
| `admin` | 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 |
| `veterinario` | 1, 6, 8, 9, 11 |
| `recepcionista` | 1, 2, 3, 6, 7, 9, 10, 12 |
| `cliente` | 14, 15 |

### Rutas protegidas

| Ruta | Protección |
|---|---|
| `/dashboard` | `requiresAuth` + `rolesExcluidos: ['cliente']` |
| `/mi-portal` | `requiresAuth` + `permiso: 'cliente.mis-mascotas'` |
| `/mis-citas` | `requiresAuth` + `permiso: 'cliente.mis-citas'` |
| `/clientes` | `requiresAuth` + `permiso: 'clientes.ver'` |
| `/medicos` | `requiresAuth` + `permiso: 'medicos.ver'` |
| `/mascotas` | `requiresAuth` + `permiso: 'mascotas.ver'` |
| `/citas` | `requiresAuth` + `permiso: 'citas.ver'` |
| `/agenda` | `requiresAuth` + `permiso: 'citas.ver'` |

---

## 14. Autor

| Campo | Detalle |
|---|---|
| **Integrante(s)** | *Romel Yugcha* |


---

## 15. Licencia

Este proyecto fue desarrollado con fines académicos. Todos los datos incluidos son ficticios y generados para propósitos de demostración. No se permite su uso comercial sin autorización expresa de los autores.

---

