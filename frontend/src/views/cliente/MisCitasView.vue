<script setup lang="ts">
import { computed, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'

import PageHeader from '@/components/common/PageHeader.vue'
import EntitySummary from '@/components/common/EntitySummary.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import TableViewLayout from '@/components/common/TableViewLayout.vue'

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
  { label: 'Total',      value: misCitas.value.length,
    svgPath: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2',
    iconColor: '#6366f1', iconBg: 'rgba(99,102,241,0.12)' },
  { label: 'Próximas',   value: misCitas.value.filter((c) => c.estado === 'pendiente' || c.estado === 'confirmada').length,
    svgPath: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z',
    iconColor: '#f59e0b', iconBg: 'rgba(245,158,11,0.12)' },
  { label: 'Completadas', value: misCitas.value.filter((c) => c.estado === 'completada').length,
    svgPath: 'M5 13l4 4L19 7',
    iconColor: '#059669', iconBg: 'rgba(5,150,105,0.12)' },
  { label: 'Canceladas', value: misCitas.value.filter((c) => c.estado === 'cancelada').length,
    svgPath: 'M6 18L18 6M6 6l12 12',
    iconColor: '#ef4444', iconBg: 'rgba(239,68,68,0.12)' },
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

    <TableViewLayout>
      <template #toolbar>
        <EntitySummary :items="summaryItems" />
      </template>

      <template #content>
        <AppCard fill-height padding="none">
          <template #header>
            <div class="px-6 py-4 border-b shrink-0" style="border-color: var(--border-color)">
              <h2 class="font-semibold" style="color: var(--text-primary)">{{ misCitas.length }} cita(s)</h2>
            </div>
          </template>

          <div class="divide-y" style="border-color: var(--border-color)">
            <EmptyState v-if="misCitas.length === 0" title="Sin citas" message="No tienes citas registradas" />
            <div v-for="cita in misCitas" :key="cita.id" class="px-4 sm:px-6 py-4 vg-table-row-hover transition-colors">
              <div class="flex gap-3">
                <div class="w-1 rounded-full shrink-0 self-stretch" :style="{ backgroundColor: cita.tipoCita.color }" />
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
                  <div class="flex"><StatusBadge :status="cita.estado" /></div>
                </div>
              </div>
            </div>
          </div>
        </AppCard>
      </template>
    </TableViewLayout>
  </DashboardLayout>
</template>