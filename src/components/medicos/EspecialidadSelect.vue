<script setup lang="ts">
import type { Especialidad } from '@/types'

interface Props {
  modelValue: number[]
  especialidades: Especialidad[]
  error?: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: number[]]
}>()

function toggle(id: number) {
  const current = [...props.modelValue]
  const idx = current.indexOf(id)
  if (idx === -1) {
    current.push(id)
  } else {
    current.splice(idx, 1)
  }
  emit('update:modelValue', current)
}

function isSelected(id: number): boolean {
  return props.modelValue.includes(id)
}
</script>

<template>
  <div class="flex flex-col gap-1">
    <label class="text-sm font-medium text-slate-700">
      Especialidades
      <span class="text-danger-500 ml-0.5" aria-hidden="true">*</span>
    </label>
    <div class="flex flex-wrap gap-2 p-3 border border-slate-300 rounded-lg min-h-15">
      <button
        v-for="esp in props.especialidades"
        :key="esp.id"
        type="button"
        @click="toggle(esp.id)"
        :class="[
          'px-3 py-1.5 rounded-full text-xs font-medium transition-all',
          isSelected(esp.id)
            ? 'bg-primary-600 text-white shadow-sm'
            : 'bg-slate-100 text-slate-600 hover:bg-primary-50 hover:text-primary-700',
        ]"
        :aria-pressed="isSelected(esp.id)"
      >
        {{ esp.nombre }}
      </button>
    </div>
    <p v-if="props.error" class="text-xs text-danger-600" role="alert">{{ props.error }}</p>
    <p v-else class="text-xs text-slate-400">Selecciona una o más especialidades</p>
  </div>
</template>
