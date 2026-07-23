<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'

import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import EntitySummary from '@/components/common/EntitySummary.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import LoadingState from '@/components/common/LoadingState.vue'

import { useAuthStore } from '@/stores/auth.store'
import { useCitasStore } from '@/stores/citas.store'
import { api } from '@/services/api'
import type { Cita, EstadoCita } from '@/types'

const router     = useRouter()
const authStore  = useAuthStore()
const citasStore = useCitasStore()

const fechaHoy           = new Date().toISOString().split('T')[0]!
const fechaSeleccionada  = ref(fechaHoy)
const perfilMedico       = ref<{ nombre: string; apellido: string; especialidades: { nombre: string }[] } | null>(null)
const cambiandoEstado    = ref<number | null>(null)
const loading            = ref(false)

onMounted(async () => {
  await Promise.all([
    citasStore.cargarCatalogos(),
    cargarAgenda(),
    cargarPerfil(),
  ])
})

async function cargarPerfil() {
  try {
    perfilMedico.value = await api.get<{ nombre: string; apellido: string; especialidades: { nombre: string }[] }>(
      '/api/portal/medico/perfil'
    )
  } catch { /* no interrumpe la vista */ }
}

async function cargarAgenda() {
  // Usa el portal médico: filtra automáticamente por el médico autenticado
  loading.value = true
  citasStore.error = null
  try {
    const res = await api.getPaged<Record<string, unknown>>('/api/portal/medico/citas/fecha', {
      fecha: fechaSeleccionada.value,
      size: 50,
      sort: 'horaInicio',
      dir: 'asc',
    })
    // Reconstruir objeto anidado tipoCita a partir de los campos planos del DTO
    citasStore.citas = res.content.map((raw) => ({
      ...(raw as unknown as Cita),
      tipoCita: {
        id:              raw['tipoCitaId']              as number,
        nombre:          (raw['tipoCitaNombre']          as string) ?? '',
        duracionMinutos: (raw['tipoCitaDuracionMinutos'] as number) ?? 0,
        color:           (raw['tipoCitaColor']           as string) ?? '#059669',
        descripcion:     raw['tipoCitaDescripcion']      as string | undefined,
      },
    }))
  } catch (e) {
    citasStore.error = e instanceof Error ? e.message : 'Error al cargar agenda'
  } finally {
    loading.value = false
  }
}

async function cambiarFecha(delta: number) {
  const d = new Date(fechaSeleccionada.value)
  d.setDate(d.getDate() + delta)
  fechaSeleccionada.value = d.toISOString().split('T')[0]!
  await cargarAgenda()
}

async function cambiarEstado(cita: Cita, estado: EstadoCita) {
  cambiandoEstado.value = cita.id
  try {
    await citasStore.cambiarEstado(cita.id, estado)
  } finally {
    cambiandoEstado.value = null
  }
}

const stats = computed(() => [
  { label: 'Total',       value: citasStore.citas.length,                                      icon: '📋' },
  { label: 'Pendientes',  value: citasStore.citas.filter(c => c.estado === 'pendiente').length,  icon: '⏳' },
  { label: 'Confirmadas', value: citasStore.citas.filter(c => c.estado === 'confirmada').length, icon: '✅' },
  { label: 'En curso',    value: citasStore.citas.filter(c => c.estado === 'en_curso').length,   icon: '🔄' },
  { label: 'Completadas', value: citasStore.citas.filter(c => c.estado === 'completada').length, icon: '✔️' },
])

const fechaLabel = computed(() => {
  const d = new Date(fechaSeleccionada.value + 'T12:00:00')
  if (fechaSeleccionada.value === fechaHoy) return 'Hoy'
  return d.toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long' })
})

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
      <PageHeader
        title="Mi Agenda"
        :subtitle="perfilMedico ? `Dr. ${perfilMedico.nombre} ${perfilMedico.apellido}` : 'Portal médico'"
      />
    </template>

    <div class="space-y-4">
      <AppAlert v-if="citasStore.error" type="error" dismissible @dismiss="citasStore.limpiarError()">
        {{ citasStore.error }}
      </AppAlert>

      <!-- Navegación de fecha -->
      <AppCard padding="sm">
        <div class="flex items-center justify-between gap-4">
          <AppButton variant="ghost" size="sm" aria-label="Día anterior" @click="cambiarFecha(-1)">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
            </svg>
          </AppButton>

          <div class="text-center">
            <p class="font-semibold capitalize" style="color: var(--text-primary)">{{ fechaLabel }}</p>
            <p class="text-xs" style="color: var(--text-muted)">{{ fechaSeleccionada }}</p>
          </div>

          <AppButton variant="ghost" size="sm" aria-label="Día siguiente" @click="cambiarFecha(1)">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
            </svg>
          </AppButton>

          <AppButton
            v-if="fechaSeleccionada !== fechaHoy"
            size="sm"
            class="ml-auto"
            @click="() => { fechaSeleccionada = fechaHoy; cargarAgenda() }"
          >
            Hoy
          </AppButton>
        </div>
      </AppCard>

      <!-- Stats del día -->
      <EntitySummary :items="stats" />

      <!-- Lista de citas del día -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b" style="border-color: var(--border-default)">
          <h2 class="font-semibold" style="color: var(--text-primary)">Citas del día</h2>
        </div>

        <LoadingState v-if="loading">Cargando agenda...</LoadingState>

        <EmptyState
          v-else-if="citasStore.citas.length === 0"
          icon="📅"
          title="Sin citas"
          message="No tienes citas para este día"
        />

        <div v-else class="divide-y" style="border-color: var(--border-default)">
          <div
            v-for="cita in citasStore.citas"
            :key="cita.id"
            class="px-6 py-4 flex flex-col sm:flex-row sm:items-center gap-4 transition-colors"
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
                <p class="font-semibold" style="color: var(--text-primary)">{{ cita.mascotaNombre }}</p>
                <AppBadge variant="neutral">{{ cita.tipoCita?.nombre }}</AppBadge>
              </div>
              <p class="text-sm mt-0.5 truncate" style="color: var(--text-muted)">
                {{ cita.clienteNombre }} · {{ cita.motivo }}
              </p>
            </div>

            <!-- Estado + acciones -->
            <div class="flex items-center gap-2 shrink-0 flex-wrap">
              <StatusBadge :status="cita.estado" />

              <AppButton
                v-if="siguienteEstado(cita.estado)"
                variant="ghost"
                size="sm"
                :loading="cambiandoEstado === cita.id"
                @click="cambiarEstado(cita, siguienteEstado(cita.estado)!)"
              >
                {{ labelSiguienteEstado(cita.estado) }}
              </AppButton>

              <AppButton
                v-if="cita.estado === 'en_curso' || cita.estado === 'completada'"
                size="sm"
                @click="router.push(`/diagnosticos?citaId=${cita.id}`)"
              >
                Diagnóstico
              </AppButton>
            </div>
          </div>
        </div>
      </AppCard>

      <!-- Acceso rápido -->
      <AppCard padding="sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="font-medium" style="color: var(--text-primary)">Mis diagnósticos registrados</p>
            <p class="text-xs mt-0.5" style="color: var(--text-muted)">Historial de consultas atendidas</p>
          </div>
          <AppButton variant="ghost" @click="router.push('/mis-diagnosticos')">Ver todos</AppButton>
        </div>
      </AppCard>
    </div>
  </DashboardLayout>
</template>
