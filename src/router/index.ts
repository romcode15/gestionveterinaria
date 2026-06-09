import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Redireccion raíz
    {
      path: '/',
      redirect: '/dashboard',
    },

    // Auth
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

    // Dashboard admin/staff
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true, rolesExcluidos: ['cliente'] },
    },

    // Portal cliente
    {
      path: '/mi-portal',
      name: 'cliente-dashboard',
      component: () => import('@/views/cliente/ClienteDashboardView.vue'),
      meta: { requiresAuth: true, permiso: 'cliente.mis-mascotas' },
    },
    {
      path: '/mis-citas',
      name: 'mis-citas',
      component: () => import('@/views/cliente/MisCitasView.vue'),
      meta: { requiresAuth: true, permiso: 'cliente.mis-citas' },
    },

    // Módulo Personas - Clientes
    {
      path: '/clientes',
      name: 'clientes',
      component: () => import('@/views/clientes/ClientesView.vue'),
      meta: { requiresAuth: true, permiso: 'clientes.ver' },
    },

    // Módulo Personas - Médicos
    {
      path: '/medicos',
      name: 'medicos',
      component: () => import('@/views/medicos/MedicosView.vue'),
      meta: { requiresAuth: true, permiso: 'medicos.ver' },
    },

    // Módulo Mascotas
    {
      path: '/mascotas',
      name: 'mascotas',
      component: () => import('@/views/mascotas/MascotasView.vue'),
      meta: { requiresAuth: true, permiso: 'mascotas.ver' },
    },

    // Módulo Citas
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

    // 404
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard',
    },
  ],
})

// Navigation guard
router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()

  // Inicializar sesión desde localStorage si no está cargada
  if (!authStore.isAuthenticated) {
    authStore.initFromStorage()
  }

  // Ruta que requiere estar autenticado
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return next({ name: 'login', query: { redirect: to.fullPath } })
  }

  // Ruta solo para invitados (login) → redirigir al portal correcto según rol
  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    if (authStore.isCliente) {
      return next({ name: 'cliente-dashboard' })
    }
    return next({ name: 'dashboard' })
  }

  // Un cliente que intenta entrar al /dashboard general → redirigir a su portal
  if (to.name === 'dashboard' && authStore.isAuthenticated && authStore.isCliente) {
    return next({ name: 'cliente-dashboard' })
  }

  // Rutas excluidas para ciertos roles
  if (to.meta.rolesExcluidos && authStore.isAuthenticated) {
    const excluidos = to.meta.rolesExcluidos as string[]
    if (authStore.roles.some((r) => excluidos.includes(r))) {
      return next({ name: 'unauthorized' })
    }
  }

  // Verificar permiso específico
  if (to.meta.permiso && !authStore.hasPermiso(to.meta.permiso as string)) {
    return next({ name: 'unauthorized' })
  }

  next()
})

export default router
