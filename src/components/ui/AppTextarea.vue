<script setup lang="ts">
interface Props {
  modelValue?: string
  label?: string
  placeholder?: string
  error?: string
  required?: boolean
  disabled?: boolean
  rows?: number
  id?: string
}

const props = withDefaults(defineProps<Props>(), {
  required: false,
  disabled: false,
  rows: 3,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()
</script>

<template>
  <div class="flex flex-col gap-1">
    <label
      v-if="props.label"
      :for="props.id"
      class="text-sm font-medium"
      style="color: var(--text-secondary)"
    >
      {{ props.label }}
      <span v-if="props.required" class="text-danger-500 ml-0.5" aria-hidden="true">*</span>
    </label>
    <textarea
      :id="props.id"
      :value="props.modelValue"
      :placeholder="props.placeholder"
      :required="props.required"
      :disabled="props.disabled"
      :rows="props.rows"
      :aria-invalid="!!props.error"
      @input="emit('update:modelValue', ($event.target as HTMLTextAreaElement).value)"
      :class="[
        'vg-input w-full px-3 py-2 rounded-lg text-sm transition-colors resize-none',
        'focus:outline-none focus:ring-2 focus:ring-offset-0',
        props.error
          ? 'border-danger-400 focus:border-danger-400 focus:ring-danger-300'
          : 'focus:border-primary-500 focus:ring-primary-200',
      ]"
    />
    <p v-if="props.error" class="text-xs text-danger-500" role="alert">{{ props.error }}</p>
  </div>
</template>
