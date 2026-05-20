<script setup lang="ts">
import type { Cita } from '@/types'
import CitaStatusBadge from './CitaStatusBadge.vue'

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
  <div class="bg-white rounded-2xl border border-slate-100 shadow-sm h-full flex flex-col">
    <div class="px-4 py-3 border-b border-slate-100">
      <h3 class="font-semibold text-slate-800 text-sm capitalize">{{ formatFecha(props.fecha) }}</h3>
      <p class="text-xs text-slate-400 mt-0.5">{{ props.citas.length }} cita(s)</p>
    </div>

    <div class="flex-1 overflow-y-auto divide-y divide-slate-100">
      <div
        v-if="props.citas.length === 0"
        class="flex flex-col items-center justify-center py-10 text-slate-400 text-sm"
      >
        <svg class="w-8 h-8 text-slate-300 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        Sin citas para este día
      </div>

      <div
        v-for="cita in props.citas"
        :key="cita.id"
        class="p-4 hover:bg-slate-50 transition-colors cursor-pointer"
        @click="emit('edit', cita)"
      >
        <div class="flex items-start gap-3">
          <div
            class="w-1 h-full min-h-10 rounded-full shrink-0"
            :style="{ backgroundColor: cita.tipoCita.color }"
          />
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between gap-2 mb-1">
              <span class="text-xs font-semibold text-slate-500">
                {{ cita.horaInicio }} - {{ cita.horaFin }}
              </span>
              <CitaStatusBadge :estado="cita.estado" size="sm" />
            </div>
            <p class="font-medium text-slate-800 text-sm truncate">{{ cita.mascotaNombre }}</p>
            <p class="text-xs text-slate-500 truncate">{{ cita.tipoCita.nombre }}</p>
            <p class="text-xs text-slate-400 truncate mt-0.5">{{ cita.medicoNombre }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
