<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppBadge from '@/components/ui/AppBadge.vue'

import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import EntitySummary from '@/components/common/EntitySummary.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import EmptyState from '@/components/common/EmptyState.vue'

import CitaForm from '@/components/citas/CitaForm.vue'
import { useCitasStore } from '@/stores/citas.store'
import { useMascotasStore } from '@/stores/mascotas.store'
import type { Cita, CitaFormData, EstadoCita } from '@/types'

const citasStore = useCitasStore()
const mascotasStore = useMascotasStore()

onMounted(async () => {
  await Promise.all([citasStore.cargar(), mascotasStore.cargar()])
})

const showModal = ref(false)
const citaEditando = ref<Cita | null>(null)
const successMessage = ref('')
const loading = ref(false)

const estadoOptions = [
  { value: 'todos', label: 'Todos los estados' },
  { value: 'pendiente', label: 'Pendientes' },
  { value: 'confirmada', label: 'Confirmadas' },
  { value: 'en_curso', label: 'En curso' },
  { value: 'completada', label: 'Completadas' },
  { value: 'cancelada', label: 'Canceladas' },
]

const medicoOptions = computed(() => [
  { value: '', label: 'Todos los médicos' },
  ...citasStore.medicos.map((m) => ({ value: m.id, label: `${m.nombre} ${m.apellido}` })),
])

const summaryItems = computed(() => [
  { label: 'Total', value: citasStore.estadisticas.total, icon: '📋' },
  { label: 'Pendientes', value: citasStore.estadisticas.pendientes, icon: '⏳' },
  { label: 'Confirmadas', value: citasStore.estadisticas.confirmadas, icon: '✅' },
  { label: 'Completadas', value: citasStore.estadisticas.completadas, icon: '✔️' },
  { label: 'Canceladas', value: citasStore.estadisticas.canceladas, icon: '❌' },
])

function abrirCrear() {
  citaEditando.value = null
  showModal.value = true
}

function abrirEditar(cita: Cita) {
  citaEditando.value = cita
  showModal.value = true
}

async function handleSubmit(data: CitaFormData) {
  loading.value = true
  try {
    if (citaEditando.value) {
      await citasStore.actualizar(citaEditando.value.id, data)
      successMessage.value = 'Cita actualizada correctamente'
    } else {
      await citasStore.crear(data)
      successMessage.value = 'Cita agendada correctamente'
    }
    showModal.value = false
    setTimeout(() => (successMessage.value = ''), 3000)
  } finally {
    loading.value = false
  }
}

async function cambiarEstado(cita: Cita, estado: EstadoCita) {
  await citasStore.cambiarEstado(cita.id, estado)
  successMessage.value = `Estado actualizado a: ${estado}`
  setTimeout(() => (successMessage.value = ''), 3000)
}

function handleFiltroMedico(val: string | number) {
  citasStore.filtroMedicoId = val ? Number(val) : null
}

function formatFecha(fecha: string): string {
  return new Date(fecha + 'T00:00:00').toLocaleDateString('es-CO', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
  })
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Citas" subtitle="Gestión de citas veterinarias" />
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="successMessage" type="success" dismissible @dismiss="successMessage = ''">
          {{ successMessage }}
        </AppAlert>
      </Transition>

      <!-- Stats con EntitySummary -->
      <EntitySummary :items="summaryItems" :columns="5" />

      <!-- Toolbar con SearchToolbar -->
      <SearchToolbar
        :show-new-button="true"
        new-button-label="Nueva cita"
        @new="abrirCrear"
      >
        <template #filters>
          <AppSelect
            :model-value="citasStore.filtroMedicoId ?? ''"
            :options="medicoOptions"
            class="w-full sm:flex-1"
            @update:model-value="handleFiltroMedico"
          />
          <AppSelect
            v-model="citasStore.filtroEstado"
            :options="estadoOptions"
            class="w-full sm:w-48"
          />
        </template>
      </SearchToolbar>

      <!-- Lista de citas -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ citasStore.citasFiltradas.length }} cita(s)
          </h2>
        </div>

        <div class="divide-y" style="border-color: var(--border-color)">
          <EmptyState
            v-if="citasStore.citasFiltradas.length === 0"
            icon="📅"
            title="Sin citas"
            message="No se encontraron citas"
          />

          <div
            v-for="cita in citasStore.citasFiltradas"
            :key="cita.id"
            class="px-4 sm:px-6 py-4 vg-table-row-hover transition-colors"
          >
            <div class="flex gap-3">
              <div
                class="w-1 rounded-full shrink-0 self-stretch"
                :style="{ backgroundColor: cita.tipoCita.color }"
              />

              <div class="flex-1 min-w-0 flex flex-col gap-2">
                <!-- Fila 1: fecha/hora + info principal -->
                <div class="flex items-start gap-3">
                  <div class="text-center shrink-0 w-16 sm:w-20">
                    <p class="text-xs font-semibold capitalize leading-tight" style="color: var(--text-muted)">
                      {{ formatFecha(cita.fecha) }}
                    </p>
                    <p class="text-sm font-bold" style="color: var(--text-primary)">{{ cita.horaInicio }}</p>
                    <p class="text-xs" style="color: var(--text-muted)">{{ cita.horaFin }}</p>
                  </div>

                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2 flex-wrap">
                      <p class="font-semibold text-sm" style="color: var(--text-primary)">{{ cita.mascotaNombre }}</p>
                      <AppBadge variant="neutral" size="sm">{{ cita.tipoCita.nombre }}</AppBadge>
                    </div>
                    <p class="text-xs truncate mt-0.5" style="color: var(--text-secondary)">{{ cita.motivo }}</p>
                    <p class="text-xs truncate mt-0.5" style="color: var(--text-muted)">
                      {{ cita.medicoNombre }} · {{ cita.clienteNombre }}
                    </p>
                  </div>
                </div>

                <!-- Fila 2: estado + acciones -->
                <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 sm:pl-0">
                  <StatusBadge :estado="cita.estado" />

                  <select
                    v-if="cita.estado !== 'completada' && cita.estado !== 'cancelada'"
                    :value="cita.estado"
                    @change="cambiarEstado(cita, ($event.target as HTMLSelectElement).value as EstadoCita)"
                    class="w-full sm:w-auto text-xs border rounded-lg px-2 py-1.5
                           focus:outline-none focus:ring-1 focus:ring-primary-400"
                    style="border-color: var(--border-color); color: var(--text-secondary); background: var(--bg-card)"
                    aria-label="Cambiar estado"
                  >
                    <option value="pendiente">Pendiente</option>
                    <option value="confirmada">Confirmar</option>
                    <option value="en_curso">En curso</option>
                    <option value="completada">Completar</option>
                    <option value="cancelada">Cancelar</option>
                  </select>

                  <button
                    @click="abrirEditar(cita)"
                    class="flex items-center gap-1.5 w-full sm:w-auto justify-center sm:justify-start
                           px-2 py-1.5 sm:p-1.5 rounded-lg text-xs transition-colors
                           border sm:border-0"
                    style="border-color: var(--border-color); color: var(--text-muted)"
                    :style="{
                      color: 'var(--text-muted)',
                      hover: { color: 'var(--color-primary)', background: 'var(--bg-hover)' }
                    }"
                    title="Editar cita"
                    aria-label="Editar cita"
                  >
                    <svg class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                    <span class="sm:hidden">Editar cita</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </AppCard>
    </div>

    <AppModal
      v-model="showModal"
      :title="citaEditando ? 'Editar cita' : 'Nueva cita'"
      size="md"
    >
      <CitaForm
        :cita="citaEditando"
        :tipos-cita="citasStore.tiposCita"
        :medicos="citasStore.medicos"
        :mascotas="mascotasStore.mascotas"
        :loading="loading"
        @submit="handleSubmit"
        @cancel="showModal = false"
      />
    </AppModal>
  </DashboardLayout>
</template>
