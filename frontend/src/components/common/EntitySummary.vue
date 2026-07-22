<script setup lang="ts">
interface Item {
  label: string
  value: number | string
  icon?: string
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
    <div v-for="i in 4" :key="i" class="vg-card rounded-xl p-4 h-16 vg-skeleton animate-pulse"></div>
  </div>
  <div v-else class="grid grid-cols-2 md:grid-cols-4 gap-4">
    <div
      v-for="item in items"
      :key="item.label"
      class="vg-card rounded-xl p-4 flex items-center gap-3"
    >
      <span v-if="item.icon" class="text-2xl">{{ item.icon }}</span>
      <div>
        <p class="text-xs" style="color: var(--text-muted)">{{ item.label }}</p>
        <p class="text-xl font-semibold" style="color: var(--text-primary)">{{ item.value }}</p>
      </div>
    </div>
  </div>
</template>