<script setup lang="ts">
import { ref, computed } from 'vue'
import type { Cita } from '@/types'

interface Props {
  citas: Cita[]
  fechaSeleccionada: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:fechaSeleccionada': [fecha: string]
}>()

const hoy = new Date()
const mesActual = ref(new Date(hoy.getFullYear(), hoy.getMonth(), 1))

const nombresMes = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
]

const diasSemana = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb']

const diasCalendario = computed(() => {
  const año = mesActual.value.getFullYear()
  const mes = mesActual.value.getMonth()
  const primerDia = new Date(año, mes, 1).getDay()
  const ultimoDia = new Date(año, mes + 1, 0).getDate()

  const dias: Array<{ fecha: string; dia: number; esHoy: boolean; esMesActual: boolean } | null> = []

  // Días vacíos al inicio
  for (let i = 0; i < primerDia; i++) {
    dias.push(null)
  }

  for (let d = 1; d <= ultimoDia; d++) {
    const fecha = `${año}-${String(mes + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    const esHoy =
      d === hoy.getDate() && mes === hoy.getMonth() && año === hoy.getFullYear()
    dias.push({ fecha, dia: d, esHoy, esMesActual: true })
  }

  return dias
})

function citasPorFecha(fecha: string): Cita[] {
  return props.citas.filter((c) => c.fecha === fecha)
}

function mesAnterior() {
  mesActual.value = new Date(mesActual.value.getFullYear(), mesActual.value.getMonth() - 1, 1)
}

function mesSiguiente() {
  mesActual.value = new Date(mesActual.value.getFullYear(), mesActual.value.getMonth() + 1, 1)
}

function seleccionarFecha(fecha: string) {
  emit('update:fechaSeleccionada', fecha)
}
</script>

<template>
  <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-4">
    <!-- Header del calendario -->
    <div class="flex items-center justify-between mb-4">
      <button
        @click="mesAnterior"
        class="p-2 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition-colors"
        aria-label="Mes anterior"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </button>
      <h3 class="font-semibold text-slate-800">
        {{ nombresMes[mesActual.getMonth()] }} {{ mesActual.getFullYear() }}
      </h3>
      <button
        @click="mesSiguiente"
        class="p-2 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition-colors"
        aria-label="Mes siguiente"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>
    </div>

    <!-- Días de la semana -->
    <div class="grid grid-cols-7 mb-2">
      <div
        v-for="dia in diasSemana"
        :key="dia"
        class="text-center text-xs font-semibold text-slate-400 py-1"
      >
        {{ dia }}
      </div>
    </div>

    <!-- Días del mes -->
    <div class="grid grid-cols-7 gap-1">
      <div v-for="(dia, idx) in diasCalendario" :key="idx">
        <button
          v-if="dia"
          @click="seleccionarFecha(dia.fecha)"
          :class="[
            'w-full aspect-square flex flex-col items-center justify-center rounded-xl text-sm transition-all relative',
            dia.fecha === props.fechaSeleccionada
              ? 'bg-primary-600 text-white font-semibold shadow-sm'
              : dia.esHoy
              ? 'bg-primary-50 text-primary-700 font-semibold'
              : 'text-slate-700 hover:bg-slate-100',
          ]"
          :aria-label="`${dia.dia} - ${citasPorFecha(dia.fecha).length} citas`"
          :aria-pressed="dia.fecha === props.fechaSeleccionada"
        >
          {{ dia.dia }}
          <!-- Indicador de citas -->
          <div
            v-if="citasPorFecha(dia.fecha).length > 0"
            :class="[
              'absolute bottom-1 w-1 h-1 rounded-full',
              dia.fecha === props.fechaSeleccionada ? 'bg-white' : 'bg-primary-500',
            ]"
          />
        </button>
        <div v-else />
      </div>
    </div>
  </div>
</template>
