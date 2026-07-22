<script setup lang="ts">
interface Props {
  page: number           // página actual (0-based del backend)
  totalPages: number
  totalElements: number
  pageSize: number
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
})

const emit = defineEmits<{
  change: [page: number]
}>()

// Página que se muestra al usuario (1-based)
const paginaVisible = (p: number) => p + 1

// Generar array de páginas visibles con elipsis
function paginas(): (number | '...')[] {
  const total = props.totalPages
  const actual = props.page

  if (total <= 7) {
    return Array.from({ length: total }, (_, i) => i)
  }

  const paginas: (number | '...')[] = [0]

  if (actual > 3) paginas.push('...')

  const inicio = Math.max(1, actual - 1)
  const fin    = Math.min(total - 2, actual + 1)

  for (let i = inicio; i <= fin; i++) paginas.push(i)

  if (actual < total - 4) paginas.push('...')

  paginas.push(total - 1)
  return paginas
}

const desde = () => props.page * props.pageSize + 1
const hasta = () => Math.min((props.page + 1) * props.pageSize, props.totalElements)
</script>

<template>
  <div
    v-if="totalPages > 1 || totalElements > 0"
    class="flex flex-col sm:flex-row items-center justify-between gap-3 px-1 py-3"
  >
    <!-- Info de registros -->
    <p class="text-sm" style="color: var(--text-muted)">
      Mostrando
      <span class="font-medium" style="color: var(--text-secondary)">{{ desde() }}</span>
      –
      <span class="font-medium" style="color: var(--text-secondary)">{{ hasta() }}</span>
      de
      <span class="font-medium" style="color: var(--text-secondary)">{{ totalElements }}</span>
      registros
    </p>

    <!-- Controles de página -->
    <div class="flex items-center gap-1">

      <!-- Anterior -->
      <button
        class="flex items-center justify-center w-8 h-8 rounded-lg transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
        style="color: var(--text-muted)"
        :class="page > 0 ? 'hover:bg-(--bg-hover)' : ''"
        :disabled="page === 0 || loading"
        @click="emit('change', page - 1)"
        aria-label="Página anterior"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </button>

      <!-- Números de página -->
      <template v-for="(p, i) in paginas()" :key="i">
        <!-- Elipsis -->
        <span
          v-if="p === '...'"
          class="flex items-center justify-center w-8 h-8 text-sm"
          style="color: var(--text-disabled)"
        >
          …
        </span>

        <!-- Número -->
        <button
          v-else
          class="flex items-center justify-center w-8 h-8 rounded-lg text-sm font-medium transition-colors"
          :class="p === page
            ? 'bg-(--color-primary) text-white'
            : 'hover:bg-(--bg-hover)'
          "
          :style="p !== page ? { color: 'var(--text-secondary)' } : {}"
          :disabled="loading"
          @click="emit('change', p)"
          :aria-label="`Página ${paginaVisible(p)}`"
          :aria-current="p === page ? 'page' : undefined"
        >
          {{ paginaVisible(p) }}
        </button>
      </template>

      <!-- Siguiente -->
      <button
        class="flex items-center justify-center w-8 h-8 rounded-lg transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
        style="color: var(--text-muted)"
        :class="page < totalPages - 1 ? 'hover:bg-(--bg-hover)' : ''"
        :disabled="page >= totalPages - 1 || loading"
        @click="emit('change', page + 1)"
        aria-label="Página siguiente"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
        </svg>
      </button>

    </div>
  </div>
</template>
