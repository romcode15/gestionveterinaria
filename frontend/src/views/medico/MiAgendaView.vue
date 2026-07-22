<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { useAuthStore } from '@/stores/auth.store'
import { useCitasStore } from '@/stores/citas.store'
import { api } from '@/services/api'
import type { Cita, EstadoCita } from '@/types'

const router    = useRouter()
const authStore = useAuthStore()
const citasStore = useCitasStore()

// Fecha seleccionada para la agenda (hoy por defecto)
const fechaHoy      = new Date().toISOString().split('T')[0]!
const fechaSeleccionada = ref(fechaHoy)

// Perfil del médico autenticado
const perfilMedico = ref<{ nombre: string; apellido: string; especialidades: { nombre: string }[] } | null>(null)

onMounted(async () => {
  await Promise.all([
    citasStore.cargarCatalogos(),
    cargarAgenda(),
    cargarPerfil(),
  ])
})

async function cargarPerfil() {
  try {
    const res = await api.get<{ nombre: string; apellido: string; especialidades: { nombre: string }[] }>(
      '/api/portal/medico/perfil'
    )
    perfilMedico.value = res
  } catch {
    // si falla no interrumpe la vista
  }
}

async function cargarAgenda() {
  // Carga las citas del médico autenticado para la fecha seleccionada
  await citasStore.cargarPorFecha(fechaSeleccionada.value)
}

async function cambiarFecha(delta: number) {
  const d = new Date(fechaSeleccionada.value)
  d.setDate(d.getDate() + delta)
  fechaSeleccionada.value = d.toISOString().split('T')[0]!
  await cargarAgenda()
}

// Cambiar estado de una cita directamente desde la agenda
const cambiandoEstado = ref<number | null>(null)
async function cambiarEstado(cita: Cita, estado: EstadoCita) {
  cambiandoEstado.value = cita.id
  try {
    await citasStore.cambiarEstado(cita.id, estado)
  } finally {
    cambiandoEstado.value = null
  }
}

// Estadísticas del día
const stats = computed(() => ({
  total:       citasStore.citas.length,
  pendientes:  citasStore.citas.filter(c => c.estado === 'pendiente').length,
  confirmadas: citasStore.citas.filter(c => c.estado === 'confirmada').length,
  enCurso:     citasStore.citas.filter(c => c.estado === 'en_curso').length,
  completadas: citasStore.citas.filter(c => c.estado === 'completada').length,
}))

// Formato de fecha legible
const fechaLabel = computed(() => {
  const d = new Date(fechaSeleccionada.value + 'T12:00:00')
  if (fechaSeleccionada.value === fechaHoy) return 'Hoy'
  return d.toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long' })
})

// Siguiente estado sugerido para la acción rápida
function siguienteEstado(estado: EstadoCita): EstadoCita | null {
  const mapa: Partial<Record<EstadoCita, EstadoCita>> = {
    pendiente:  'confirmada',
    confirmada: 'en_curso',
    en_curso:   'completada',
  }
  return mapa[estado] ?? null
}

function labelSiguienteEstado(estado: EstadoCita): string {
  const labels: Partial<Record<EstadoCita, string>> = {
    pendiente:  'Confirmar',
    confirmada: 'Iniciar',
    en_curso:   'Completar',
  }
  return labels[estado] ?? ''
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <div>
        <h1 class="text-lg font-semibold" style="color: var(--text-primary)">Mi Agenda</h1>
        <p class="text-xs" style="color: var(--text-muted)">
          {{ perfilMedico ? `Dr. ${perfilMedico.nombre} ${perfilMedico.apellido}` : 'Portal médico' }}
        </p>
      </div>
    </template>

    <div class="space-y-4">
      <AppAlert v-if="citasStore.error" type="error" dismissible @dismiss="citasStore.limpiarError()">
        {{ citasStore.error }}
      </AppAlert>

      <!-- Navegación de fecha -->
      <AppCard padding="sm">
        <div class="flex items-center justify-between gap-4">
          <button
            class="p-2 rounded-lg transition-colors"
            style="color: var(--text-muted)"
            :class="'hover:bg-[var(--bg-surface-2)'"
            @click="cambiarFecha(-1)"
            aria-label="Día anterior"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
            </svg>
          </button>

          <div class="text-center">
            <p class="font-semibold capitalize" style="color: var(--text-primary)">{{ fechaLabel }}</p>
            <p class="text-xs" style="color: var(--text-muted)">{{ fechaSeleccionada }}</p>
          </div>

          <button
            class="p-2 rounded-lg transition-colors"
            style="color: var(--text-muted)"
            :class="'hover:bg-(--bg-surface-2)'"
            @click="cambiarFecha(1)"
            aria-label="Día siguiente"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </button>

          <button
            v-if="fechaSeleccionada !== fechaHoy"
            class="ml-auto text-xs px-3 py-1.5 rounded-lg bg-(--color-primary) text-white"
            @click="() => { fechaSeleccionada = fechaHoy; cargarAgenda() }"
          >
            Hoy
          </button>
        </div>
      </AppCard>

      <!-- Stats del día -->
      <div class="grid grid-cols-2 sm:grid-cols-5 gap-3">
        <AppCard padding="sm" class="text-center">
          <p class="text-2xl font-bold" style="color: var(--text-primary)">{{ stats.total }}</p>
          <p class="text-xs mt-0.5" style="color: var(--text-muted)">Total</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-2xl font-bold text-yellow-600">{{ stats.pendientes }}</p>
          <p class="text-xs mt-0.5" style="color: var(--text-muted)">Pendientes</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-2xl font-bold text-blue-600">{{ stats.confirmadas }}</p>
          <p class="text-xs mt-0.5" style="color: var(--text-muted)">Confirmadas</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-2xl font-bold" style="color: var(--color-primary)">{{ stats.enCurso }}</p>
          <p class="text-xs mt-0.5" style="color: var(--text-muted)">En curso</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-2xl font-bold text-green-600">{{ stats.completadas }}</p>
          <p class="text-xs mt-0.5" style="color: var(--text-muted)">Completadas</p>
        </AppCard>
      </div>

      <!-- Lista de citas del día -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b" style="border-color: var(--border-default)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            Citas del día
          </h2>
        </div>

        <!-- Loading -->
        <div v-if="citasStore.loading" class="p-6 space-y-3">
          <div v-for="i in 4" :key="i" class="h-20 vg-skeleton rounded-xl animate-pulse" />
        </div>

        <!-- Sin citas -->
        <div
          v-else-if="citasStore.citas.length === 0"
          class="py-16 text-center"
          style="color: var(--text-muted)"
        >
          <svg class="w-12 h-12 mx-auto mb-3" style="color: var(--text-disabled)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
              d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
          </svg>
          <p>No tienes citas para este día</p>
        </div>

        <!-- Citas -->
        <div v-else class="divide-y" style="border-color: var(--border-default)">
          <div
            v-for="cita in citasStore.citas"
            :key="cita.id"
            class="px-6 py-4 flex flex-col sm:flex-row sm:items-center gap-4 transition-colors"
            style="transition: background-color 0.15s"
            :class="cita.estado === 'en_curso' ? 'bg-[rgba(5,150,105,0.05)]' : 'hover:bg-(--bg-surface-2)'"
          >
            <!-- Hora -->
            <div class="text-center shrink-0 w-16">
              <p class="text-sm font-bold" style="color: var(--text-primary)">{{ cita.horaInicio }}</p>
              <p class="text-xs" style="color: var(--text-muted)">{{ cita.horaFin }}</p>
            </div>

            <!-- Barra de color tipo cita -->
            <div
              class="w-1 h-12 rounded-full shrink-0 hidden sm:block"
              :style="{ backgroundColor: cita.tipoCita?.color ?? '#059669' }"
            />

            <!-- Info cita -->
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 flex-wrap">
                <p class="font-semibold" style="color: var(--text-primary)">
                  {{ cita.mascotaNombre }}
                </p>
                <AppBadge variant="neutral">{{ cita.tipoCita?.nombre }}</AppBadge>
              </div>
              <p class="text-sm mt-0.5 truncate" style="color: var(--text-muted)">
                {{ cita.clienteNombre }} · {{ cita.motivo }}
              </p>
            </div>

            <!-- Estado + acciones -->
            <div class="flex items-center gap-2 shrink-0 flex-wrap">
              <StatusBadge :estado="cita.estado" />

              <!-- Botón de acción rápida (avanzar estado) -->
              <button
                v-if="siguienteEstado(cita.estado)"
                class="text-xs px-3 py-1.5 rounded-lg font-medium transition-colors"
                style="background-color: var(--bg-surface-2); color: var(--text-secondary)"
                :class="'hover:bg-(--color-primary) hover:text-white'"
                :disabled="cambiandoEstado === cita.id"
                @click="cambiarEstado(cita, siguienteEstado(cita.estado)!)"
              >
                {{ labelSiguienteEstado(cita.estado) }}
              </button>

              <!-- Ir al diagnóstico si está completada o en curso -->
              <button
                v-if="cita.estado === 'en_curso' || cita.estado === 'completada'"
                class="text-xs px-3 py-1.5 rounded-lg font-medium transition-colors bg-(--color-primary) text-white"
                :class="'hover:opacity-90'"
                @click="router.push(`/diagnosticos?citaId=${cita.id}`)"
              >
                Diagnóstico
              </button>
            </div>
          </div>
        </div>
      </AppCard>

      <!-- Acceso rápido a mis diagnósticos -->
      <AppCard padding="sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="font-medium" style="color: var(--text-primary)">Mis diagnósticos registrados</p>
            <p class="text-xs mt-0.5" style="color: var(--text-muted)">Historial de consultas atendidas</p>
          </div>
          <AppButton variant="secondary" @click="router.push('/mis-diagnosticos')">
            Ver todos
          </AppButton>
        </div>
      </AppCard>
    </div>
  </DashboardLayout>
</template>
