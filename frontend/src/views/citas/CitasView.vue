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
import TableViewLayout from '@/components/common/TableViewLayout.vue'
import { ESTADO_CITA_OPTIONS, ESTADO_CITA_CAMBIO_OPTIONS } from '@/constants/filterOptions'

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

const estadoOptions      = ESTADO_CITA_OPTIONS
const estadoCambioOptions = ESTADO_CITA_CAMBIO_OPTIONS

const medicoOptions = computed(() => [
  { value: '', label: 'Todos los médicos' },
  ...citasStore.medicos.map((m) => ({ value: m.id, label: `${m.nombre} ${m.apellido}` })),
])

const summaryItems = computed(() => [
  { label: 'Total',       value: citasStore.estadisticas.total,
    svgPath: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2',
    iconColor: '#6366f1', iconBg: 'rgba(99,102,241,0.12)' },
  { label: 'Pendientes',  value: citasStore.estadisticas.pendientes,
    svgPath: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z',
    iconColor: '#f59e0b', iconBg: 'rgba(245,158,11,0.12)' },
  { label: 'Confirmadas', value: citasStore.estadisticas.confirmadas,
    svgPath: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
    iconColor: '#3b82f6', iconBg: 'rgba(59,130,246,0.12)' },
  { label: 'Completadas', value: citasStore.estadisticas.completadas,
    svgPath: 'M5 13l4 4L19 7',
    iconColor: '#059669', iconBg: 'rgba(5,150,105,0.12)' },
  { label: 'Canceladas',  value: citasStore.estadisticas.canceladas,
    svgPath: 'M6 18L18 6M6 6l12 12',
    iconColor: '#ef4444', iconBg: 'rgba(239,68,68,0.12)' },
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

    <TableViewLayout>
      <template #toolbar>
        <Transition name="fade">
          <AppAlert v-if="successMessage" type="success" dismissible @dismiss="successMessage = ''">
            {{ successMessage }}
          </AppAlert>
        </Transition>
        <EntitySummary :items="summaryItems" />
        <SearchToolbar new-button-label="Nueva cita" @new="abrirCrear">
          <template #filters>
            <AppSelect
              :model-value="citasStore.filtroMedicoId ?? ''"
              :options="medicoOptions"
              class="w-full sm:flex-1"
              @update:model-value="handleFiltroMedico"
            />
            <AppSelect v-model="citasStore.filtroEstado" :options="estadoOptions" class="w-full sm:w-48" />
          </template>
        </SearchToolbar>
      </template>

      <template #content>
        <AppCard fill-height padding="none">
          <template #header>
            <div class="px-6 py-4 border-b shrink-0" style="border-color: var(--border-color)">
              <h2 class="font-semibold" style="color: var(--text-primary)">
                {{ citasStore.citasFiltradas.length }} cita(s)
              </h2>
            </div>
          </template>

          <div class="divide-y" style="border-color: var(--border-color)">
            <EmptyState
              v-if="citasStore.citasFiltradas.length === 0"
              title="Sin citas"
              message="No se encontraron citas con los filtros seleccionados"
            />
            <div
              v-for="cita in citasStore.citasFiltradas"
              :key="cita.id"
              class="px-4 sm:px-6 py-4 vg-table-row-hover transition-colors"
            >
              <div class="flex gap-3">
                <div class="w-1 rounded-full shrink-0 self-stretch"
                  :style="{ backgroundColor: cita.tipoCita?.color ?? '#6b7280' }" />
                <div class="flex-1 min-w-0 flex flex-col gap-2">
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
                        <AppBadge variant="neutral" size="sm">{{ cita.tipoCita?.nombre ?? '-' }}</AppBadge>
                      </div>
                      <p class="text-xs truncate mt-0.5" style="color: var(--text-secondary)">{{ cita.motivo }}</p>
                      <p class="text-xs truncate mt-0.5" style="color: var(--text-muted)">
                        {{ cita.medicoNombre }} · {{ cita.clienteNombre }}
                      </p>
                    </div>
                  </div>
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
                      <AppButton variant="ghost" size="sm" aria-label="Editar cita" @click="abrirEditar(cita)">
                        <svg class="w-4 h-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                      </AppButton>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </AppCard>
      </template>
    </TableViewLayout>

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
