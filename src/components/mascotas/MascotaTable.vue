<script setup lang="ts">
import type { Mascota } from '@/types'
import AppBadge from '@/components/ui/AppBadge.vue'
import MascotaAvatar from './MascotaAvatar.vue'

interface Props {
  mascotas: Mascota[]
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), { loading: false })

const emit = defineEmits<{
  edit: [mascota: Mascota]
  view: [mascota: Mascota]
}>()

function calcularEdad(fechaNacimiento?: string): string {
  if (!fechaNacimiento) return '-'
  const hoy = new Date()
  const nac = new Date(fechaNacimiento)
  const años = hoy.getFullYear() - nac.getFullYear()
  if (años === 0) {
    const meses = hoy.getMonth() - nac.getMonth()
    return `${meses < 0 ? meses + 12 : meses}m`
  }
  return `${años}a`
}
</script>

<template>
  <div class="overflow-x-auto rounded-xl border border-slate-200">
    <table class="w-full text-sm">
      <thead>
        <tr class="bg-slate-50 border-b border-slate-200">
          <th class="px-4 py-3 text-left font-semibold text-slate-600">Mascota</th>
          <th class="px-4 py-3 text-left font-semibold text-slate-600">Especie / Raza</th>
          <th class="px-4 py-3 text-center font-semibold text-slate-600">Sexo</th>
          <th class="px-4 py-3 text-center font-semibold text-slate-600">Edad</th>
          <th class="px-4 py-3 text-center font-semibold text-slate-600">Peso</th>
          <th class="px-4 py-3 text-left font-semibold text-slate-600">Propietario</th>
          <th class="px-4 py-3 text-right font-semibold text-slate-600 w-24">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <template v-if="props.loading">
          <tr v-for="i in 5" :key="i" class="border-b border-slate-100">
            <td v-for="j in 7" :key="j" class="px-4 py-3">
              <div class="h-4 bg-slate-200 rounded animate-pulse" />
            </td>
          </tr>
        </template>

        <tr v-else-if="props.mascotas.length === 0">
          <td colspan="7" class="px-4 py-12 text-center text-slate-400">
            <div class="flex flex-col items-center gap-2">
              <span class="text-4xl">🐾</span>
              <span>No se encontraron mascotas</span>
            </div>
          </td>
        </tr>

        <tr
          v-else
          v-for="mascota in props.mascotas"
          :key="mascota.id"
          class="border-b border-slate-100 last:border-0 hover:bg-slate-50 transition-colors"
        >
          <td class="px-4 py-3">
            <div class="flex items-center gap-3">
              <MascotaAvatar :especie-nombre="mascota.especie.nombre" :nombre="mascota.nombre" size="sm" />
              <span class="font-medium text-slate-800">{{ mascota.nombre }}</span>
            </div>
          </td>
          <td class="px-4 py-3">
            <p class="text-slate-700">{{ mascota.especie.nombre }}</p>
            <p class="text-xs text-slate-400">{{ mascota.raza.nombre }}</p>
          </td>
          <td class="px-4 py-3 text-center">
            <AppBadge :variant="mascota.sexo === 'macho' ? 'info' : 'warning'" size="sm">
              {{ mascota.sexo === 'macho' ? '♂' : '♀' }}
            </AppBadge>
          </td>
          <td class="px-4 py-3 text-center text-slate-600">
            {{ calcularEdad(mascota.fechaNacimiento) }}
          </td>
          <td class="px-4 py-3 text-center text-slate-600">
            {{ mascota.peso ? `${mascota.peso} kg` : '-' }}
          </td>
          <td class="px-4 py-3 text-slate-600">{{ mascota.clienteNombre }}</td>
          <td class="px-4 py-3 text-right">
            <button
              @click="emit('edit', mascota)"
              class="p-1.5 rounded-lg text-slate-400 hover:text-primary-600 hover:bg-primary-50 transition-colors"
              title="Editar"
              aria-label="Editar mascota"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
