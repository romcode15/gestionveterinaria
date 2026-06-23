<script setup lang="ts" generic="T extends Record<string, unknown>">
import type { TableColumn } from '@/types'

interface Props {
  columns: TableColumn<T>[]
  rows: T[]
  loading?: boolean
  emptyMessage?: string
  rowKey?: keyof T
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  emptyMessage: 'No hay datos disponibles',
  rowKey: 'id' as keyof T,
})

const emit = defineEmits<{
  rowClick: [row: T]
}>()
</script>

<template>
  <div class="overflow-x-auto rounded-xl vg-table-border">
    <table class="w-full text-sm">
      <thead>
        <tr class="vg-table-head">
          <th
            v-for="col in props.columns"
            :key="col.key"
            :style="col.width ? { width: col.width } : {}"
            :class="[
              'px-4 py-3 font-semibold whitespace-nowrap',
              col.align === 'center' ? 'text-center' : col.align === 'right' ? 'text-right' : 'text-left',
            ]"
            style="color: var(--text-muted)"
          >
            {{ col.label }}
          </th>
          <th
            v-if="$slots.actions"
            class="px-4 py-3 text-right font-semibold w-24"
            style="color: var(--text-muted)"
          >
            Acciones
          </th>
        </tr>
      </thead>
      <tbody>
        <!-- Loading skeleton -->
        <template v-if="props.loading">
          <tr v-for="i in 5" :key="i" class="vg-table-divider last:border-0">
            <td v-for="col in props.columns" :key="col.key" class="px-4 py-3">
              <div class="h-4 vg-skeleton rounded animate-pulse" />
            </td>
            <td v-if="$slots.actions" class="px-4 py-3">
              <div class="h-4 vg-skeleton rounded animate-pulse" />
            </td>
          </tr>
        </template>

        <!-- Empty state -->
        <tr v-else-if="props.rows.length === 0">
          <td
            :colspan="props.columns.length + ($slots.actions ? 1 : 0)"
            class="px-4 py-12 text-center"
            style="color: var(--text-muted)"
          >
            <div class="flex flex-col items-center gap-2">
              <svg class="w-10 h-10" style="color: var(--text-disabled)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                  d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
              <span>{{ props.emptyMessage }}</span>
            </div>
          </td>
        </tr>

        <!-- Data rows -->
        <tr
          v-else
          v-for="row in props.rows"
          :key="String((row as Record<string, unknown>)[props.rowKey as string])"
          class="vg-table-divider last:border-0 vg-table-row-hover transition-colors cursor-default"
          @click="emit('rowClick', row)"
        >
          <td
            v-for="col in props.columns"
            :key="col.key"
            :class="[
              'px-4 py-3',
              col.align === 'center' ? 'text-center' : col.align === 'right' ? 'text-right' : 'text-left',
            ]"
            style="color: var(--text-secondary)"
          >
            <slot :name="`cell-${col.key}`" :row="row" :value="row[col.key]">
              {{ col.render ? col.render(row) : row[col.key] }}
            </slot>
          </td>
          <td v-if="$slots.actions" class="px-4 py-3 text-right">
            <slot name="actions" :row="row" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
