<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'

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
    <span class="text-5xl" aria-hidden="true">⚠️</span>
    <h3 class="text-lg font-semibold" style="color: var(--text-primary)">Error</h3>
    <p class="text-sm max-w-sm" style="color: var(--text-muted)">{{ error }}</p>
    <AppButton v-if="showRetry" variant="primary" @click="emit('retry')">
      Reintentar
    </AppButton>
  </div>
</template>
