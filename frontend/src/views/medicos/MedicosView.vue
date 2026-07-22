<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSearchInput from '@/components/ui/AppSearchInput.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import MedicoCard from '@/components/medicos/MedicoCard.vue'
import MedicoForm from '@/components/medicos/MedicoForm.vue'
import { useMedicosStore } from '@/stores/medicos.store'
import type { Medico, MedicoFormData } from '@/types'

const medicosStore = useMedicosStore()

onMounted(async () => {
  await Promise.all([
    medicosStore.cargar({ page: 0 }),
    medicosStore.cargarCatalogos(),
  ])
})

watch(() => medicosStore.searchQuery, () => {
  medicosStore.cargar({ page: 0 })
})

const showModal       = ref(false)
const medicoEditando  = ref<Medico | null>(null)
const successMessage  = ref('')
const loading         = ref(false)

function abrirCrear() {
  medicoEditando.value = null
  showModal.value = true
}

function abrirEditar(medico: Medico) {
  medicoEditando.value = medico
  showModal.value = true
}

async function handleSubmit(data: MedicoFormData) {
  loading.value = true
  try {
    if (medicoEditando.value) {
      await medicosStore.actualizar(medicoEditando.value.id, data)
      successMessage.value = 'Médico actualizado correctamente'
    } else {
      await medicosStore.crear(data)
      successMessage.value = 'Médico registrado correctamente'
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
        <h1 class="text-lg font-semibold" style="color: var(--text-primary)">Médicos</h1>
        <p class="text-xs" style="color: var(--text-muted)">Gestión del equipo médico</p>
      </div>
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="medicosStore.error" type="error" dismissible @dismiss="medicosStore.limpiarError()">
          {{ medicosStore.error }}
        </AppAlert>
      </Transition>
      <Transition name="fade">
        <AppAlert v-if="successMessage" type="success" dismissible @dismiss="successMessage = ''">
          {{ successMessage }}
        </AppAlert>
      </Transition>

      <!-- Toolbar -->
      <AppCard padding="sm">
        <div class="flex flex-col sm:flex-row gap-3 items-start sm:items-center justify-between">
          <AppSearchInput
            v-model="medicosStore.searchQuery"
            placeholder="Buscar por nombre, licencia, especialidad..."
            class="sm:w-80"
          />
          <AppButton @click="abrirCrear">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            Nuevo médico
          </AppButton>
        </div>
      </AppCard>

      <!-- Stats -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <AppCard padding="sm">
          <p class="text-2xl font-bold" style="color: var(--text-primary)">{{ medicosStore.totalElements }}</p>
          <p class="text-xs mt-0.5" style="color: var(--text-muted)">Total médicos</p>
        </AppCard>
        <AppCard padding="sm">
          <p class="text-2xl font-bold text-green-600">
            {{ medicosStore.medicos.filter((m) => m.disponible).length }}
          </p>
          <p class="text-xs mt-0.5" style="color: var(--text-muted)">Disponibles</p>
        </AppCard>
        <AppCard padding="sm">
          <p class="text-2xl font-bold" style="color: var(--text-muted)">
            {{ medicosStore.medicos.filter((m) => !m.disponible).length }}
          </p>
          <p class="text-xs mt-0.5" style="color: var(--text-muted)">No disponibles</p>
        </AppCard>
        <AppCard padding="sm">
          <p class="text-2xl font-bold text-blue-600">{{ medicosStore.especialidades.length }}</p>
          <p class="text-xs mt-0.5" style="color: var(--text-muted)">Especialidades</p>
        </AppCard>
      </div>

      <!-- Grid de médicos + paginación -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ medicosStore.totalElements }} médico(s)
          </h2>
        </div>

        <div v-if="medicosStore.medicos.length > 0" class="p-4 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          <MedicoCard
            v-for="medico in medicosStore.medicos"
            :key="medico.id"
            :medico="medico"
            @edit="abrirEditar"
          />
        </div>
        <div v-else-if="!medicosStore.loading" class="py-12 text-center" style="color: var(--text-muted)">
          <svg class="w-12 h-12 mx-auto mb-3" style="color: var(--text-disabled)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
              d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          No se encontraron médicos
        </div>

        <!-- Paginación -->
        <div class="px-4 border-t" style="border-color: var(--border-color)">
          <AppPagination
            :page="medicosStore.page"
            :total-pages="medicosStore.totalPages"
            :total-elements="medicosStore.totalElements"
            :page-size="medicosStore.pageSize"
            :loading="medicosStore.loading"
            @change="medicosStore.irAPagina"
          />
        </div>
      </AppCard>
    </div>

    <AppModal
      v-model="showModal"
      :title="medicoEditando ? 'Editar médico' : 'Nuevo médico'"
      size="lg"
    >
      <MedicoForm
        :medico="medicoEditando"
        :especialidades="medicosStore.especialidades"
        :loading="loading"
        @submit="handleSubmit"
        @cancel="showModal = false"
      />
    </AppModal>
  </DashboardLayout>
</template>
