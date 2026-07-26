<script setup lang="ts">
interface Item {
  label: string
  value: number | string
  // icon se mantiene por compatibilidad pero ya no se renderiza como emoji
  icon?: string
  // SVG path para renderizar un ícono real
  svgPath?: string
  iconColor?: string
  iconBg?: string
}

interface Props {
  items: Item[]
  loading?: boolean
}

withDefaults(defineProps<Props>(), {
  loading: false,
})
</script>

<template>
  <div v-if="loading" class="grid grid-cols-2 md:grid-cols-4 gap-4">
    <div v-for="i in 4" :key="i" class="vg-card rounded-xl p-4 h-16 vg-skeleton animate-pulse" />
  </div>
  <div v-else class="grid grid-cols-2 md:grid-cols-4 gap-4">
    <div
      v-for="item in items"
      :key="item.label"
      class="vg-card rounded-xl p-4 flex items-center gap-3"
    >
      <!-- SVG path explícito -->
      <div
        v-if="item.svgPath"
        class="w-9 h-9 rounded-lg flex items-center justify-center shrink-0"
        :style="{ backgroundColor: item.iconBg ?? 'var(--bg-surface-2)' }"
      >
        <svg class="w-5 h-5" :style="{ color: item.iconColor ?? 'var(--text-secondary)' }"
             fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" :d="item.svgPath" />
        </svg>
      </div>
      <div>
        <p class="text-xs" style="color: var(--text-muted)">{{ item.label }}</p>
        <p class="text-xl font-semibold" style="color: var(--text-primary)">{{ item.value }}</p>
      </div>
    </div>
  </div>
</template>
