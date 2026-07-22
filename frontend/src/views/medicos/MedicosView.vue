<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppPagination from '@/components/ui/AppPagination.vue'

import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import EntitySummary from '@/components/common/EntitySummary.vue'
import EntityCard from '@/components/common/EntityCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'

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

const showModal = ref(false)
const medicoEditando = ref<Medico | null>(null)
const successMessage = ref('')
const loading = ref(false)

// Estadísticas para EntitySummary
const summaryItems = computed(() => [
  { label: 'Total médicos', value: medicosStore.totalElements, icon: '👨‍⚕️' },
  { label: 'Disponibles', value: medicosStore.medicos.filter((m) => m.disponible).length, icon: '✅' },
  { label: 'No disponibles', value: medicosStore.medicos.filter((m) => !m.disponible).length, icon: '⛔' },
  { label: 'Especialidades', value: medicosStore.especialidades.length, icon: '🏷️' },
])

// Paleta de avatares (igual que en MedicoCard)
const avatarPalette = [
  { bg: 'rgba(16,185,129,0.15)',  color: '#059669' },
  { bg: 'rgba(20,184,166,0.15)',  color: '#0d9488' },
  { bg: 'rgba(59,130,246,0.15)',  color: '#2563eb' },
  { bg: 'rgba(168,85,247,0.15)',  color: '#7c3aed' },
]

function getAvatarPalette(id: number) {
  return avatarPalette[id % avatarPalette.length]!
}

function getInitials(medico: Medico) {
  return `${medico.nombre[0] ?? ''}${medico.apellido[0] ?? ''}`.toUpperCase()
}

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
      <PageHeader title="Médicos" subtitle="Gestión del equipo médico" />
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

      <!-- Stats con EntitySummary -->
      <EntitySummary :items="summaryItems" />

      <!-- Toolbar con SearchToolbar -->
      <SearchToolbar
        v-model:search="medicosStore.searchQuery"
        search-placeholder="Buscar por nombre, licencia, especialidad..."
        :show-new-button="true"
        new-button-label="Nuevo médico"
        @new="abrirCrear"
      />

      <!-- Grid de médicos -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ medicosStore.totalElements }} médico(s)
          </h2>
        </div>

        <div class="p-4 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          <template v-if="medicosStore.medicos.length > 0">
            <EntityCard
              v-for="medico in medicosStore.medicos"
              :key="medico.id"
              :title="`${medico.nombre} ${medico.apellido}`"
              :subtitle="`Lic. ${medico.numeroLicencia}`"
              :status="{
                label: medico.disponible ? 'Disponible' : 'No disponible',
                variant: medico.disponible ? 'success' : 'neutral',
                dot: true,
              }"
            >
              <template #avatar>
                <div
                  class="w-12 h-12 sm:w-14 sm:h-14 rounded-2xl flex items-center justify-center text-base sm:text-lg font-bold shrink-0"
                  :style="{
                    backgroundColor: getAvatarPalette(medico.id).bg,
                    color: getAvatarPalette(medico.id).color,
                  }"
                >
                  {{ getInitials(medico) }}
                </div>
              </template>

              <template #details>
                <div class="flex flex-wrap gap-1.5 mt-2">
                  <span
                    v-for="esp in medico.especialidades"
                    :key="esp.id"
                    class="px-2 py-0.5 text-xs rounded-full font-medium vg-esp-tag"
                    style="background-color: rgba(16,185,129,0.12); color: #059669;"
                  >
                    {{ esp.nombre }}
                  </span>
                </div>
                <div class="flex flex-col sm:flex-row sm:items-center gap-1.5 sm:gap-4 mt-3 text-xs" style="color: var(--text-muted)">
                  <span class="flex items-center gap-1 min-w-0">
                    <svg class="w-3.5 h-3.5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                    </svg>
                    <span class="truncate">{{ medico.email }}</span>
                  </span>
                  <span class="flex items-center gap-1 shrink-0">
                    <svg class="w-3.5 h-3.5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                    </svg>
                    {{ medico.telefono }}
                  </span>
                </div>
              </template>

              <template #actions>
                <div class="flex-1"></div>
                <AppButton variant="ghost" size="sm" title="Editar médico" @click="abrirEditar(medico)">
                  ✏️
                </AppButton>
              </template>
            </EntityCard>
          </template>

          <EmptyState
            v-else-if="!medicosStore.loading"
            class="col-span-full"
            icon="👨‍⚕️"
            title="No se encontraron médicos"
            message="No hay médicos registrados que coincidan con la búsqueda"
          />
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
