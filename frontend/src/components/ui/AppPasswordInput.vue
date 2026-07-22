<script setup lang="ts">
import { ref } from 'vue'

interface Props {
  modelValue?: string
  label?: string
  placeholder?: string
  error?: string
  required?: boolean
  disabled?: boolean
  id?: string
  autocomplete?: string
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: 'Ingresa tu contraseña',
  required: false,
  disabled: false,
  autocomplete: 'current-password',
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const showPassword = ref(false)
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

    <div class="relative">
      <input
        :id="props.id"
        :type="showPassword ? 'text' : 'password'"
        :value="props.modelValue"
        :placeholder="props.placeholder"
        :required="props.required"
        :disabled="props.disabled"
        :autocomplete="props.autocomplete"
        :aria-invalid="!!props.error"
        @input="emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        :class="[
          'vg-input w-full px-3 py-2 pr-10 rounded-lg text-sm transition-colors',
          'focus:outline-none focus:ring-2 focus:ring-offset-0',
          props.error
            ? 'border-danger-400 focus:border-danger-400 focus:ring-danger-300'
            : 'focus:border-primary-500 focus:ring-primary-200',
        ]"
      />
      <button
        type="button"
        @click="showPassword = !showPassword"
        class="absolute right-3 top-1/2 -translate-y-1/2 transition-colors hover:opacity-70"
        style="color: var(--text-muted)"
        :aria-label="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
        tabindex="-1"
      >
        <!-- Ojo abierto (password oculto) -->
        <svg v-if="!showPassword" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
        </svg>
        <!-- Ojo tachado (password visible) -->
        <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
        </svg>
      </button>
    </div>

    <p v-if="props.error" class="text-xs text-danger-500" role="alert">{{ props.error }}</p>
  </div>
</template>
