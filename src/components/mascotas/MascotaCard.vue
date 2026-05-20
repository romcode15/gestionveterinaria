<script setup lang="ts">
import type { Mascota } from '@/types'
import AppBadge from '@/components/ui/AppBadge.vue'
import MascotaAvatar from './MascotaAvatar.vue'

interface Props {
  mascota: Mascota
}

const props = defineProps<Props>()

const emit = defineEmits<{
  edit: [mascota: Mascota]
  view: [mascota: Mascota]
}>()

function calcularEdad(fechaNacimiento?: string): string {
  if (!fechaNacimiento) return 'Edad desconocida'
  const hoy = new Date()
  const nac = new Date(fechaNacimiento)
  const años = hoy.getFullYear() - nac.getFullYear()
  const meses = hoy.getMonth() - nac.getMonth()
  if (años === 0) return `${meses + (meses < 0 ? 12 : 0)} meses`
  return `${años} año${años !== 1 ? 's' : ''}`
}
</script>

<template>
  <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-4 sm:p-5 hover:shadow-md transition-shadow">
    <div class="flex items-start gap-3 sm:gap-4">
      <MascotaAvatar :especie-nombre="props.mascota.especie.nombre" :nombre="props.mascota.nombre" />

      <div class="flex-1 min-w-0">
        <!-- Nombre + badge: wrap en móvil -->
        <div class="flex flex-wrap items-start justify-between gap-x-2 gap-y-1">
          <div class="min-w-0">
            <h3 class="font-semibold text-slate-800 truncate">{{ props.mascota.nombre }}</h3>
            <p class="text-xs text-slate-500 mt-0.5 truncate">
              {{ props.mascota.especie.nombre }} · {{ props.mascota.raza.nombre }}
            </p>
          </div>
          <AppBadge :variant="props.mascota.sexo === 'macho' ? 'info' : 'warning'" class="shrink-0">
            {{ props.mascota.sexo === 'macho' ? '♂ Macho' : '♀ Hembra' }}
          </AppBadge>
        </div>

        <!-- Datos clínicos en grid 2 columnas -->
        <div class="grid grid-cols-2 gap-x-3 gap-y-1 mt-3 text-xs text-slate-500">
          <span class="truncate">🎂 {{ calcularEdad(props.mascota.fechaNacimiento) }}</span>
          <span v-if="props.mascota.peso" class="truncate">⚖️ {{ props.mascota.peso }} kg</span>
          <span v-if="props.mascota.color" class="truncate">🎨 {{ props.mascota.color }}</span>
          <span class="truncate">{{ props.mascota.esterilizado ? '✅ Esterilizado' : '❌ No esterilizado' }}</span>
        </div>

        <!-- Footer: propietario + botón editar -->
        <div class="mt-3 pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
          <div class="flex items-center gap-1.5 text-xs text-slate-500 min-w-0">
            <svg class="w-3.5 h-3.5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
            <span class="truncate">{{ props.mascota.clienteNombre }}</span>
          </div>
          <button
            @click="emit('edit', props.mascota)"
            class="p-1.5 rounded-lg text-slate-400 hover:text-primary-600 hover:bg-primary-50 transition-colors shrink-0"
            title="Editar"
            aria-label="Editar mascota"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
