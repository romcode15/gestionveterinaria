<script setup lang="ts">
interface Props {
  searchPlaceholder?: string
  showNewButton?: boolean
  newButtonLabel?: string
}

withDefaults(defineProps<Props>(), {
  searchPlaceholder: 'Buscar...',
  showNewButton: true,
  newButtonLabel: 'Nuevo',
})

const emit = defineEmits<{
  'update:search': [value: string]
  new: []
}>()

const search = defineModel<string>('search', { default: '' })
</script>

<template>
  <AppCard padding="sm">
    <div class="flex flex-col gap-3">
      <div class="flex flex-col sm:flex-row gap-3 w-full">
        <AppSearchInput
          v-model="search"
          :placeholder="searchPlaceholder"
          class="w-full sm:flex-1"
          @update:model-value="(val) => emit('update:search', val)"
        />
        <slot name="filters" />
      </div>
      <div class="flex items-center justify-between gap-3 sm:justify-end">
        <slot name="actions" />
        <AppButton v-if="showNewButton" @click="emit('new')" class="flex-1 sm:flex-none">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          {{ newButtonLabel }}
        </AppButton>
      </div>
    </div>
  </AppCard>
</template>