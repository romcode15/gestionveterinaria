<script setup lang="ts" generic="T extends { fecha: string; descripcion: string; }">
interface Props {
  items: T[]
  emptyMessage?: string
}

withDefaults(defineProps<Props>(), {
  emptyMessage: 'No hay eventos',
})
</script>

<template>
  <div class="relative">
    <!-- Línea vertical -->
    <div class="absolute left-4 top-0 bottom-0 w-0.5" style="background-color: var(--border-color)"></div>
    
    <div v-if="items.length === 0" class="py-8 text-center text-sm" style="color: var(--text-muted)">
      {{ emptyMessage }}
    </div>

    <div
      v-for="(item, index) in items"
      :key="index"
      class="relative pl-10 pb-6 last:pb-0"
    >
      <!-- Punto -->
      <div class="absolute left-2.5 w-3.5 h-3.5 rounded-full -translate-x-0.5 mt-1" style="background-color: var(--color-primary); border: 2px solid var(--bg-default)"></div>
      
      <div class="text-xs" style="color: var(--text-muted)">{{ new Date(item.fecha).toLocaleString() }}</div>
      <div class="text-sm" style="color: var(--text-primary)">{{ item.descripcion }}</div>
      <slot name="extra" :item="item" />
    </div>
  </div>
</template>