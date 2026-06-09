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

// Paleta de avatares que funciona en light y dark
const avatarPalette = [
  { bg: 'rgba(16,185,129,0.15)',  color: '#059669' },
  { bg: 'rgba(20,184,166,0.15)',  color: '#0d9488' },
  { bg: 'rgba(59,130,246,0.15)',  color: '#2563eb' },
  { bg: 'rgba(168,85,247,0.15)',  color: '#7c3aed' },
]
const palette = avatarPalette[props.medico.id % avatarPalette.length]!
</script>

<template>
  <div class="vg-card rounded-2xl p-4 sm:p-5 hover:shadow-md transition-shadow">

    <!-- Avatar + nombre + badge -->
    <div class="flex items-start gap-3">
      <div
        class="w-12 h-12 sm:w-14 sm:h-14 rounded-2xl flex items-center justify-center text-base sm:text-lg font-bold shrink-0"
        :style="{ backgroundColor: palette.bg, color: palette.color }"
      >
        {{ initials }}
      </div>

      <div class="flex-1 min-w-0">
        <div class="flex flex-wrap items-start justify-between gap-x-2 gap-y-1">
          <div class="min-w-0">
            <h3 class="font-semibold text-sm sm:text-base leading-tight truncate" style="color: var(--text-primary)">
              {{ props.medico.nombre }} {{ props.medico.apellido }}
            </h3>
            <p class="text-xs mt-0.5 truncate" style="color: var(--text-muted)">Lic. {{ props.medico.numeroLicencia }}</p>
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
        class="px-2 py-0.5 text-xs rounded-full font-medium vg-esp-tag"
      >
        {{ esp.nombre }}
      </span>
    </div>

    <!-- Contacto -->
    <div class="flex flex-col sm:flex-row sm:items-center gap-1.5 sm:gap-4 mt-3 text-xs" style="color: var(--text-muted)">
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
    <div
      class="flex justify-end mt-4 pt-4"
      style="border-top: 1px solid var(--border-default)"
    >
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

<style>
.vg-esp-tag {
  background-color: rgba(16, 185, 129, 0.12);
  color: #059669;
}
[data-theme="dark"] .vg-esp-tag {
  background-color: rgba(52, 211, 153, 0.15);
  color: #34d399;
}
</style>
