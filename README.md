# GestionVeterinaria 🐾

Sistema web empresarial para la administración integral de clínicas veterinarias.

---

## Descripción

**GestionVeterinaria**  es una SPA (Single Page Application) desarrollada con framework Vue.js en frontend con datos estáticos (mock data), diseñada para digitalizar y optimizar la gestión de una clínica veterinaria. La plataforma permite centralizar procesos administrativos y clínicos mediante módulos para gestionar clientes, médicos, mascotas, citas y agenda médica desde una interfaz moderna, modular y escalable.
**La propuesta técnica se basa en tres pilares:**
Arquitectura modular por dominio. Cada módulo del negocio (autenticación, personas, mascotas, citas) tiene su propia carpeta de vistas, componentes, store y tipos. Esto permite que el equipo trabaje en paralelo sin conflictos y que cada módulo sea reemplazable de forma independiente cuando se conecte el backend real.

---

## Problema que resuelve

Las clínicas veterinarias medianas y pequeñas suelen operar con registros en papel, hojas de cálculo dispersas o sistemas desconectados entre sí. Esto genera:

- Pérdida de información clínica de pacientes.
- Dificultad para coordinar la agenda entre médicos.
- Procesos administrativos lentos y propensos a errores.

GestionVeterinaria resuelve estos problemas ofreciendo una plataforma centralizada que conecta todas las áreas de la clínica en tiempo real.

---

## Público objetivo

- Clínicas veterinarias pequeñas y medianas.
- Veterinarios independientes con personal de apoyo.
- Administradores y recepcionistas que gestionan citas y clientes.
- Auxiliares veterinarios que requieren acceso controlado a la información clínica.

---

## Framework principal

| Capa | Framework |
|------|-----------|
| Frontend | **Vue.js 3** (Composition API + `<script setup>`) |
| Backend *(futuro)* | Por definir |

Vue.js 3 fue seleccionado desde el inicio del proyecto por su curva de aprendizaje accesible, su ecosistema maduro (Pinia, Vue Router), su excelente integración con TypeScript y su rendimiento en aplicaciones SPA de escala empresarial.

---

## Tecnologías utilizadas

### Frontend

| Tecnología | Versión | Rol |
|---|---|---|
| [Vue.js](https://vuejs.org/) | 3.5 | Framework principal de UI |
| [TypeScript](https://www.typescriptlang.org/) | 6.0 | Tipado estático |
| [Vite](https://vite.dev/) | 8.0 | Bundler y servidor de desarrollo |
| [Vue Router](https://router.vuejs.org/) | 5.0 | Enrutamiento SPA |
| [Pinia](https://pinia.vuejs.org/) | 3.0 | Gestión de estado global |
| [Tailwind CSS](https://tailwindcss.com/) | 4.2 | Estilos utilitarios |
| [ESLint](https://eslint.org/) + [Oxlint](https://oxc.rs/docs/guide/usage/linter) | 10 / 1.6 | Linting de código |
| [Prettier](https://prettier.io/) | 3.8 | Formateo de código |
| [vue-tsc](https://github.com/vuejs/language-tools) | 3.2 | Verificación de tipos en `.vue` |

### Herramientas de desarrollo

- **Node.js** `>=20.19.0`
- **npm** (gestor de paquetes)
- **VS Code** con extensión [Vue - Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar)

---

## Instalación y ejecución

### Requisitos previos

- Node.js `v20.19.0` o superior
- npm `v10` o superior

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd gestionveterinaria
```

### 2. Instalar dependencias

```bash
cd frontend
npm install
```

### 3. Ejecutar en modo desarrollo

```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`

### 4. Verificar tipos TypeScript

```bash
npm run type-check
```

### 5. Ejecutar linter

```bash
npm run lint
```

### 6. Compilar para producción

```bash
npm run build
```

### 7. Previsualizar build de producción

```bash
npm run preview
```

### Usuarios de prueba

Una vez en la pantalla de login, puedes usar cualquiera de estos usuarios de demostración:

| Usuario | Contraseña | Rol | Accesos |
|---|---|---|---|
| `admin` | `admin123` | Administrador | Acceso total |
| `dra.garcia` | `vet123` | Veterinario | Mascotas, citas, clientes (lectura) |
| `recepcion` | `rec123` | Recepcionista | Clientes, mascotas, citas |

---

## Estructura del proyecto

```
gestionveterinaria/
├── backend/                        # Backend (en desarrollo)
└── frontend/                       # Aplicación Vue.js
    ├── public/
    │   └── favicon.ico
    ├── src/
    │   ├── components/             # Componentes reutilizables
    │   │   ├── ui/                 # Componentes base del sistema de diseño
    │   │   │   ├── AppAlert.vue
    │   │   │   ├── AppBadge.vue
    │   │   │   ├── AppButton.vue
    │   │   │   ├── AppCard.vue
    │   │   │   ├── AppConfirmDialog.vue
    │   │   │   ├── AppInput.vue
    │   │   │   ├── AppModal.vue
    │   │   │   ├── AppSearchInput.vue
    │   │   │   ├── AppSelect.vue
    │   │   │   ├── AppTable.vue
    │   │   │   └── AppTextarea.vue
    │   │   ├── citas/              # Componentes del módulo de citas
    │   │   │   ├── AgendaSidebar.vue
    │   │   │   ├── CitaCalendar.vue
    │   │   │   ├── CitaForm.vue
    │   │   │   └── CitaStatusBadge.vue
    │   │   ├── clientes/           # Componentes del módulo de clientes
    │   │   │   ├── ClienteForm.vue
    │   │   │   └── ClienteTable.vue
    │   │   ├── mascotas/           # Componentes del módulo de mascotas
    │   │   │   ├── MascotaAvatar.vue
    │   │   │   ├── MascotaCard.vue
    │   │   │   ├── MascotaForm.vue
    │   │   │   └── MascotaTable.vue
    │   │   └── medicos/            # Componentes del módulo de médicos
    │   │       ├── EspecialidadSelect.vue
    │   │       ├── MedicoCard.vue
    │   │       └── MedicoForm.vue
    │   ├── data/                   # Datos estáticos de prueba (mock data)
    │   │   ├── auth.data.ts
    │   │   ├── citas.data.ts
    │   │   ├── mascotas.data.ts
    │   │   ├── personas.data.ts
    │   │   └── index.ts
    │   ├── layouts/                # Layouts de página
    │   │   ├── AuthLayout.vue      # Layout para pantallas de autenticación
    │   │   └── DashboardLayout.vue # Layout principal con sidebar
    │   ├── router/
    │   │   └── index.ts            # Rutas y navigation guards
    │   ├── services/               # Lógica de acceso a datos
    │   │   └── auth.service.ts
    │   ├── stores/                 # Estado global con Pinia
    │   │   ├── auth.store.ts
    │   │   ├── citas.store.ts
    │   │   ├── clientes.store.ts
    │   │   ├── mascotas.store.ts
    │   │   └── medicos.store.ts
    │   ├── types/                  # Interfaces y tipos TypeScript
    │   │   ├── auth.types.ts
    │   │   ├── cita.types.ts
    │   │   ├── mascota.types.ts
    │   │   ├── persona.types.ts
    │   │   └── index.ts
    │   ├── views/                  # Vistas / páginas
    │   │   ├── auth/
    │   │   │   ├── LoginView.vue
    │   │   │   └── UnauthorizedView.vue
    │   │   ├── citas/
    │   │   │   ├── AgendaView.vue
    │   │   │   └── CitasView.vue
    │   │   ├── clientes/
    │   │   │   └── ClientesView.vue
    │   │   ├── mascotas/
    │   │   │   └── MascotasView.vue
    │   │   ├── medicos/
    │   │   │   └── MedicosView.vue
    │   │   └── DashboardView.vue
    │   ├── App.vue
    │   ├── main.ts
    │   └── style.css
    ├── .vscode/
    │   └── settings.json
    ├── index.html
    ├── package.json
    ├── tsconfig.json
    └── vite.config.ts
```

---

## Módulos implementados — Fase 1

### 1. Autenticación y Seguridad
Control de acceso con login, logout, sesión persistente en `localStorage`, protección de rutas por rol y por permiso granular.

### 2. Personas
- **Clientes:** registro, edición, listado con búsqueda y filtros, activar/inactivar.
- **Médicos:** registro, edición, asignación de especialidades, control de disponibilidad.

### 3. Mascotas
Registro y edición de pacientes veterinarios con especie, raza, propietario, datos clínicos básicos. Vista tabla y vista tarjetas.

### 4. Citas
Creación, edición y control de estado de citas. Vista de lista con filtros y vista de calendario mensual con agenda del día seleccionado.

---

## Paleta de colores

| Rol | Color | Hex |
|---|---|---|
| Principal | Verde esmeralda | `#059669` |
| Secundario | Teal | `#0d9488` |
| Alerta / Acento | Ámbar | `#f59e0b` |
| Peligro | Coral / Rosa | `#f43f5e` |

---

## Integrantes y asignatura

| Nombre | Rol en el proyecto |
|---|---|
| *Romel Yugcha* | *Desarrollador* |


**Asignatura:** *Aplicaciones de Tecnologias Web*
**Institución:** *ESPE*
**Período académico:** *2026*

---

