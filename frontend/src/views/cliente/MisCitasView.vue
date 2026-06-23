<script setup lang="ts">
import { computed, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import CitaStatusBadge from '@/components/citas/CitaStatusBadge.vue'
import { useAuthStore } from '@/stores/auth.store'
import { useCitasStore } from '@/stores/citas.store'

const authStore = useAuthStore()
const citasStore = useCitasStore()

onMounted(async () => {
  const clienteId = authStore.clienteId
  if (clienteId) await citasStore.cargarMisCitas(clienteId)
})

// Ya se cargaron solo las citas del cliente, ordenadas por fecha y hora
const misCitas = computed(() =>
  [...citasStore.citas].sort((a, b) => {
    if (a.fecha !== b.fecha) return a.fecha.localeCompare(b.fecha)
    return a.horaInicio.localeCompare(b.horaInicio)
  }),
)

function formatFecha(fecha: string): string {
  return new Date(fecha + 'T00:00:00').toLocaleDateString('es-CO', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <div>
        <h1 class="text-lg font-semibold text-slate-800">Mis citas</h1>
        <p class="text-xs text-slate-500">Historial de citas veterinarias</p>
      </div>
    </template>

    <div class="space-y-4">
      <!-- Stats -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <AppCard padding="sm" class="text-center">
          <p class="text-xl font-bold text-slate-800">{{ misCitas.length }}</p>
          <p class="text-xs text-slate-500">Total</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-xl font-bold text-accent-600">
            {{ misCitas.filter((c) => c.estado === 'pendiente' || c.estado === 'confirmada').length }}
          </p>
          <p class="text-xs text-slate-500">Próximas</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-xl font-bold text-primary-600">
            {{ misCitas.filter((c) => c.estado === 'completada').length }}
          </p>
          <p class="text-xs text-slate-500">Completadas</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-xl font-bold text-danger-500">
            {{ misCitas.filter((c) => c.estado === 'cancelada').length }}
          </p>
          <p class="text-xs text-slate-500">Canceladas</p>
        </AppCard>
      </div>

      <!-- Lista -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b border-slate-100">
          <h2 class="font-semibold text-slate-800">{{ misCitas.length }} cita(s)</h2>
        </div>

        <div class="divide-y divide-slate-100">
          <div v-if="misCitas.length === 0" class="px-6 py-12 text-center text-slate-400">
            <svg class="w-10 h-10 text-slate-300 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            No tienes citas registradas
          </div>

          <div
            v-for="cita in misCitas"
            :key="cita.id"
            class="px-4 sm:px-6 py-4 hover:bg-slate-50 transition-colors"
          >
            <div class="flex gap-3">
              <!-- Barra de color -->
              <div
                class="w-1 rounded-full shrink-0 self-stretch"
                :style="{ backgroundColor: cita.tipoCita.color }"
              />

              <div class="flex-1 min-w-0 flex flex-col gap-2">
                <!-- Fila 1: fecha + info -->
                <div class="flex items-start gap-3">
                  <div class="text-center shrink-0 w-16 sm:w-20">
                    <p class="text-xs font-semibold text-slate-500 capitalize leading-tight">
                      {{ formatFecha(cita.fecha) }}
                    </p>
                    <p class="text-sm font-bold text-slate-800">{{ cita.horaInicio }}</p>
                    <p class="text-xs text-slate-400">{{ cita.horaFin }}</p>
                  </div>

                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2 flex-wrap">
                      <p class="font-semibold text-slate-800 text-sm">{{ cita.mascotaNombre }}</p>
                      <AppBadge variant="neutral" size="sm">{{ cita.tipoCita.nombre }}</AppBadge>
                    </div>
                    <p class="text-xs text-slate-500 truncate mt-0.5">{{ cita.motivo }}</p>
                    <p class="text-xs text-slate-400 truncate mt-0.5">{{ cita.medicoNombre }}</p>
                  </div>
                </div>

                <!-- Fila 2: estado -->
                <div class="flex">
                  <CitaStatusBadge :estado="cita.estado" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </AppCard>
    </div>
  </DashboardLayout>
</template>
