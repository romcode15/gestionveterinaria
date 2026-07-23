<script lang="ts">
// Mapa por defecto para estados (ahora en ámbito de módulo)
const defaultConfig: Record<string, { variant: BadgeVariant; label: string }> = {
  pendiente:   { variant: 'warning', label: 'Pendiente' },
  confirmada:  { variant: 'info',    label: 'Confirmada' },
  en_curso:    { variant: 'primary', label: 'En curso' },
  completada:  { variant: 'success', label: 'Completada' },
  cancelada:   { variant: 'danger',  label: 'Cancelada' },
  no_asistio:  { variant: 'neutral', label: 'No asistió' },
  activo:      { variant: 'success', label: 'Activo' },
  inactivo:    { variant: 'neutral', label: 'Inactivo' },
}
</script>

<script setup lang="ts">
import AppBadge from '@/components/ui/AppBadge.vue'

type BadgeVariant = 'success' | 'warning' | 'danger' | 'info' | 'neutral' | 'primary'

interface Props {
  status: string
  size?: 'sm' | 'md'
  // Permite sobrescribir el mapa o añadir estados personalizados
  config?: Record<string, { variant: BadgeVariant; label: string }>
}

const props = withDefaults(defineProps<Props>(), {
  size: 'md',
  config: () => defaultConfig, // ✅ ahora referencia a la variable del módulo
})

// Combinar configuraciones (el prop tiene prioridad)
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