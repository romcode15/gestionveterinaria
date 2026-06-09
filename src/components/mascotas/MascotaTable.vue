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
  <div class="overflow-x-auto rounded-xl vg-table-border">
    <table class="w-full text-sm">
      <thead>
        <tr class="vg-table-head">
          <th class="px-4 py-3 text-left font-semibold" style="color: var(--text-muted)">Mascota</th>
          <th class="px-4 py-3 text-left font-semibold" style="color: var(--text-muted)">Especie / Raza</th>
          <th class="px-4 py-3 text-center font-semibold" style="color: var(--text-muted)">Sexo</th>
          <th class="px-4 py-3 text-center font-semibold" style="color: var(--text-muted)">Edad</th>
          <th class="px-4 py-3 text-center font-semibold" style="color: var(--text-muted)">Peso</th>
          <th class="px-4 py-3 text-left font-semibold" style="color: var(--text-muted)">Propietario</th>
          <th class="px-4 py-3 text-right font-semibold w-24" style="color: var(--text-muted)">Acciones</th>
        </tr>
      </thead>
      <tbody>
        <!-- Loading skeleton -->
        <template v-if="props.loading">
          <tr v-for="i in 5" :key="i" class="vg-table-divider">
            <td v-for="j in 7" :key="j" class="px-4 py-3">
              <div class="h-4 vg-skeleton rounded animate-pulse" />
            </td>
          </tr>
        </template>

        <!-- Empty -->
        <tr v-else-if="props.mascotas.length === 0">
          <td colspan="7" class="px-4 py-12 text-center" style="color: var(--text-muted)">
            <div class="flex flex-col items-center gap-2">
              <span class="text-4xl">🐾</span>
              <span>No se encontraron mascotas</span>
            </div>
          </td>
        </tr>

        <!-- Rows -->
        <tr
          v-else
          v-for="mascota in props.mascotas"
          :key="mascota.id"
          class="vg-table-divider last:border-0 vg-table-row-hover transition-colors"
        >
          <td class="px-4 py-3">
            <div class="flex items-center gap-3">
              <MascotaAvatar :especie-nombre="mascota.especie.nombre" :nombre="mascota.nombre" size="sm" />
              <span class="font-medium" style="color: var(--text-primary)">{{ mascota.nombre }}</span>
            </div>
          </td>
          <td class="px-4 py-3">
            <p style="color: var(--text-secondary)">{{ mascota.especie.nombre }}</p>
            <p class="text-xs" style="color: var(--text-muted)">{{ mascota.raza.nombre }}</p>
          </td>
          <td class="px-4 py-3 text-center">
            <AppBadge :variant="mascota.sexo === 'macho' ? 'info' : 'warning'" size="sm">
              {{ mascota.sexo === 'macho' ? '♂' : '♀' }}
            </AppBadge>
          </td>
          <td class="px-4 py-3 text-center" style="color: var(--text-secondary)">
            {{ calcularEdad(mascota.fechaNacimiento) }}
          </td>
          <td class="px-4 py-3 text-center" style="color: var(--text-secondary)">
            {{ mascota.peso ? `${mascota.peso} kg` : '-' }}
          </td>
          <td class="px-4 py-3" style="color: var(--text-secondary)">{{ mascota.clienteNombre }}</td>
          <td class="px-4 py-3 text-right">
            <button
              @click="emit('edit', mascota)"
              class="p-1.5 rounded-lg transition-colors vg-icon-btn"
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
