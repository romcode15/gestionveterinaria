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
import EmptyState from '@/components/common/EmptyState.vue'
import TableViewLayout from '@/components/common/TableViewLayout.vue'
import { useFiltros } from '@/composables/useFiltros'
import AppBadge from '@/components/ui/AppBadge.vue'

import MedicoForm from '@/components/medicos/MedicoForm.vue'
import { useMedicosStore } from '@/stores/medicos.store'
import type { Medico, MedicoFormData } from '@/types'

const medicosStore = useMedicosStore()

// Composable: debounce 400ms en la búsqueda de texto
const { busqueda } = useFiltros({ onCargar: (page) => medicosStore.cargar({ page }) })
watch(busqueda, (val) => { medicosStore.searchQuery = val })

onMounted(async () => {
  await Promise.all([
    medicosStore.cargar({ page: 0 }),
    medicosStore.cargarCatalogos(),
  ])
})
const showModal = ref(false)
const medicoEditando = ref<Medico | null>(null)
const successMessage = ref('')
const loading = ref(false)

const summaryItems = computed(() => [
  { label: 'Total médicos',  value: medicosStore.totalElements,
    svgPath: 'M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2',
    iconColor: '#6366f1', iconBg: 'rgba(99,102,241,0.12)' },
  { label: 'Disponibles',    value: medicosStore.medicos.filter((m) => m.disponible).length,
    svgPath: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
    iconColor: '#059669', iconBg: 'rgba(5,150,105,0.12)' },
  { label: 'No disponibles', value: medicosStore.medicos.filter((m) => !m.disponible).length,
    svgPath: 'M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636',
    iconColor: '#ef4444', iconBg: 'rgba(239,68,68,0.12)' },
  { label: 'Especialidades', value: medicosStore.especialidades.length,
    svgPath: 'M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z',
    iconColor: '#0d9488', iconBg: 'rgba(13,148,136,0.12)' },
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

function cerrarModal() {
  showModal.value = false
  // Limpiar después de que cierre la animación
  setTimeout(() => { medicoEditando.value = null }, 300)
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
    cerrarModal()
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

    <TableViewLayout>
      <template #toolbar>
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

        <EntitySummary :items="summaryItems" />

        <SearchToolbar
          v-model:search="busqueda"
          search-placeholder="Buscar por nombre, licencia, especialidad..."
          :show-new-button="true"
          new-button-label="Nuevo médico"
          @new="abrirCrear"
        />
      </template>

      <template #content>
        <AppCard fill-height padding="none">
          <template #header>
            <div class="px-6 py-4 border-b shrink-0" style="border-color: var(--border-color)">
              <h2 class="font-semibold" style="color: var(--text-primary)">
                {{ medicosStore.totalElements }} médico(s)
              </h2>
            </div>
          </template>

          <div class="p-4 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
            <template v-if="medicosStore.medicos.length > 0">
              <div
                v-for="medico in medicosStore.medicos"
                :key="medico.id"
                class="vg-card rounded-2xl p-4 sm:p-5 hover:shadow-md transition-shadow"
              >
                <div class="flex items-start gap-3 sm:gap-4">
                  <div
                    class="w-12 h-12 sm:w-14 sm:h-14 rounded-2xl flex items-center justify-center text-base sm:text-lg font-bold shrink-0"
                    :style="{ backgroundColor: getAvatarPalette(medico.id).bg, color: getAvatarPalette(medico.id).color }"
                  >
                    {{ getInitials(medico) }}
                  </div>
                  <div class="flex-1 min-w-0">
                    <div class="flex flex-wrap items-start justify-between gap-2">
                      <div class="min-w-0">
                        <h3 class="font-semibold truncate" style="color: var(--text-primary)">
                          {{ medico.nombre }} {{ medico.apellido }}
                        </h3>
                        <p class="text-xs mt-0.5 truncate" style="color: var(--text-muted)">
                          Lic. {{ medico.numeroLicencia }}
                        </p>
                      </div>
                      <AppBadge :variant="medico.disponible ? 'success' : 'neutral'" dot class="shrink-0">
                        {{ medico.disponible ? 'Disponible' : 'No disponible' }}
                      </AppBadge>
                    </div>
                    <div class="flex flex-wrap gap-1.5 mt-2">
                      <span
                        v-for="esp in medico.especialidades" :key="esp.id"
                        class="px-2 py-0.5 text-xs rounded-full font-medium"
                        style="background-color: rgba(16,185,129,0.12); color: #059669;"
                      >{{ esp.nombre }}</span>
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
                    <div v-if="medico.username" class="mt-2">
                      <span class="text-xs font-mono px-2 py-0.5 rounded flex items-center gap-1 w-fit"
                        style="background: var(--bg-hover); color: var(--text-secondary)">
                        <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                        </svg>
                        {{ medico.username }}
                      </span>
                    </div>
                    <div class="mt-3 pt-3 flex items-center justify-end" style="border-top: 1px solid var(--border-default)">
                      <AppButton variant="ghost" size="sm" title="Editar médico" @click="abrirEditar(medico)">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                        </svg>
                        Editar
                      </AppButton>
                    </div>
                  </div>
                </div>
              </div>
            </template>

            <EmptyState
              v-else-if="!medicosStore.loading"
              class="col-span-full"
              title="No se encontraron médicos"
              message="No hay médicos registrados que coincidan con la búsqueda"
            />
          </div>

          <template #footer>
            <div class="border-t" style="border-color: var(--border-color)">
              <AppPagination
                :page="medicosStore.page"
                :total-pages="medicosStore.totalPages"
                :total-elements="medicosStore.totalElements"
                :page-size="medicosStore.pageSize"
                :loading="medicosStore.loading"
                @change="medicosStore.irAPagina"
              />
            </div>
          </template>
        </AppCard>
      </template>
    </TableViewLayout>

    <AppModal
      v-model="showModal"
      :title="medicoEditando ? 'Editar médico' : 'Nuevo médico'"
      size="lg"
      @close="cerrarModal"
    >
      <MedicoForm
        :key="medicoEditando?.id ?? 'nuevo'"
        :medico="medicoEditando"
        :especialidades="medicosStore.especialidades"
        :loading="loading"
        @submit="handleSubmit"
        @cancel="cerrarModal"
      />
    </AppModal>
  </DashboardLayout>
</template>
