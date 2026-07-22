<script setup lang="ts">
interface Props {
  title: string
  subtitle?: string
}

defineProps<Props>()
</script>

<template>
  <div>
    <h1 class="text-lg font-semibold" style="color: var(--text-primary)">{{ title }}</h1>
    <p v-if="subtitle" class="text-xs" style="color: var(--text-muted)">{{ subtitle }}</p>
  </div>
</template>