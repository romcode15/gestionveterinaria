<script setup lang="ts">
interface Props {
  especieNombre: string
  nombre: string
  size?: 'sm' | 'md' | 'lg'
}

const props = withDefaults(defineProps<Props>(), { size: 'md' })

// Paleta de colores por especie — consistente con el resto del sistema
const especiePaleta: Record<string, { bg: string; color: string; letra: string }> = {
  Perro:   { bg: 'rgba(217,119,6,0.15)',  color: '#b45309', letra: 'Pe' },
  Gato:    { bg: 'rgba(139,92,246,0.15)', color: '#7c3aed', letra: 'Ga' },
  Conejo:  { bg: 'rgba(236,72,153,0.15)', color: '#be185d', letra: 'Co' },
  Ave:     { bg: 'rgba(14,165,233,0.15)', color: '#0369a1', letra: 'Av' },
  Reptil:  { bg: 'rgba(34,197,94,0.15)',  color: '#15803d', letra: 'Re' },
  Roedor:  { bg: 'rgba(249,115,22,0.15)', color: '#c2410c', letra: 'Ro' },
  Pez:     { bg: 'rgba(6,182,212,0.15)',  color: '#0e7490', letra: 'Pz' },
  Anfibio: { bg: 'rgba(16,185,129,0.15)', color: '#047857', letra: 'An' },
}

const paleta = especiePaleta[props.especieNombre] ?? {
  bg: 'rgba(16,185,129,0.12)', color: '#059669',
  letra: props.especieNombre.slice(0, 2),
}

const sizeClasses: Record<string, string> = {
  sm: 'w-8 h-8 text-xs',
  md: 'w-12 h-12 text-sm',
  lg: 'w-16 h-16 text-base',
}
</script>

<template>
  <div
    :class="['rounded-2xl flex items-center justify-center shrink-0 font-bold', sizeClasses[props.size]]"
    :style="{ backgroundColor: paleta.bg, color: paleta.color }"
    :aria-label="`${props.nombre} - ${props.especieNombre}`"
    role="img"
  >
    {{ paleta.letra }}
  </div>
</template>
