<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppButton from '@/components/ui/AppButton.vue'

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
  await Promise.all([
    citasStore.cargar(),
    citasStore.cargarCatalogos(),
    mascotasStore.cargar(),
  ])
})

const showModal = ref(false)
const citaEditando = ref<Cita | null>(null)
const successMessage = ref('')
const loading = ref(false)

const estadoOptions = [
  { value: 'todos',      label: 'Todos los estados' },
  { value: 'pendiente',  label: 'Pendientes' },
  { value: 'confirmada', label: 'Confirmadas' },
  { value: 'en_curso',   label: 'En curso' },
  { value: 'completada', label: 'Completadas' },
  { value: 'cancelada',  label: 'Canceladas' },
]

const estadoCambioOptions = [
  { value: 'pendiente',  label: 'Pendiente' },
  { value: 'confirmada', label: 'Confirmar' },
  { value: 'en_curso',   label: 'En curso' },
  { value: 'completada', label: 'Completar' },
  { value: 'cancelada',  label: 'Cancelar' },
]

const medicoOptions = computed(() => [
  { value: '', label: 'Todos los médicos' },
  ...citasStore.medicos.map((m) => ({ value: m.id, label: `${m.nombre} ${m.apellido}` })),
])

const summaryItems = computed(() => [
  { label: 'Total',       value: citasStore.estadisticas.total,      icon: '📋' },
  { label: 'Pendientes',  value: citasStore.estadisticas.pendientes,  icon: '⏳' },
  { label: 'Confirmadas', value: citasStore.estadisticas.confirmadas, icon: '✅' },
  { label: 'Completadas', value: citasStore.estadisticas.completadas, icon: '✔️' },
  { label: 'Canceladas',  value: citasStore.estadisticas.canceladas,  icon: '❌' },
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

async function cambiarEstado(cita: Cita, estado: string | number) {
  await citasStore.cambiarEstado(cita.id, estado as EstadoCita)
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

      <!-- Estadísticas -->
      <EntitySummary :items="summaryItems" />

      <!-- Toolbar de filtros -->
      <SearchToolbar
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
            message="No se encontraron citas con los filtros seleccionados"
          />

          <div
            v-for="cita in citasStore.citasFiltradas"
            :key="cita.id"
            class="px-4 sm:px-6 py-4 vg-table-row-hover transition-colors"
          >
            <div class="flex gap-3">
              <!-- Franja de color del tipo de cita -->
              <div
                class="w-1 rounded-full shrink-0 self-stretch"
                :style="{ backgroundColor: cita.tipoCita?.color ?? '#6b7280' }"
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
                      <p class="font-semibold text-sm" style="color: var(--text-primary)">
                        {{ cita.mascotaNombre }}
                      </p>
                      <AppBadge variant="neutral" size="sm">{{ cita.tipoCita?.nombre ?? '-' }}</AppBadge>
                    </div>
                    <p class="text-xs truncate mt-0.5" style="color: var(--text-secondary)">{{ cita.motivo }}</p>
                    <p class="text-xs truncate mt-0.5" style="color: var(--text-muted)">
                      {{ cita.medicoNombre }} · {{ cita.clienteNombre }}
                    </p>
                  </div>
                </div>

                <!-- Fila 2: estado + cambio de estado + botón editar -->
                <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
                  <StatusBadge :status="cita.estado" />

                  <div class="flex items-center gap-2">
                    <AppSelect
                      v-if="cita.estado !== 'completada' && cita.estado !== 'cancelada'"
                      :model-value="cita.estado"
                      :options="estadoCambioOptions"
                      class="w-full sm:w-auto text-xs"
                      aria-label="Cambiar estado"
                      @update:model-value="cambiarEstado(cita, $event)"
                    />

                    <AppButton
                      variant="ghost"
                      size="sm"
                      aria-label="Editar cita"
                      title="Editar cita"
                      @click="abrirEditar(cita)"
                    >
                      <svg class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                      <span class="sm:hidden">Editar</span>
                    </AppButton>
                  </div>
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
