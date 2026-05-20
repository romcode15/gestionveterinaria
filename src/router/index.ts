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

    // Dashboard
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true },
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

  // Ruta solo para invitados (login)
  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    return next({ name: 'dashboard' })
  }

  // Verificar permiso específico
  if (to.meta.permiso && !authStore.hasPermiso(to.meta.permiso as string)) {
    return next({ name: 'unauthorized' })
  }

  next()
})

export default router
