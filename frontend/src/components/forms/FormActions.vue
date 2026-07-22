<script setup lang="ts">
import AppButton from '@/components/ui/AppButton.vue'

interface Props {
  loading?: boolean
  submitLabel?: string
  cancelLabel?: string
}

withDefaults(defineProps<Props>(), {
  loading: false,
  submitLabel: 'Guardar',
  cancelLabel: 'Cancelar',
})

const emit = defineEmits<{
  cancel: []
}>()
</script>

<template>
  <div class="flex justify-end gap-3 pt-2">
    <AppButton type="button" variant="ghost" @click="emit('cancel')" :disabled="loading">
      {{ cancelLabel }}
    </AppButton>
    <AppButton type="submit" :loading="loading">
      {{ submitLabel }}
    </AppButton>
  </div>
</template>
