<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import CitaStatusBadge from '@/components/citas/CitaStatusBadge.vue'
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
      <div>
        <h1 class="text-lg font-semibold text-slate-800">Citas</h1>
        <p class="text-xs text-slate-500">Gestión de citas veterinarias</p>
      </div>
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="successMessage" type="success" dismissible @dismiss="successMessage = ''">
          {{ successMessage }}
        </AppAlert>
      </Transition>

      <!-- Stats rápidas -->
      <div class="grid grid-cols-2 sm:grid-cols-5 gap-3">
        <AppCard padding="sm" class="text-center">
          <p class="text-xl font-bold text-slate-800">{{ citasStore.estadisticas.total }}</p>
          <p class="text-xs text-slate-500">Total</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-xl font-bold text-accent-600">{{ citasStore.estadisticas.pendientes }}</p>
          <p class="text-xs text-slate-500">Pendientes</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-xl font-bold text-blue-600">{{ citasStore.estadisticas.confirmadas }}</p>
          <p class="text-xs text-slate-500">Confirmadas</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-xl font-bold text-primary-600">{{ citasStore.estadisticas.completadas }}</p>
          <p class="text-xs text-slate-500">Completadas</p>
        </AppCard>
        <AppCard padding="sm" class="text-center">
          <p class="text-xl font-bold text-danger-500">{{ citasStore.estadisticas.canceladas }}</p>
          <p class="text-xs text-slate-500">Canceladas</p>
        </AppCard>
      </div>

      <!-- Toolbar -->
      <AppCard padding="sm">
        <div class="flex flex-col gap-3">
          <!-- Fila 1: filtros -->
          <div class="flex flex-col sm:flex-row gap-3 w-full">
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
          </div>
          <!-- Fila 2: botón -->
          <div class="flex justify-end">
            <AppButton @click="abrirCrear" class="w-full sm:w-auto">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              Nueva cita
            </AppButton>
          </div>
        </div>
      </AppCard>

      <!-- Lista de citas -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b border-slate-100">
          <h2 class="font-semibold text-slate-800">
            {{ citasStore.citasFiltradas.length }} cita(s)
          </h2>
        </div>

        <div class="divide-y divide-slate-100">
          <div
            v-if="citasStore.citasFiltradas.length === 0"
            class="px-6 py-12 text-center text-slate-400"
          >
            <svg class="w-10 h-10 text-slate-300 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            No se encontraron citas
          </div>

          <div
            v-for="cita in citasStore.citasFiltradas"
            :key="cita.id"
            class="px-4 sm:px-6 py-4 hover:bg-slate-50 transition-colors"
          >
            <!-- Layout móvil: dos filas. Desktop: una fila -->
            <div class="flex gap-3">
              <!-- Barra de color tipo cita -->
              <div
                class="w-1 rounded-full shrink-0 self-stretch"
                :style="{ backgroundColor: cita.tipoCita.color }"
              />

              <div class="flex-1 min-w-0 flex flex-col gap-2">
                <!-- Fila 1: fecha/hora + info principal -->
                <div class="flex items-start gap-3">
                  <!-- Fecha y hora -->
                  <div class="text-center shrink-0 w-16 sm:w-20">
                    <p class="text-xs font-semibold text-slate-500 capitalize leading-tight">
                      {{ formatFecha(cita.fecha) }}
                    </p>
                    <p class="text-sm font-bold text-slate-800">{{ cita.horaInicio }}</p>
                    <p class="text-xs text-slate-400">{{ cita.horaFin }}</p>
                  </div>

                  <!-- Info principal -->
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2 flex-wrap">
                      <p class="font-semibold text-slate-800 text-sm">{{ cita.mascotaNombre }}</p>
                      <AppBadge variant="neutral" size="sm">{{ cita.tipoCita.nombre }}</AppBadge>
                    </div>
                    <p class="text-xs text-slate-500 truncate mt-0.5">{{ cita.motivo }}</p>
                    <p class="text-xs text-slate-400 truncate mt-0.5">
                      {{ cita.medicoNombre }} · {{ cita.clienteNombre }}
                    </p>
                  </div>
                </div>

                <!-- Fila 2: estado + acciones -->
                <!-- Móvil: columna apilada. Desktop: fila horizontal -->
                <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 sm:pl-0">

                  <!-- Badge de estado actual -->
                  <CitaStatusBadge :estado="cita.estado" />

                  <!-- Selector de cambio de estado -->
                  <select
                    v-if="cita.estado !== 'completada' && cita.estado !== 'cancelada'"
                    :value="cita.estado"
                    @change="cambiarEstado(cita, ($event.target as HTMLSelectElement).value as EstadoCita)"
                    class="w-full sm:w-auto text-xs border border-slate-200 rounded-lg px-2 py-1.5
                           text-slate-600 focus:outline-none focus:ring-1 focus:ring-primary-400"
                    aria-label="Cambiar estado"
                  >
                    <option value="pendiente">Pendiente</option>
                    <option value="confirmada">Confirmar</option>
                    <option value="en_curso">En curso</option>
                    <option value="completada">Completar</option>
                    <option value="cancelada">Cancelar</option>
                  </select>

                  <!-- Botón editar -->
                  <button
                    @click="abrirEditar(cita)"
                    class="flex items-center gap-1.5 w-full sm:w-auto justify-center sm:justify-start
                           px-2 py-1.5 sm:p-1.5 rounded-lg text-xs text-slate-500
                           hover:text-primary-600 hover:bg-primary-50 transition-colors border border-slate-200 sm:border-0"
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

    <!-- Modal -->
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
