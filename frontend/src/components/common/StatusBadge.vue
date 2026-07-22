<script setup lang="ts">
import AppBadge from '@/components/ui/AppBadge.vue'

type BadgeVariant = 'success' | 'warning' | 'danger' | 'info' | 'neutral' | 'primary'

// Mapa por defecto para estados de citas (puedes ampliarlo)
const defaultConfig: Record<string, { variant: BadgeVariant; label: string }> = {
  pendiente: { variant: 'warning', label: 'Pendiente' },
  confirmada: { variant: 'info', label: 'Confirmada' },
  en_curso: { variant: 'primary', label: 'En curso' },
  completada: { variant: 'success', label: 'Completada' },
  cancelada: { variant: 'danger', label: 'Cancelada' },
  no_asistio: { variant: 'neutral', label: 'No asistió' },
  activo: { variant: 'success', label: 'Activo' },
  inactivo: { variant: 'neutral', label: 'Inactivo' },
}

interface Props {
  status: string
  size?: 'sm' | 'md'
  // Permite sobrescribir el mapa o añadir estados personalizados
  config?: Record<string, { variant: BadgeVariant; label: string }>
}

const props = withDefaults(defineProps<Props>(), {
  size: 'md',
  config: () => defaultConfig,
})

// Usamos el config combinado (el del prop tiene prioridad)
const finalConfig = { ...defaultConfig, ...props.config }
</script>

<template>
  <AppBadge
    :variant="finalConfig[status]?.variant || 'neutral'"
    :size="size"
    dot
  >
    {{ finalConfig[status]?.label || status }}
  </AppBadge>
</template>