<script setup lang="ts">
import { computed, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'

import PageHeader from '@/components/common/PageHeader.vue'
import EntitySummary from '@/components/common/EntitySummary.vue'
import EmptyState from '@/components/common/EmptyState.vue'

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

const summaryItems = computed(() => [
  { label: 'Total', value: misCitas.value.length, icon: '📋' },
  { label: 'Próximas', value: misCitas.value.filter((c) => c.estado === 'pendiente' || c.estado === 'confirmada').length, icon: '⏳' },
  { label: 'Completadas', value: misCitas.value.filter((c) => c.estado === 'completada').length, icon: '✅' },
  { label: 'Canceladas', value: misCitas.value.filter((c) => c.estado === 'cancelada').length, icon: '❌' },
])

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
      <PageHeader title="Mis citas" subtitle="Historial de citas veterinarias" />
    </template>

    <div class="space-y-4">
      <!-- Stats con EntitySummary -->
      <EntitySummary :items="summaryItems" :columns="4" />

      <!-- Lista -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">{{ misCitas.length }} cita(s)</h2>
        </div>

        <div class="divide-y" style="border-color: var(--border-color)">
          <EmptyState
            v-if="misCitas.length === 0"
            icon="📅"
            title="Sin citas"
            message="No tienes citas registradas"
          />

          <div
            v-for="cita in misCitas"
            :key="cita.id"
            class="px-4 sm:px-6 py-4 vg-table-row-hover transition-colors"
          >
            <div class="flex gap-3">
              <div
                class="w-1 rounded-full shrink-0 self-stretch"
                :style="{ backgroundColor: cita.tipoCita.color }"
              />

              <div class="flex-1 min-w-0 flex flex-col gap-2">
                <div class="flex items-start gap-3">
                  <div class="text-center shrink-0 w-16 sm:w-20">
                    <p class="text-xs font-semibold capitalize leading-tight" style="color: var(--text-muted)">
                      {{ formatFecha(cita.fecha) }}
                    </p>
                    <p class="text-sm font-bold" style="color: var(--text-primary)">{{ cita.horaInicio }}</p>
                    <p class="text-xs" style="color: var(--text-muted)">{{ cita.horaFin }}</p>
                  </div>

                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2 flex-wrap">
                      <p class="font-semibold text-sm" style="color: var(--text-primary)">{{ cita.mascotaNombre }}</p>
                      <AppBadge variant="neutral" size="sm">{{ cita.tipoCita.nombre }}</AppBadge>
                    </div>
                    <p class="text-xs truncate mt-0.5" style="color: var(--text-secondary)">{{ cita.motivo }}</p>
                    <p class="text-xs truncate mt-0.5" style="color: var(--text-muted)">{{ cita.medicoNombre }}</p>
                  </div>
                </div>

                <div class="flex">
                  <StatusBadge :estado="cita.estado" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </AppCard>
    </div>
  </DashboardLayout>
</template>