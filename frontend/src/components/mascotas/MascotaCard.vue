<script setup lang="ts">
import { computed } from 'vue'
import type { Mascota } from '@/types'
import MascotaAvatar from '@/components/mascotas/MascotaAvatar.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppButton from '@/components/ui/AppButton.vue'

interface Props {
  mascota: Mascota
  readonly?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  readonly: false,
})

const emit = defineEmits<{
  edit: [mascota: Mascota]
  viewHistorial: [mascota: Mascota]
}>()

// Calcular edad aproximada
const edad = computed(() => {
  if (!props.mascota.fechaNacimiento) return null
  const hoy = new Date()
  const nac = new Date(props.mascota.fechaNacimiento + 'T00:00:00')
  const meses = (hoy.getFullYear() - nac.getFullYear()) * 12 + (hoy.getMonth() - nac.getMonth())
  if (meses < 12) return `${meses} mes${meses !== 1 ? 'es' : ''}`
  const años = Math.floor(meses / 12)
  return `${años} año${años !== 1 ? 's' : ''}`
})

const sexoLabel = computed(() =>
  props.mascota.sexo === 'macho' ? '♂ Macho' : '♀ Hembra',
)
</script>

<template>
  <div
    class="flex items-center gap-4 p-4 rounded-xl border transition-colors"
    style="border-color: var(--border-color); background-color: var(--bg-surface)"
  >
    <!-- Avatar -->
    <MascotaAvatar
      :especie-nombre="mascota.especie?.nombre ?? ''"
      :nombre="mascota.nombre"
      size="md"
    />

    <!-- Info -->
    <div class="flex-1 min-w-0">
      <div class="flex items-center gap-2 flex-wrap">
        <p class="font-semibold truncate" style="color: var(--text-primary)">
          {{ mascota.nombre }}
        </p>
        <AppBadge
          :variant="mascota.estado === 'activo' ? 'success' : 'neutral'"
          dot
          size="sm"
        >
          {{ mascota.estado }}
        </AppBadge>
      </div>

      <p class="text-sm mt-0.5" style="color: var(--text-secondary)">
        {{ mascota.raza?.nombre ?? mascota.especie?.nombre }}
        <span class="mx-1 opacity-40">·</span>
        {{ sexoLabel }}
        <span v-if="edad" class="mx-1 opacity-40">·</span>
        <span v-if="edad">{{ edad }}</span>
      </p>

      <div class="flex items-center gap-3 mt-1 text-xs" style="color: var(--text-muted)">
        <span v-if="mascota.peso">{{ mascota.peso }} kg</span>
        <span v-if="mascota.color">{{ mascota.color }}</span>
        <span v-if="mascota.esterilizado" class="text-emerald-500">✓ Esterilizado</span>
      </div>
    </div>

    <!-- Acciones -->
    <div v-if="!readonly" class="flex items-center gap-1 shrink-0">
      <AppButton
        variant="ghost"
        size="sm"
        title="Ver historial"
        @click="emit('viewHistorial', mascota)"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
      </AppButton>
      <AppButton
        variant="ghost"
        size="sm"
        title="Editar"
        @click="emit('edit', mascota)"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
        </svg>
      </AppButton>
    </div>
  </div>
</template>
