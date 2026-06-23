<script setup lang="ts">
import type { EstadoCita } from '@/types'
import AppBadge from '@/components/ui/AppBadge.vue'

interface Props {
  estado: EstadoCita
  size?: 'sm' | 'md'
}

const props = withDefaults(defineProps<Props>(), { size: 'md' })

type BadgeVariant = 'success' | 'warning' | 'danger' | 'info' | 'neutral' | 'primary'

const config: Record<EstadoCita, { variant: BadgeVariant; label: string }> = {
  pendiente: { variant: 'warning', label: 'Pendiente' },
  confirmada: { variant: 'info', label: 'Confirmada' },
  en_curso: { variant: 'primary', label: 'En curso' },
  completada: { variant: 'success', label: 'Completada' },
  cancelada: { variant: 'danger', label: 'Cancelada' },
  no_asistio: { variant: 'neutral', label: 'No asistió' },
}
</script>

<template>
  <AppBadge :variant="config[props.estado].variant" :size="props.size" dot>
    {{ config[props.estado].label }}
  </AppBadge>
</template>
