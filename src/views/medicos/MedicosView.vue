<script setup lang="ts">
import { ref } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSearchInput from '@/components/ui/AppSearchInput.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import MedicoCard from '@/components/medicos/MedicoCard.vue'
import MedicoForm from '@/components/medicos/MedicoForm.vue'
import { useMedicosStore } from '@/stores/medicos.store'
import type { Medico, MedicoFormData } from '@/types'

const medicosStore = useMedicosStore()

const showModal = ref(false)
const medicoEditando = ref<Medico | null>(null)
const successMessage = ref('')
const loading = ref(false)

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
        <h1 class="text-lg font-semibold text-slate-800">Médicos</h1>
        <p class="text-xs text-slate-500">Gestión del equipo médico</p>
      </div>
    </template>

    <div class="space-y-4">
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
          <p class="text-2xl font-bold text-slate-800">{{ medicosStore.medicos.length }}</p>
          <p class="text-xs text-slate-500 mt-0.5">Total médicos</p>
        </AppCard>
        <AppCard padding="sm">
          <p class="text-2xl font-bold text-primary-600">
            {{ medicosStore.medicos.filter((m) => m.disponible).length }}
          </p>
          <p class="text-xs text-slate-500 mt-0.5">Disponibles</p>
        </AppCard>
        <AppCard padding="sm">
          <p class="text-2xl font-bold text-slate-400">
            {{ medicosStore.medicos.filter((m) => !m.disponible).length }}
          </p>
          <p class="text-xs text-slate-500 mt-0.5">No disponibles</p>
        </AppCard>
        <AppCard padding="sm">
          <p class="text-2xl font-bold text-secondary-600">
            {{ medicosStore.especialidades.length }}
          </p>
          <p class="text-xs text-slate-500 mt-0.5">Especialidades</p>
        </AppCard>
      </div>

      <!-- Grid de médicos -->
      <div v-if="medicosStore.medicosFiltrados.length > 0" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
        <MedicoCard
          v-for="medico in medicosStore.medicosFiltrados"
          :key="medico.id"
          :medico="medico"
          @edit="abrirEditar"
        />
      </div>
      <AppCard v-else class="py-12 text-center text-slate-400">
        <svg class="w-12 h-12 text-slate-300 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
        </svg>
        No se encontraron médicos
      </AppCard>
    </div>

    <!-- Modal -->
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
