<script setup lang="ts">
import AppModal from './AppModal.vue'
import AppButton from './AppButton.vue'

interface Props {
  modelValue: boolean
  title?: string
  message?: string
  confirmLabel?: string
  cancelLabel?: string
  variant?: 'danger' | 'warning' | 'primary'
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '¿Confirmar acción?',
  message: '¿Estás seguro de que deseas continuar?',
  confirmLabel: 'Confirmar',
  cancelLabel: 'Cancelar',
  variant: 'danger',
  loading: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: []
  cancel: []
}>()

function handleConfirm() {
  emit('confirm')
}

function handleCancel() {
  emit('update:modelValue', false)
  emit('cancel')
}
</script>

<template>
  <AppModal :model-value="props.modelValue" :title="props.title" size="sm" @update:model-value="handleCancel">
    <p class="text-slate-600 text-sm">{{ props.message }}</p>
    <template #footer>
      <AppButton variant="ghost" @click="handleCancel" :disabled="props.loading">
        {{ props.cancelLabel }}
      </AppButton>
      <AppButton :variant="props.variant" @click="handleConfirm" :loading="props.loading">
        {{ props.confirmLabel }}
      </AppButton>
    </template>
  </AppModal>
</template>
