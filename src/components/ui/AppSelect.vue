<script setup lang="ts">
interface Option {
  value: string | number
  label: string
  disabled?: boolean
}

interface Props {
  modelValue?: string | number
  label?: string
  options: Option[]
  placeholder?: string
  error?: string
  required?: boolean
  disabled?: boolean
  id?: string
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: 'Seleccionar...',
  required: false,
  disabled: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
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

    <select
      :id="props.id"
      :value="props.modelValue"
      :required="props.required"
      :disabled="props.disabled"
      :aria-invalid="!!props.error"
      @change="emit('update:modelValue', ($event.target as HTMLSelectElement).value)"
      :class="[
        'vg-input w-full px-3 py-2 rounded-lg text-sm transition-colors appearance-none',
        'focus:outline-none focus:ring-2 focus:ring-offset-0',
        props.error
          ? 'border-danger-400 focus:border-danger-400 focus:ring-danger-300'
          : 'focus:border-primary-500 focus:ring-primary-200',
      ]"
    >
      <option value="" disabled :selected="!props.modelValue">{{ props.placeholder }}</option>
      <option
        v-for="opt in props.options"
        :key="opt.value"
        :value="opt.value"
        :disabled="opt.disabled"
      >
        {{ opt.label }}
      </option>
    </select>

    <p v-if="props.error" class="text-xs text-danger-500" role="alert">{{ props.error }}</p>
  </div>
</template>
