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
  <div class="overflow-x-auto rounded-xl border border-slate-200">
    <table class="w-full text-sm">
      <thead>
        <tr class="bg-slate-50 border-b border-slate-200">
          <th
            v-for="col in props.columns"
            :key="col.key"
            :style="col.width ? { width: col.width } : {}"
            :class="[
              'px-4 py-3 font-semibold text-slate-600 whitespace-nowrap',
              col.align === 'center' ? 'text-center' : col.align === 'right' ? 'text-right' : 'text-left',
            ]"
          >
            {{ col.label }}
          </th>
          <th v-if="$slots.actions" class="px-4 py-3 text-right font-semibold text-slate-600 w-24">
            Acciones
          </th>
        </tr>
      </thead>
      <tbody>
        <!-- Loading skeleton -->
        <template v-if="props.loading">
          <tr v-for="i in 5" :key="i" class="border-b border-slate-100 last:border-0">
            <td
              v-for="col in props.columns"
              :key="col.key"
              class="px-4 py-3"
            >
              <div class="h-4 bg-slate-200 rounded animate-pulse" />
            </td>
            <td v-if="$slots.actions" class="px-4 py-3">
              <div class="h-4 bg-slate-200 rounded animate-pulse" />
            </td>
          </tr>
        </template>

        <!-- Empty state -->
        <tr v-else-if="props.rows.length === 0">
          <td
            :colspan="props.columns.length + ($slots.actions ? 1 : 0)"
            class="px-4 py-12 text-center text-slate-400"
          >
            <div class="flex flex-col items-center gap-2">
              <svg class="w-10 h-10 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
          class="border-b border-slate-100 last:border-0 hover:bg-slate-50 transition-colors"
          @click="emit('rowClick', row)"
        >
          <td
            v-for="col in props.columns"
            :key="col.key"
            :class="[
              'px-4 py-3 text-slate-700',
              col.align === 'center' ? 'text-center' : col.align === 'right' ? 'text-right' : 'text-left',
            ]"
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
