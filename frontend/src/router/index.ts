import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // ── Raíz ──────────────────────────────────────────────────────────────
    {
      path: '/',
      redirect: '/dashboard',
    },

    // ── Auth ───────────────────────────────────────────────────────────────
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { requiresGuest: true },
    },
    {
      path: '/unauthorized',
      name: 'unauthorized',
      component: () => import('@/views/auth/UnauthorizedView.vue'),
    },

    // ── Dashboard admin/staff (con datos reales del backend) ───────────────
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true, rolesExcluidos: ['cliente'] },
    },

    // ── Portal Cliente ─────────────────────────────────────────────────────
    {
      path: '/mi-portal',
      name: 'cliente-dashboard',
      component: () => import('@/views/cliente/ClienteDashboardView.vue'),
      meta: { requiresAuth: true, permiso: 'cliente.mis_mascotas' },
    },
    {
      path: '/mis-citas',
      name: 'mis-citas',
      component: () => import('@/views/cliente/MisCitasView.vue'),
      meta: { requiresAuth: true, permiso: 'cliente.mis_citas' },
    },
    {
      path: '/mis-mascotas/:id/historial',
      name: 'mi-historial-clinico',
      component: () => import('@/views/cliente/MiHistorialClinicoView.vue'),
      meta: { requiresAuth: true, permiso: 'cliente.mis_mascotas' },
    },

    // ── Portal Médico ──────────────────────────────────────────────────────
    {
      path: '/mi-agenda',
      name: 'medico-agenda',
      component: () => import('@/views/medico/MiAgendaView.vue'),
      meta: { requiresAuth: true, roles: ['veterinario'] },
    },
    {
      path: '/mis-diagnosticos',
      name: 'medico-diagnosticos',
      component: () => import('@/views/medico/MisDiagnosticosView.vue'),
      meta: { requiresAuth: true, roles: ['veterinario'] },
    },

    // ── Módulo Clientes ────────────────────────────────────────────────────
    {
      path: '/clientes',
      name: 'clientes',
      component: () => import('@/views/clientes/ClientesView.vue'),
      meta: { requiresAuth: true, permiso: 'clientes.ver' },
    },

    // ── Módulo Médicos ─────────────────────────────────────────────────────
    {
      path: '/medicos',
      name: 'medicos',
      component: () => import('@/views/medicos/MedicosView.vue'),
      meta: { requiresAuth: true, permiso: 'medicos.ver' },
    },

    // ── Módulo Mascotas ────────────────────────────────────────────────────
    {
      path: '/mascotas',
      name: 'mascotas',
      component: () => import('@/views/mascotas/MascotasView.vue'),
      meta: { requiresAuth: true, permiso: 'mascotas.ver' },
    },

    // ── Módulo Citas ───────────────────────────────────────────────────────
    {
      path: '/citas',
      name: 'citas',
      component: () => import('@/views/citas/CitasView.vue'),
      meta: { requiresAuth: true, permiso: 'citas.ver' },
    },
    {
      path: '/agenda',
      name: 'agenda',
      component: () => import('@/views/citas/AgendaView.vue'),
      meta: { requiresAuth: true, permiso: 'citas.ver' },
    },

    // ── Módulo Clínico (Fase 1) ────────────────────────────────────────────
    {
      path: '/diagnosticos',
      name: 'diagnosticos',
      component: () => import('@/views/clinico/DiagnosticosView.vue'),
      meta: { requiresAuth: true, roles: ['admin', 'veterinario', 'recepcionista'] },
    },
    {
      path: '/diagnosticos/:id/tratamiento',
      name: 'tratamiento',
      component: () => import('@/views/clinico/TratamientoView.vue'),
      meta: { requiresAuth: true, roles: ['admin', 'veterinario'] },
    },
    {
      path: '/mascotas/:id/historial',
      name: 'historial-clinico',
      component: () => import('@/views/clinico/HistorialClinicoView.vue'),
      meta: { requiresAuth: true, roles: ['admin', 'veterinario', 'recepcionista'] },
    },

    // ── Módulo Vacunación (Fase 2) ─────────────────────────────────────────
    {
      path: '/vacunacion',
      name: 'vacunacion',
      component: () => import('@/views/vacunacion/VacunacionView.vue'),
      meta: { requiresAuth: true, roles: ['admin', 'veterinario', 'recepcionista'] },
    },
    {
      path: '/vacunas',
      name: 'vacunas',
      component: () => import('@/views/vacunacion/VacunasView.vue'),
      meta: { requiresAuth: true, roles: ['admin', 'veterinario'] },
    },

    // ── Módulo Inventario (Fase 3) ─────────────────────────────────────────
    {
      path: '/inventario',
      name: 'inventario',
      component: () => import('@/views/inventario/InventarioView.vue'),
      meta: { requiresAuth: true, roles: ['admin', 'veterinario', 'recepcionista'] },
    },
    {
      path: '/inventario/productos',
      name: 'productos',
      component: () => import('@/views/inventario/ProductosView.vue'),
      meta: { requiresAuth: true, roles: ['admin', 'veterinario', 'recepcionista'] },
    },
    {
      path: '/inventario/proveedores',
      name: 'proveedores',
      component: () => import('@/views/inventario/ProveedoresView.vue'),
      meta: { requiresAuth: true, roles: ['admin', 'recepcionista'] },
    },

    // ── Auditoría (Fase 5) — solo ADMIN ───────────────────────────────────
    {
      path: '/auditoria',
      name: 'auditoria',
      component: () => import('@/views/auditoria/AuditoriaView.vue'),
      meta: { requiresAuth: true, roles: ['admin'] },
    },

    // ── Recepcionistas — solo ADMIN ────────────────────────────────────────
    {
      path: '/recepcionistas',
      name: 'recepcionistas',
      component: () => import('@/views/recepcionistas/RecepcionistasView.vue'),
      meta: { requiresAuth: true, roles: ['admin'] },
    },

    // ── Usuarios — solo ADMIN ──────────────────────────────────────────────
    {
      path: '/usuarios',
      name: 'usuarios',
      component: () => import('@/views/usuarios/UsuariosView.vue'),
      meta: { requiresAuth: true, roles: ['admin'] },
    },

    // ── Perfil (cualquier usuario autenticado) ─────────────────────────────
    {
      path: '/perfil',
      name: 'perfil',
      component: () => import('@/views/PerfilView.vue'),
      meta: { requiresAuth: true },
    },

    // ── 404 ────────────────────────────────────────────────────────────────
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard',
    },
  ],
})

// ── Navigation Guard ───────────────────────────────────────────────────────

router.beforeEach((to, _from) => {
  const authStore = useAuthStore()

  // Inicializar sesión desde localStorage si no está cargada
  if (!authStore.isAuthenticated) {
    authStore.initFromStorage()
  }

  // Ruta que requiere autenticación
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  // Ruta solo para invitados (login) → redirigir al portal correcto
  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    if (authStore.isCliente) return { name: 'cliente-dashboard' }
    if (authStore.isMedico)  return { name: 'medico-agenda' }
    return { name: 'dashboard' }
  }

  // Cliente intentando entrar al dashboard general → su portal
  if (to.name === 'dashboard' && authStore.isAuthenticated && authStore.isCliente) {
    return { name: 'cliente-dashboard' }
  }

  // Médico intentando entrar al dashboard general → su agenda
  if (to.name === 'dashboard' && authStore.isAuthenticated && authStore.isMedico) {
    return { name: 'medico-agenda' }
  }

  // Rutas excluidas para ciertos roles
  if (to.meta.rolesExcluidos && authStore.isAuthenticated) {
    const excluidos = to.meta.rolesExcluidos as string[]
    if (authStore.roles.some((r) => excluidos.includes(r))) {
      return { name: 'unauthorized' }
    }
  }

  // Rutas que requieren uno de los roles especificados
  if (to.meta.roles && authStore.isAuthenticated) {
    const rolesRequeridos = to.meta.roles as string[]
    const tieneRol = authStore.roles.some((r) => rolesRequeridos.includes(r))
    if (!tieneRol) return { name: 'unauthorized' }
  }

  // Verificar permiso específico
  if (to.meta.permiso && !authStore.hasPermiso(to.meta.permiso as string)) {
    return { name: 'unauthorized' }
  }

  // Permitir navegación
  return true
})

export default router
