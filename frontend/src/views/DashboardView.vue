<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppButton from '@/components/ui/AppButton.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import EmptyState from '@/components/common/EmptyState.vue'

import { useAuthStore } from '@/stores/auth.store'
import { useDashboardStore } from '@/stores/dashboard.store'
import { useCitasStore } from '@/stores/citas.store'
import ChatWidget from '@/components/chat/ChatWidget.vue'

const router         = useRouter()
const authStore      = useAuthStore()
const dashboardStore = useDashboardStore()
const citasStore     = useCitasStore()

onMounted(async () => {
  if (authStore.isMedico) {
    // Veterinario: solo sus datos del día
    await Promise.all([
      citasStore.cargarHoy(authStore.medicoId),
      citasStore.cargar({ page: 0, size: 10 }),
    ])
  } else {
    // Admin / recepcionista: resumen general (1 request) + citas de hoy
    await Promise.all([
      dashboardStore.cargarResumen(),
      citasStore.cargarHoy(null),
    ])
  }
})

const stats = computed(() => {
  const esMedico = authStore.isMedico

  if (esMedico) {
    return [
      {
        label: 'Mis citas hoy',
        value: citasStore.citasHoy.length,
        icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
        iconColor: '#d97706',
        iconBg: 'rgba(217,119,6,0.12)',
        to: '/mi-agenda',
      },
      {
        label: 'Pendientes',
        value: citasStore.citasHoy.filter((c) => c.estado === 'pendiente').length,
        icon: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z',
        iconColor: '#f59e0b',
        iconBg: 'rgba(245,158,11,0.12)',
        to: '/mi-agenda',
      },
      {
        label: 'En curso',
        value: citasStore.citasHoy.filter((c) => c.estado === 'en_curso').length,
        icon: 'M13 10V3L4 14h7v7l9-11h-7z',
        iconColor: '#059669',
        iconBg: 'rgba(5,150,105,0.12)',
        to: '/mi-agenda',
      },
      {
        label: 'Completadas hoy',
        value: citasStore.citasHoy.filter((c) => c.estado === 'completada').length,
        icon: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
        iconColor: '#2563eb',
        iconBg: 'rgba(37,99,235,0.12)',
        to: '/mis-diagnosticos',
      },
    ]
  }

  return [
    {
      label: 'Clientes activos',
      value: dashboardStore.resumen?.totalClientesActivos ?? 0,
      icon: 'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z',
      iconColor: '#059669',
      iconBg: 'rgba(5,150,105,0.12)',
      to: '/clientes',
    },
    {
      label: 'Mascotas activas',
      value: dashboardStore.resumen?.totalMascotasActivas ?? 0,
      icon: 'M14.121 14.121L19 19m-7-7l7-7m-7 7l-2.879 2.879M12 12L9.121 9.121m0 5.758a3 3 0 10-4.243 4.243 3 3 0 004.243-4.243zm0-5.758a3 3 0 10-4.243-4.243 3 3 0 004.243 4.243z',
      iconColor: '#0d9488',
      iconBg: 'rgba(13,148,136,0.12)',
      to: '/mascotas',
    },
    {
      label: 'Citas hoy',
      value: dashboardStore.resumen?.totalCitasHoy ?? 0,
      icon: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
      iconColor: '#d97706',
      iconBg: 'rgba(217,119,6,0.12)',
      to: '/citas',
    },
    {
      label: 'Médicos disponibles',
      value: dashboardStore.resumen?.totalMedicosDisponibles ?? 0,
      icon: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2',
      iconColor: '#2563eb',
      iconBg: 'rgba(37,99,235,0.12)',
      to: '/medicos',
    },
  ]
})

// Citas de hoy ordenadas por hora
const citasHoyOrdenadas = computed(() =>
  [...citasStore.citasHoy].sort((a, b) => a.horaInicio.localeCompare(b.horaInicio)),
)

const hora = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return 'Buenos días'
  if (h < 18) return 'Buenas tardes'
  return 'Buenas noches'
})

const accesosRapidos = [
  { label: 'Nuevo cliente',    to: '/clientes' },
  { label: 'Registrar mascota', to: '/mascotas' },
  { label: 'Agendar cita',     to: '/citas' },
]
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Dashboard" subtitle="Resumen del sistema" />
    </template>

    <!-- Saludo -->
    <div class="mb-6">
      <h2 class="text-2xl font-bold" style="color: var(--text-primary)">
        {{ hora }}, {{ authStore.usuario?.nombre ?? 'Usuario' }}
      </h2>
      <p class="mt-1" style="color: var(--text-muted)">
        {{ authStore.isMedico ? 'Aquí tienes tu agenda de hoy' : 'Aquí tienes el resumen de hoy' }}
      </p>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
      <AppCard
        v-for="stat in stats"
        :key="stat.label"
        hover
        @click="router.push(stat.to)"
      >
        <div class="flex items-center gap-4">
          <div
            class="w-12 h-12 rounded-xl flex items-center justify-center shrink-0"
            :style="{ backgroundColor: stat.iconBg }"
          >
            <svg
              class="w-6 h-6"
              :style="{ color: stat.iconColor }"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              aria-hidden="true"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" :d="stat.icon" />
            </svg>
          </div>
          <div>
            <p class="text-2xl font-bold" style="color: var(--text-primary)">{{ stat.value }}</p>
            <p class="text-sm" style="color: var(--text-muted)">{{ stat.label }}</p>
          </div>
        </div>
      </AppCard>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Citas de hoy (título cambia según rol) -->
      <div class="lg:col-span-2">
        <AppCard padding="none">
          <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-color)">
            <h3 class="font-semibold" style="color: var(--text-primary)">
              {{ authStore.isMedico ? 'Mis citas de hoy' : 'Citas de hoy' }}
            </h3>
            <AppBadge variant="info">{{ citasHoyOrdenadas.length }} citas</AppBadge>
          </div>

          <EmptyState
            v-if="citasHoyOrdenadas.length === 0"
            title="Sin citas"
            message="No hay citas programadas para hoy"
          />

          <div v-else class="divide-y" style="border-color: var(--border-color)">
            <div
              v-for="cita in citasHoyOrdenadas"
              :key="cita.id"
              class="px-6 py-4 flex items-center gap-4 vg-table-row-hover transition-colors cursor-pointer"
              @click="router.push(authStore.isMedico ? '/mi-agenda' : '/citas')"
            >
              <div class="text-center shrink-0 w-14">
                <p class="text-sm font-bold" style="color: var(--text-primary)">{{ cita.horaInicio }}</p>
                <p class="text-xs" style="color: var(--text-muted)">{{ cita.horaFin }}</p>
              </div>
              <div
                class="w-1 h-10 rounded-full shrink-0"
                :style="{ backgroundColor: cita.tipoCita?.color ?? '#059669' }"
              />
              <div class="flex-1 min-w-0">
                <p class="font-medium truncate" style="color: var(--text-primary)">{{ cita.mascotaNombre }}</p>
                <p class="text-sm truncate" style="color: var(--text-muted)">
                  {{ cita.tipoCita?.nombre }} · {{ cita.clienteNombre }}
                </p>
              </div>
              <StatusBadge :status="cita.estado" />
            </div>
          </div>
        </AppCard>
      </div>

      <!-- Panel derecho -->
      <div class="space-y-4">
        <!-- Estado de citas del día -->
        <AppCard>
          <h3 class="font-semibold mb-4" style="color: var(--text-primary)">Estado de citas hoy</h3>
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <span class="text-sm" style="color: var(--text-secondary)">Pendientes</span>
              <AppBadge variant="warning" dot>{{ citasStore.estadisticas?.pendientes ?? 0 }}</AppBadge>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm" style="color: var(--text-secondary)">Confirmadas</span>
              <AppBadge variant="info" dot>{{ citasStore.estadisticas?.confirmadas ?? 0 }}</AppBadge>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm" style="color: var(--text-secondary)">En curso</span>
              <AppBadge variant="primary" dot>{{ citasStore.estadisticas?.enCurso ?? 0 }}</AppBadge>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm" style="color: var(--text-secondary)">Completadas</span>
              <AppBadge variant="success" dot>{{ citasStore.estadisticas?.completadas ?? 0 }}</AppBadge>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm" style="color: var(--text-secondary)">Canceladas</span>
              <AppBadge variant="danger" dot>{{ citasStore.estadisticas?.canceladas ?? 0 }}</AppBadge>
            </div>
          </div>
        </AppCard>

        <!-- Accesos rápidos: distintos según rol -->
        <AppCard>
          <h3 class="font-semibold mb-4" style="color: var(--text-primary)">Accesos rápidos</h3>
          <div class="space-y-2">
            <!-- Veterinario -->
            <template v-if="authStore.isMedico">
              <AppButton variant="ghost" full-width class="justify-start" @click="router.push('/mi-agenda')">
                <svg class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                Mi agenda
              </AppButton>
              <AppButton variant="ghost" full-width class="justify-start" @click="router.push('/diagnosticos')">
                <svg class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                Mis diagnósticos
              </AppButton>
              <AppButton variant="ghost" full-width class="justify-start" @click="router.push('/vacunacion')">
                <svg class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z" />
                </svg>
                Vacunación
              </AppButton>
            </template>
            <!-- Admin / recepcionista -->
            <template v-else>
              <AppButton v-for="acceso in accesosRapidos" :key="acceso.to"
                variant="ghost" full-width class="justify-start" @click="router.push(acceso.to)">
                <svg class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                </svg>
                {{ acceso.label }}
              </AppButton>
            </template>
          </div>
        </AppCard>
      </div>
    </div>
  </DashboardLayout>

  <!-- Chatbot IA: solo visible para el administrador -->
  <ChatWidget v-if="authStore.isAdmin()" />
</template>