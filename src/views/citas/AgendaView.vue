<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import CitaCalendar from '@/components/citas/CitaCalendar.vue'
import AgendaSidebar from '@/components/citas/AgendaSidebar.vue'
import CitaForm from '@/components/citas/CitaForm.vue'
import { useCitasStore } from '@/stores/citas.store'
import { useMascotasStore } from '@/stores/mascotas.store'
import type { Cita, CitaFormData } from '@/types'

const citasStore = useCitasStore()
const mascotasStore = useMascotasStore()

onMounted(async () => {
  await Promise.all([citasStore.cargar(), mascotasStore.cargar()])
})

const showModal = ref(false)
const citaEditando = ref<Cita | null>(null)
const successMessage = ref('')
const loading = ref(false)

const citasDiaSeleccionado = computed(() =>
  citasStore.citasPorFecha(citasStore.fechaSeleccionada).sort((a, b) =>
    a.horaInicio.localeCompare(b.horaInicio),
  ),
)

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
      successMessage.value = 'Cita actualizada'
    } else {
      await citasStore.crear(data)
      successMessage.value = 'Cita agendada'
    }
    showModal.value = false
    setTimeout(() => (successMessage.value = ''), 3000)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <div>
        <h1 class="text-lg font-semibold text-slate-800">Agenda</h1>
        <p class="text-xs text-slate-500">Vista de calendario</p>
      </div>
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="successMessage" type="success" dismissible @dismiss="successMessage = ''">
          {{ successMessage }}
        </AppAlert>
      </Transition>

      <div class="flex justify-end">
        <AppButton @click="abrirCrear">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          Nueva cita
        </AppButton>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <!-- Calendario -->
        <div class="lg:col-span-2">
          <CitaCalendar
            :citas="citasStore.citas"
            v-model:fecha-seleccionada="citasStore.fechaSeleccionada"
          />
        </div>

        <!-- Sidebar con citas del día -->
        <div class="lg:col-span-1 min-h-100">
          <AgendaSidebar
            :citas="citasDiaSeleccionado"
            :fecha="citasStore.fechaSeleccionada"
            @edit="abrirEditar"
          />
        </div>
      </div>
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
