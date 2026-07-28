<script setup lang="ts">
interface Props {
  searchPlaceholder?: string
  showSearch?: boolean
  showNewButton?: boolean
  newButtonLabel?: string
}

withDefaults(defineProps<Props>(), {
  searchPlaceholder: 'Buscar...',
  showSearch: true,
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
  <!--
    Layout: una sola fila flex-wrap.
    - Search ocupa el espacio disponible (flex-1 min-w-48)
    - Filtros adicionales (#filters) se insertan inline en la misma fila
    - Acciones extra (#actions) van antes del botón Nuevo
    - Botón Nuevo se ancla a la derecha con ml-auto
    Con pocos elementos todo cabe en una línea.
    Con muchos (Auditoría) el wrap natural distribuye sin altura reservada.
  -->
  <AppCard padding="sm">
    <div class="flex flex-wrap items-center gap-3">

      <!-- Campo de búsqueda principal -->
      <AppSearchInput
        v-if="showSearch"
        v-model="search"
        :placeholder="searchPlaceholder"
        class="flex-1 min-w-48"
      />

      <!-- Filtros adicionales (selects, inputs, toggles) inyectados por cada vista -->
      <slot name="filters" />

      <!-- Acciones extra (ej. toggle tabla/grid) -->
      <slot name="actions" />

      <!-- Botón Nuevo — siempre alineado a la derecha -->
      <AppButton
        v-if="showNewButton"
        class="ml-auto shrink-0"
        @click="emit('new')"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        {{ newButtonLabel }}
      </AppButton>

    </div>
  </AppCard>
</template>
