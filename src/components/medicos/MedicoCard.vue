<script setup lang="ts">
import type { Medico } from '@/types'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppButton from '@/components/ui/AppButton.vue'

interface Props {
  medico: Medico
}

const props = defineProps<Props>()

const emit = defineEmits<{
  edit: [medico: Medico]
}>()

const initials = `${props.medico.nombre[0] ?? ''}${props.medico.apellido[0] ?? ''}`.toUpperCase()

const avatarColors = [
  'bg-primary-100 text-primary-700',
  'bg-secondary-100 text-secondary-700',
  'bg-blue-100 text-blue-700',
  'bg-purple-100 text-purple-700',
]
const colorClass = avatarColors[props.medico.id % avatarColors.length] ?? avatarColors[0]!
</script>

<template>
  <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-4 sm:p-5 hover:shadow-md transition-shadow">

    <!-- Fila superior: avatar + nombre + badge -->
    <div class="flex items-start gap-3">
      <!-- Avatar -->
      <div :class="['w-12 h-12 sm:w-14 sm:h-14 rounded-2xl flex items-center justify-center text-base sm:text-lg font-bold shrink-0', colorClass]">
        {{ initials }}
      </div>

      <!-- Nombre, licencia y badge -->
      <div class="flex-1 min-w-0">
        <div class="flex flex-wrap items-start justify-between gap-x-2 gap-y-1">
          <div class="min-w-0">
            <h3 class="font-semibold text-slate-800 text-sm sm:text-base leading-tight truncate">
              {{ props.medico.nombre }} {{ props.medico.apellido }}
            </h3>
            <p class="text-xs text-slate-400 mt-0.5 truncate">Lic. {{ props.medico.numeroLicencia }}</p>
          </div>
          <AppBadge :variant="props.medico.disponible ? 'success' : 'neutral'" dot class="shrink-0">
            {{ props.medico.disponible ? 'Disponible' : 'No disponible' }}
          </AppBadge>
        </div>
      </div>
    </div>

    <!-- Especialidades -->
    <div class="flex flex-wrap gap-1.5 mt-3">
      <span
        v-for="esp in props.medico.especialidades"
        :key="esp.id"
        class="px-2 py-0.5 bg-primary-50 text-primary-700 text-xs rounded-full font-medium"
      >
        {{ esp.nombre }}
      </span>
    </div>

    <!-- Contacto: columna en móvil, fila en sm+ -->
    <div class="flex flex-col sm:flex-row sm:items-center gap-1.5 sm:gap-4 mt-3 text-xs text-slate-500">
      <span class="flex items-center gap-1 min-w-0">
        <svg class="w-3.5 h-3.5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
        </svg>
        <span class="truncate">{{ props.medico.email }}</span>
      </span>
      <span class="flex items-center gap-1 shrink-0">
        <svg class="w-3.5 h-3.5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
        </svg>
        {{ props.medico.telefono }}
      </span>
    </div>

    <!-- Actions -->
    <div class="flex justify-end mt-4 pt-4 border-t border-slate-100">
      <AppButton variant="outline" size="sm" @click="emit('edit', props.medico)">
        <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
        </svg>
        Editar
      </AppButton>
    </div>
  </div>
</template>
