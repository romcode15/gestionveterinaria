<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import { useAuthStore } from '@/stores/auth.store'
import { useClientesStore } from '@/stores/clientes.store'
import { useMascotasStore } from '@/stores/mascotas.store'
import { useCitasStore } from '@/stores/citas.store'
import { useMedicosStore } from '@/stores/medicos.store'
import CitaStatusBadge from '@/components/citas/CitaStatusBadge.vue'

const router = useRouter()
const authStore = useAuthStore()
const clientesStore = useClientesStore()
const mascotasStore = useMascotasStore()
const citasStore = useCitasStore()
const medicosStore = useMedicosStore()

const stats = computed(() => [
  {
    label: 'Clientes activos',
    value: clientesStore.clientes.filter((c) => c.estado === 'activo').length,
    icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z',
    color: 'text-primary-600',
    bg: 'bg-primary-100',
    to: '/clientes',
  },
  {
    label: 'Mascotas registradas',
    value: mascotasStore.mascotas.filter((m) => m.estado === 'activo').length,
    icon: 'M14.121 14.121L19 19m-7-7l7-7m-7 7l-2.879 2.879M12 12L9.121 9.121m0 5.758a3 3 0 10-4.243 4.243 3 3 0 004.243-4.243zm0-5.758a3 3 0 10-4.243-4.243 3 3 0 004.243 4.243z',
    color: 'text-secondary-600',
    bg: 'bg-secondary-100',
    to: '/mascotas',
  },
  {
    label: 'Citas hoy',
    value: citasStore.citasHoy.length,
    icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
    color: 'text-accent-600',
    bg: 'bg-accent-100',
    to: '/citas',
  },
  {
    label: 'Médicos disponibles',
    value: medicosStore.medicos.filter((m) => m.disponible).length,
    icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2',
    color: 'text-blue-600',
    bg: 'bg-blue-100',
    to: '/medicos',
  },
])

const citasHoyOrdenadas = computed(() =>
  [...citasStore.citasHoy].sort((a, b) => a.horaInicio.localeCompare(b.horaInicio)),
)

const hora = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return 'Buenos días'
  if (h < 18) return 'Buenas tardes'
  return 'Buenas noches'
})
</script>

<template>
  <DashboardLayout>
    <template #header>
      <div>
        <h1 class="text-lg font-semibold text-slate-800">Dashboard</h1>
        <p class="text-xs text-slate-500">Resumen del sistema</p>
      </div>
    </template>

    <!-- Saludo -->
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-slate-800">
        {{ hora }}, {{ authStore.usuario?.nombre }} 👋
      </h2>
      <p class="text-slate-500 mt-1">Aquí tienes el resumen de hoy</p>
    </div>

    <!-- Stats cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
      <AppCard
        v-for="stat in stats"
        :key="stat.label"
        hover
        @click="router.push(stat.to)"
      >
        <div class="flex items-center gap-4">
          <div :class="['w-12 h-12 rounded-xl flex items-center justify-center shrink-0', stat.bg]">
            <svg class="w-6 h-6" :class="stat.color" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" :d="stat.icon" />
            </svg>
          </div>
          <div>
            <p class="text-2xl font-bold text-slate-800">{{ stat.value }}</p>
            <p class="text-sm text-slate-500">{{ stat.label }}</p>
          </div>
        </div>
      </AppCard>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Citas de hoy -->
      <div class="lg:col-span-2">
        <AppCard padding="none">
          <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
            <h3 class="font-semibold text-slate-800">Citas de hoy</h3>
            <AppBadge variant="info">{{ citasHoyOrdenadas.length }} citas</AppBadge>
          </div>
          <div class="divide-y divide-slate-100">
            <div
              v-if="citasHoyOrdenadas.length === 0"
              class="px-6 py-10 text-center text-slate-400 text-sm"
            >
              No hay citas programadas para hoy
            </div>
            <div
              v-for="cita in citasHoyOrdenadas"
              :key="cita.id"
              class="px-6 py-4 flex items-center gap-4 hover:bg-slate-50 transition-colors cursor-pointer"
              @click="router.push('/citas')"
            >
              <div class="text-center shrink-0 w-14">
                <p class="text-sm font-bold text-slate-800">{{ cita.horaInicio }}</p>
                <p class="text-xs text-slate-400">{{ cita.horaFin }}</p>
              </div>
              <div
                class="w-1 h-10 rounded-full shrink-0"
                :style="{ backgroundColor: cita.tipoCita.color }"
              />
              <div class="flex-1 min-w-0">
                <p class="font-medium text-slate-800 truncate">{{ cita.mascotaNombre }}</p>
                <p class="text-sm text-slate-500 truncate">
                  {{ cita.tipoCita.nombre }} · {{ cita.medicoNombre }}
                </p>
              </div>
              <CitaStatusBadge :estado="cita.estado" />
            </div>
          </div>
        </AppCard>
      </div>

      <!-- Resumen de citas -->
      <div class="space-y-4">
        <AppCard>
          <h3 class="font-semibold text-slate-800 mb-4">Estado de citas</h3>
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <span class="text-sm text-slate-600">Pendientes</span>
              <AppBadge variant="warning" dot>{{ citasStore.estadisticas.pendientes }}</AppBadge>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm text-slate-600">Confirmadas</span>
              <AppBadge variant="info" dot>{{ citasStore.estadisticas.confirmadas }}</AppBadge>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm text-slate-600">En curso</span>
              <AppBadge variant="primary" dot>{{ citasStore.estadisticas.enCurso }}</AppBadge>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm text-slate-600">Completadas</span>
              <AppBadge variant="success" dot>{{ citasStore.estadisticas.completadas }}</AppBadge>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm text-slate-600">Canceladas</span>
              <AppBadge variant="danger" dot>{{ citasStore.estadisticas.canceladas }}</AppBadge>
            </div>
          </div>
        </AppCard>

        <!-- Accesos rápidos -->
        <AppCard>
          <h3 class="font-semibold text-slate-800 mb-4">Accesos rápidos</h3>
          <div class="space-y-2">
            <button
              @click="router.push('/clientes')"
              class="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-slate-700
                     hover:bg-primary-50 hover:text-primary-700 transition-colors text-left"
            >
              <svg class="w-4 h-4 text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              Nuevo cliente
            </button>
            <button
              @click="router.push('/mascotas')"
              class="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-slate-700
                     hover:bg-primary-50 hover:text-primary-700 transition-colors text-left"
            >
              <svg class="w-4 h-4 text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              Registrar mascota
            </button>
            <button
              @click="router.push('/citas')"
              class="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-slate-700
                     hover:bg-primary-50 hover:text-primary-700 transition-colors text-left"
            >
              <svg class="w-4 h-4 text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              Agendar cita
            </button>
          </div>
        </AppCard>
      </div>
    </div>
  </DashboardLayout>
</template>
