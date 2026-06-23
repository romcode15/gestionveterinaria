<script setup lang="ts">
interface Props {
  especieNombre: string
  nombre: string
  size?: 'sm' | 'md' | 'lg'
}

const props = withDefaults(defineProps<Props>(), { size: 'md' })

const especieEmoji: Record<string, string> = {
  Perro: '🐕',
  Gato: '🐈',
  Conejo: '🐇',
  Ave: '🦜',
  Reptil: '🦎',
  Hámster: '🐹',
}

const emoji = especieEmoji[props.especieNombre] ?? '🐾'

const sizeClasses = {
  sm: 'w-8 h-8 text-base',
  md: 'w-12 h-12 text-2xl',
  lg: 'w-16 h-16 text-3xl',
}
</script>

<template>
  <div
    :class="[
      'rounded-2xl flex items-center justify-center shrink-0 vg-avatar',
      sizeClasses[props.size],
    ]"
    :aria-label="`${props.nombre} - ${props.especieNombre}`"
    role="img"
  >
    {{ emoji }}
  </div>
</template>

<style>
.vg-avatar {
  background-color: rgba(16, 185, 129, 0.12);
}
[data-theme="dark"] .vg-avatar {
  background-color: rgba(16, 185, 129, 0.15);
}
</style>
