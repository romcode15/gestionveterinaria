<script setup lang="ts">
import type { Cliente } from '@/types'
import AppBadge from '@/components/ui/AppBadge.vue'

interface Props {
  clientes: Cliente[]
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), { loading: false })

const emit = defineEmits<{
  edit: [cliente: Cliente]
  toggleEstado: [cliente: Cliente]
  view: [cliente: Cliente]
}>()
</script>

<template>
  <div class="overflow-x-auto rounded-xl border border-slate-200">
    <table class="w-full text-sm">
      <thead>
        <tr class="bg-slate-50 border-b border-slate-200">
          <th class="px-4 py-3 text-left font-semibold text-slate-600">Cliente</th>
          <th class="px-4 py-3 text-left font-semibold text-slate-600">Documento</th>
          <th class="px-4 py-3 text-left font-semibold text-slate-600">Contacto</th>
          <th class="px-4 py-3 text-center font-semibold text-slate-600">Mascotas</th>
          <th class="px-4 py-3 text-center font-semibold text-slate-600">Estado</th>
          <th class="px-4 py-3 text-right font-semibold text-slate-600 w-28">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <!-- Loading -->
        <template v-if="props.loading">
          <tr v-for="i in 5" :key="i" class="border-b border-slate-100">
            <td v-for="j in 6" :key="j" class="px-4 py-3">
              <div class="h-4 bg-slate-200 rounded animate-pulse" />
            </td>
          </tr>
        </template>

        <!-- Empty -->
        <tr v-else-if="props.clientes.length === 0">
          <td colspan="6" class="px-4 py-12 text-center text-slate-400">
            <div class="flex flex-col items-center gap-2">
              <svg class="w-10 h-10 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                  d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <span>No se encontraron clientes</span>
            </div>
          </td>
        </tr>

        <!-- Rows -->
        <tr
          v-else
          v-for="cliente in props.clientes"
          :key="cliente.id"
          class="border-b border-slate-100 last:border-0 hover:bg-slate-50 transition-colors"
        >
          <td class="px-4 py-3">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-semibold text-sm shrink-0">
                {{ cliente.nombre[0] }}{{ cliente.apellido[0] }}
              </div>
              <div>
                <p class="font-medium text-slate-800">{{ cliente.nombre }} {{ cliente.apellido }}</p>
                <p class="text-xs text-slate-400">{{ cliente.ciudad }}</p>
              </div>
            </div>
          </td>
          <td class="px-4 py-3 text-slate-600">
            <span class="text-xs text-slate-400">{{ cliente.tipoDocumento }}</span>
            <p>{{ cliente.numeroDocumento }}</p>
          </td>
          <td class="px-4 py-3">
            <p class="text-slate-700">{{ cliente.email }}</p>
            <p class="text-xs text-slate-400">{{ cliente.telefono }}</p>
          </td>
          <td class="px-4 py-3 text-center">
            <AppBadge :variant="cliente.numeroMascotas > 0 ? 'primary' : 'neutral'">
              {{ cliente.numeroMascotas }}
            </AppBadge>
          </td>
          <td class="px-4 py-3 text-center">
            <AppBadge :variant="cliente.estado === 'activo' ? 'success' : 'neutral'" dot>
              {{ cliente.estado === 'activo' ? 'Activo' : 'Inactivo' }}
            </AppBadge>
          </td>
          <td class="px-4 py-3 text-right">
            <div class="flex items-center justify-end gap-1">
              <button
                @click="emit('edit', cliente)"
                class="p-1.5 rounded-lg text-slate-400 hover:text-primary-600 hover:bg-primary-50 transition-colors"
                title="Editar"
                aria-label="Editar cliente"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </button>
              <button
                @click="emit('toggleEstado', cliente)"
                :class="[
                  'p-1.5 rounded-lg transition-colors',
                  cliente.estado === 'activo'
                    ? 'text-slate-400 hover:text-danger-600 hover:bg-danger-50'
                    : 'text-slate-400 hover:text-primary-600 hover:bg-primary-50',
                ]"
                :title="cliente.estado === 'activo' ? 'Inactivar' : 'Activar'"
                :aria-label="cliente.estado === 'activo' ? 'Inactivar cliente' : 'Activar cliente'"
              >
                <svg v-if="cliente.estado === 'activo'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                </svg>
                <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
