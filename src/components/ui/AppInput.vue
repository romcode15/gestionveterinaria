<script setup lang="ts">
interface Props {
  modelValue?: string | number
  label?: string
  placeholder?: string
  type?: string
  error?: string
  hint?: string
  required?: boolean
  disabled?: boolean
  id?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  required: false,
  disabled: false,
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

    <input
      :id="props.id"
      :type="props.type"
      :value="props.modelValue"
      :placeholder="props.placeholder"
      :required="props.required"
      :disabled="props.disabled"
      :aria-describedby="props.error ? `${props.id}-error` : props.hint ? `${props.id}-hint` : undefined"
      :aria-invalid="!!props.error"
      @input="emit('update:modelValue', ($event.target as HTMLInputElement).value)"
      :class="[
        'vg-input w-full px-3 py-2 rounded-lg text-sm transition-colors',
        'focus:outline-none focus:ring-2 focus:ring-offset-0',
        props.error
          ? 'border-danger-400 focus:border-danger-400 focus:ring-danger-300'
          : 'focus:border-primary-500 focus:ring-primary-200',
      ]"
    />

    <p v-if="props.error" :id="`${props.id}-error`" class="text-xs text-danger-500" role="alert">
      {{ props.error }}
    </p>
    <p v-else-if="props.hint" :id="`${props.id}-hint`" class="text-xs" style="color: var(--text-muted)">
      {{ props.hint }}
    </p>
  </div>
</template>
