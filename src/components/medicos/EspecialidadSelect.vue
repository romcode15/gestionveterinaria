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
  if (idx === -1) current.push(id)
  else current.splice(idx, 1)
  emit('update:modelValue', current)
}

function isSelected(id: number): boolean {
  return props.modelValue.includes(id)
}
</script>

<template>
  <div class="flex flex-col gap-1">
    <label class="text-sm font-medium" style="color: var(--text-secondary)">
      Especialidades
      <span class="text-danger-500 ml-0.5" aria-hidden="true">*</span>
    </label>
    <div
      class="flex flex-wrap gap-2 p-3 rounded-lg min-h-[3.75rem]"
      style="border: 1px solid var(--border-strong)"
    >
      <button
        v-for="esp in props.especialidades"
        :key="esp.id"
        type="button"
        @click="toggle(esp.id)"
        :class="[
          'px-3 py-1.5 rounded-full text-xs font-medium transition-all',
          isSelected(esp.id)
            ? 'bg-primary-600 text-white shadow-sm'
            : 'vg-esp-chip',
        ]"
        :aria-pressed="isSelected(esp.id)"
      >
        {{ esp.nombre }}
      </button>
    </div>
    <p v-if="props.error" class="text-xs text-danger-500" role="alert">{{ props.error }}</p>
    <p v-else class="text-xs" style="color: var(--text-muted)">Selecciona una o más especialidades</p>
  </div>
</template>

<style>
.vg-esp-chip {
  background-color: var(--bg-surface-2);
  color: var(--text-secondary);
}
.vg-esp-chip:hover {
  background-color: rgba(16, 185, 129, 0.12);
  color: #059669;
}
[data-theme="dark"] .vg-esp-chip:hover {
  background-color: rgba(52, 211, 153, 0.15);
  color: #34d399;
}
</style>
