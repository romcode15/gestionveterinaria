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
    <div class="w-12 h-12 rounded-full flex items-center justify-center bg-red-50 dark:bg-red-950">
      <svg class="w-6 h-6 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
      </svg>
    </div>
    <h3 class="text-lg font-semibold" style="color: var(--text-primary)">Error</h3>
    <p class="text-sm max-w-sm" style="color: var(--text-muted)">{{ error }}</p>
    <AppButton v-if="showRetry" variant="primary" @click="emit('retry')">
      Reintentar
    </AppButton>
  </div>
</template>
