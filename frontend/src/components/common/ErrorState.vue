<script setup lang="ts">
interface Props {
  error?: string
  showRetry?: boolean
}

withDefaults(defineProps<Props>(), {
  error: 'Ocurrió un error inesperado',
  showRetry: true,
})

const emit = defineEmits<{
  retry: []
}>()
</script>

<template>
  <div class="flex flex-col items-center justify-center py-12 gap-3 text-center">
    <span class="text-5xl">⚠️</span>
    <h3 class="text-lg font-semibold" style="color: var(--text-primary)">Error</h3>
    <p class="text-sm max-w-sm" style="color: var(--text-muted)">{{ error }}</p>
    <button
      v-if="showRetry"
      @click="emit('retry')"
      class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
      style="background-color: var(--color-primary); color: white;"
    >
      Reintentar
    </button>
  </div>
</template>