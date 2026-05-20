<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import AppBadge from '@/components/ui/AppBadge.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const sidebarOpen = ref(true)

interface NavItem {
  label: string
  icon: string
  to: string
  permiso?: string
  badge?: number
}

const navItems: NavItem[] = [
  {
    label: 'Dashboard',
    icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6',
    to: '/dashboard',
  },
  {
    label: 'Clientes',
    icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z',
    to: '/clientes',
    permiso: 'clientes.ver',
  },
  {
    label: 'Médicos',
    icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01',
    to: '/medicos',
    permiso: 'medicos.ver',
  },
  {
    label: 'Mascotas',
    icon: 'M14.121 14.121L19 19m-7-7l7-7m-7 7l-2.879 2.879M12 12L9.121 9.121m0 5.758a3 3 0 10-4.243 4.243 3 3 0 004.243-4.243zm0-5.758a3 3 0 10-4.243-4.243 3 3 0 004.243 4.243z',
    to: '/mascotas',
    permiso: 'mascotas.ver',
  },
  {
    label: 'Citas',
    icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
    to: '/citas',
    permiso: 'citas.ver',
  },
  {
    label: 'Agenda',
    icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4',
    to: '/agenda',
    permiso: 'citas.ver',
  },
]

const visibleNavItems = computed(() =>
  navItems.filter((item) => !item.permiso || authStore.hasPermiso(item.permiso)),
)

function isActive(to: string): boolean {
  return route.path === to || route.path.startsWith(to + '/')
}

async function handleLogout() {
  authStore.logout()
  await router.push('/login')
}

const userInitials = computed(() => {
  if (!authStore.usuario) return '?'
  return `${authStore.usuario.nombre[0] ?? ''}${authStore.usuario.apellido[0] ?? ''}`.toUpperCase()
})

const rolLabel = computed(() => {
  const rol = authStore.roles[0]
  const labels: Record<string, string> = {
    admin: 'Administrador',
    veterinario: 'Veterinario',
    recepcionista: 'Recepcionista',
    auxiliar: 'Auxiliar',
  }
  return rol ? (labels[rol] ?? rol) : ''
})
</script>

<template>
  <div class="flex h-screen bg-slate-50 overflow-hidden">
    <!-- Sidebar -->
    <aside
      :class="[
        'flex flex-col bg-primary-900 text-white transition-all duration-300 shrink-0',
        sidebarOpen ? 'w-64' : 'w-16',
      ]"
    >
      <!-- Logo -->
      <div class="flex items-center gap-3 px-4 py-5 border-b border-primary-800">
        <div class="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center shrink-0">
          <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
          </svg>
        </div>
        <Transition name="fade">
          <span v-if="sidebarOpen" class="font-bold text-sm leading-tight">
            Gestión<br />Veterinaria
          </span>
        </Transition>
      </div>

      <!-- Nav -->
      <nav class="flex-1 py-4 overflow-y-auto" aria-label="Navegación principal">
        <ul class="space-y-1 px-2">
          <li v-for="item in visibleNavItems" :key="item.to">
            <router-link
              :to="item.to"
              :class="[
                'flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150',
                isActive(item.to)
                  ? 'bg-primary-600 text-white shadow-sm'
                  : 'text-primary-200 hover:bg-primary-800 hover:text-white',
              ]"
              :title="!sidebarOpen ? item.label : undefined"
            >
              <svg class="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" :d="item.icon" />
              </svg>
              <Transition name="fade">
                <span v-if="sidebarOpen" class="truncate">{{ item.label }}</span>
              </Transition>
            </router-link>
          </li>
        </ul>
      </nav>

      <!-- User section -->
      <div class="border-t border-primary-800 p-3">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 bg-primary-500 rounded-full flex items-center justify-center text-xs font-bold shrink-0">
            {{ userInitials }}
          </div>
          <Transition name="fade">
            <div v-if="sidebarOpen" class="flex-1 min-w-0">
              <p class="text-sm font-medium text-white truncate">{{ authStore.nombreCompleto }}</p>
              <p class="text-xs text-primary-300 truncate">{{ rolLabel }}</p>
            </div>
          </Transition>
          <Transition name="fade">
            <button
              v-if="sidebarOpen"
              @click="handleLogout"
              class="p-1.5 rounded-lg text-primary-300 hover:text-white hover:bg-primary-700 transition-colors"
              title="Cerrar sesión"
              aria-label="Cerrar sesión"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
            </button>
          </Transition>
        </div>
      </div>
    </aside>

    <!-- Main content -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- Top bar -->
      <header class="bg-white border-b border-slate-200 px-4 sm:px-6 py-3 flex items-center gap-3 shrink-0">
        <button
          @click="sidebarOpen = !sidebarOpen"
          class="p-2 rounded-lg text-slate-500 hover:text-slate-700 hover:bg-slate-100 transition-colors shrink-0"
          :aria-label="sidebarOpen ? 'Colapsar menú' : 'Expandir menú'"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>

        <!-- Breadcrumb / Page title -->
        <div class="flex-1 min-w-0">
          <slot name="header" />
        </div>

        <!-- Badge de rol: se oculta en pantallas muy pequeñas -->
        <div class="hidden xs:flex sm:flex items-center gap-2 shrink-0">
          <AppBadge variant="success" dot>
            {{ rolLabel }}
          </AppBadge>
        </div>
      </header>

      <!-- Page content -->
      <main class="flex-1 overflow-y-auto p-6">
        <slot />
      </main>
    </div>
  </div>
</template>
