<script setup lang="ts">
interface Props {
  modelValue: boolean
  label?: string
  ariaLabel?: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()
</script>

<template>
  <div class="flex items-center gap-3">
    <button
      type="button"
      @click="emit('update:modelValue', !props.modelValue)"
      :class="[
        'relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-1',
        props.modelValue ? 'bg-primary-600' : 'bg-slate-300',
      ]"
      :aria-checked="props.modelValue"
      :aria-label="props.ariaLabel ?? props.label"
      role="switch"
    >
      <span
        :class="[
          'inline-block h-4 w-4 transform rounded-full bg-white shadow transition-transform',
          props.modelValue ? 'translate-x-6' : 'translate-x-1',
        ]"
      />
    </button>
    <span v-if="props.label" class="text-sm text-slate-700">{{ props.label }}</span>
  </div>
</template>
