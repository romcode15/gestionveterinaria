<script setup lang="ts">
import type { Cita } from '@/types'
import StatusBadge from '@/components/common/StatusBadge.vue'
import EmptyState from '@/components/common/EmptyState.vue'

interface Props {
  citas: Cita[]
  fecha: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  edit: [cita: Cita]
  cambiarEstado: [cita: Cita, estado: string]
}>()

function formatFecha(fecha: string): string {
  return new Date(fecha + 'T00:00:00').toLocaleDateString('es-CO', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}
</script>

<template>
  <div class="vg-card rounded-2xl h-full flex flex-col">
    <div class="px-4 py-3" style="border-bottom: 1px solid var(--border-default)">
      <h3 class="font-semibold text-sm capitalize" style="color: var(--text-primary)">
        {{ formatFecha(props.fecha) }}
      </h3>
      <p class="text-xs mt-0.5" style="color: var(--text-muted)">{{ props.citas.length }} cita(s)</p>
    </div>

    <div class="flex-1 overflow-y-auto" style="divide-color: var(--border-default)">
      <!-- Usar EmptyState -->
      <EmptyState
        v-if="props.citas.length === 0"
        title="Sin citas"
        message="No hay citas programadas para este día"
      />

      <!-- Lista de citas -->
      <div
        v-for="cita in props.citas"
        :key="cita.id"
        class="p-4 vg-table-row-hover transition-colors cursor-pointer vg-table-divider"
        @click="emit('edit', cita)"
      >
        <div class="flex items-start gap-3">
          <div
            class="w-1 h-full min-h-10 rounded-full shrink-0"
            :style="{ backgroundColor: cita.tipoCita.color }"
          />
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between gap-2 mb-1">
              <span class="text-xs font-semibold" style="color: var(--text-muted)">
                {{ cita.horaInicio }} - {{ cita.horaFin }}
              </span>
              <StatusBadge :status="cita.estado" size="sm" />
            </div>
            <p class="font-medium text-sm truncate" style="color: var(--text-primary)">{{ cita.mascotaNombre }}</p>
            <p class="text-xs truncate" style="color: var(--text-secondary)">{{ cita.tipoCita.nombre }}</p>
            <p class="text-xs truncate mt-0.5" style="color: var(--text-muted)">{{ cita.medicoNombre }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>