<script setup lang="ts">
interface Props {
  icon?: string
  title?: string
  message?: string
}

withDefaults(defineProps<Props>(), {
  icon: '📭',
  title: 'No hay datos',
  message: 'No se encontraron elementos para mostrar.',
})
</script>

<template>
  <div class="flex flex-col items-center justify-center py-12 gap-3 text-center">
    <span class="text-5xl">{{ icon }}</span>
    <h3 class="text-lg font-semibold" style="color: var(--text-primary)">{{ title }}</h3>
    <p class="text-sm max-w-sm" style="color: var(--text-muted)">{{ message }}</p>
    <slot name="actions" />
  </div>
</template>