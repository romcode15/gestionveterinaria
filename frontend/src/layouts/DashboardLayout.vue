<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { useThemeStore } from '@/stores/theme.store'
import AppBadge from '@/components/ui/AppBadge.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const themeStore = useThemeStore()

const sidebarOpen = ref(true)

interface NavItem {
  label: string
  icon: string
  to: string
  permiso?: string
  rol?: string          // requiere exactamente este rol
  roles?: string[]      // requiere uno de estos roles
  rolExcluido?: string  // oculto si el usuario tiene este rol
  badge?: number
}

// Navegación para roles staff (admin, veterinario, recepcionista)
const navItemsStaff: NavItem[] = [
  {
    label: 'Dashboard',
    icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6',
    to: '/dashboard',
    rolExcluido: 'veterinario',
  },
  // ── Solo ADMIN y RECEPCIONISTA ─────────────────────────────────────────
  {
    label: 'Clientes',
    icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z',
    to: '/clientes',
    permiso: 'clientes.ver',
  },
  // ── Solo ADMIN ─────────────────────────────────────────────────────────
  {
    label: 'Médicos',
    icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01',
    to: '/medicos',
    permiso: 'medicos.ver',
    rol: 'admin',
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
    rolExcluido: 'veterinario',   // el veterinario usa su propia agenda en /mi-agenda
  },
  // ── Portal Médico — solo VETERINARIO ───────────────────────────────────
  {
    label: 'Mi Agenda',
    icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4',
    to: '/mi-agenda',
    rol: 'veterinario',
  },
  {
    label: 'Diagnósticos',
    icon: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z',
    to: '/diagnosticos',
    roles: ['admin', 'veterinario', 'recepcionista'],
  },
  {
    label: 'Vacunación',
    icon: 'M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z',
    to: '/vacunacion',
    roles: ['admin', 'veterinario', 'recepcionista'],
  },
  // Inventario oculto temporalmente
  // {
  //   label: 'Inventario',
  //   icon: 'M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4',
  //   to: '/inventario',
  //   roles: ['admin', 'veterinario', 'recepcionista'],
  // },
  // ── Solo ADMIN ─────────────────────────────────────────────────────────
  {
    label: 'Recepcionistas',
    icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z',
    to: '/recepcionistas',
    rol: 'admin',
  },
  {
    label: 'Usuarios',
    icon: 'M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z',
    to: '/usuarios',
    rol: 'admin',
  },
  {
    label: 'Auditoría',
    icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01',
    to: '/auditoria',
    rol: 'admin',
  },
]

// Navegación para clientes: solo su portal y sus citas
const navItemsCliente: NavItem[] = [
  {
    label: 'Mi portal',
    icon: 'M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6',
    to: '/mi-portal',
    permiso: 'cliente.mis_mascotas',
  },
  {
    label: 'Mis citas',
    icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
    to: '/mis-citas',
    permiso: 'cliente.mis_citas',
  },
]

const visibleNavItems = computed(() => {
  const items = authStore.isCliente ? navItemsCliente : navItemsStaff
  return items.filter((item) => {
    // Ocultar si el usuario tiene el rol excluido
    if (item.rolExcluido && authStore.hasRole(item.rolExcluido as import('@/types').RoleName)) return false
    // Requiere un rol exacto
    if (item.rol && !authStore.hasRole(item.rol as import('@/types').RoleName)) return false
    // Requiere uno de varios roles
    if (item.roles && !item.roles.some((r) => authStore.hasRole(r as import('@/types').RoleName))) return false
    // Requiere un permiso específico
    if (item.permiso && !authStore.hasPermiso(item.permiso)) return false
    return true
  })
})

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
    cliente: 'Cliente',
  }
  return rol ? (labels[rol] ?? rol) : ''
})
</script>

<template>
  <div class="flex h-screen overflow-hidden" style="background-color: var(--bg-base)">

    <!-- Sidebar -->
    <aside
      :class="[
        'vg-sidebar flex flex-col transition-all duration-300 shrink-0',
        sidebarOpen ? 'w-64' : 'w-16',
      ]"
    >
      <!-- Logo -->
      <div
        class="flex items-center gap-3 px-4 py-5 vg-sidebar-border"
        style="border-bottom-width: 1px; border-bottom-style: solid"
      >
        <div class="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center shrink-0">
          <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
          </svg>
        </div>
        <Transition name="fade">
          <span v-if="sidebarOpen" class="font-bold text-sm leading-tight text-white">
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
                  ? 'vg-sidebar-item-active'
                  : 'vg-sidebar-text vg-sidebar-item-hover',
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

      <!-- Toggle tema + User section -->
      <div
        class="vg-sidebar-border p-3 space-y-2"
        style="border-top-width: 1px; border-top-style: solid"
      >
        <!-- Toggle tema (solo cuando sidebar abierto) -->
        <Transition name="fade">
          <div
            v-if="sidebarOpen"
            class="flex items-center justify-between px-1 py-1"
          >
            <span class="text-xs vg-sidebar-text flex items-center gap-2">
              <svg v-if="themeStore.theme === 'light'" class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 3v1m0 16v1m8.66-13H20m-17 0H2m14.95 9.07l-.71-.71M7.76 7.76l-.71-.71M19.07 19.07l-.71-.71M5.64 5.64l-.71-.71M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <svg v-else class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
              </svg>
              {{ themeStore.theme === 'light' ? 'Claro' : 'Oscuro' }}
            </span>
            <button
              class="theme-toggle"
              role="switch"
              :aria-checked="themeStore.theme === 'dark'"
              aria-label="Cambiar tema"
              @click="themeStore.toggle()"
            >
              <span class="theme-toggle-thumb" />
            </button>
          </div>
        </Transition>

        <!-- Icono tema cuando sidebar cerrado -->
        <button
          v-if="!sidebarOpen"
          @click="themeStore.toggle()"
          class="w-full flex items-center justify-center p-2 rounded-xl vg-sidebar-text vg-sidebar-item-hover transition-colors"
          :aria-label="themeStore.theme === 'dark' ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'"
          :title="themeStore.theme === 'dark' ? 'Modo claro' : 'Modo oscuro'"
        >
          <svg v-if="themeStore.theme === 'light'" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
              d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
          </svg>
          <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
              d="M12 3v1m0 16v1m8.66-13H20m-17 0H2m14.95 9.07l-.71-.71M7.76 7.76l-.71-.71M19.07 19.07l-.71-.71M5.64 5.64l-.71-.71M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </button>

        <!-- User -->
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 vg-sidebar-user-bg rounded-full flex items-center justify-center text-xs font-bold text-white shrink-0">
            {{ userInitials }}
          </div>
          <Transition name="fade">
            <div v-if="sidebarOpen" class="flex-1 min-w-0">
              <p class="text-sm font-medium text-white truncate">{{ authStore.nombreCompleto }}</p>
              <p class="text-xs vg-sidebar-text truncate">{{ rolLabel }}</p>
            </div>
          </Transition>
          <Transition name="fade">
            <div v-if="sidebarOpen" class="flex items-center gap-1">
              <!-- Perfil -->
              <button
                @click="router.push('/perfil')"
                class="p-1.5 rounded-lg vg-sidebar-text hover:text-white vg-sidebar-item-hover transition-colors"
                title="Mi perfil"
                aria-label="Mi perfil"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </button>
              <!-- Cerrar sesión -->
              <button
                @click="handleLogout"
                class="p-1.5 rounded-lg vg-sidebar-text hover:text-white vg-sidebar-item-hover transition-colors"
                title="Cerrar sesión"
                aria-label="Cerrar sesión"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </aside>

    <!-- Main content -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- Top bar -->
      <header class="vg-topbar px-4 sm:px-6 py-3 flex items-center gap-3 shrink-0">
        <button
          @click="sidebarOpen = !sidebarOpen"
          class="p-2 rounded-lg transition-colors shrink-0"
          style="color: var(--text-muted)"
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

        <!-- Badge de rol -->
        <div class="hidden xs:flex sm:flex items-center gap-2 shrink-0">
          <AppBadge variant="success" dot>
            {{ rolLabel }}
          </AppBadge>
        </div>
      </header>

      <!-- Page content -->
      <main class="flex-1 overflow-hidden flex flex-col">
        <slot />
      </main>
    </div>
  </div>
</template>
