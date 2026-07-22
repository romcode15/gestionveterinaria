<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSearchInput from '@/components/ui/AppSearchInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import MascotaTable from '@/components/mascotas/MascotaTable.vue'
import MascotaCard from '@/components/mascotas/MascotaCard.vue'
import MascotaForm from '@/components/mascotas/MascotaForm.vue'
import { useMascotasStore } from '@/stores/mascotas.store'
import { useClientesStore } from '@/stores/clientes.store'
import type { Mascota, MascotaFormData } from '@/types'

const mascotasStore = useMascotasStore()
const clientesStore = useClientesStore()

onMounted(async () => {
  await Promise.all([
    mascotasStore.cargar({ page: 0 }),
    mascotasStore.cargarCatalogos(),
    clientesStore.cargar({ size: 200 }), // lista para el formulario
  ])
})

watch(() => mascotasStore.searchQuery, () => {
  mascotasStore.cargar({ page: 0 })
})

const showModal        = ref(false)
const mascotaEditando  = ref<Mascota | null>(null)
const successMessage   = ref('')
const loading          = ref(false)
const vistaGrid        = ref(false)

const especieOptions = computed(() => [
  { value: '', label: 'Todas las especies' },
  ...mascotasStore.especies.map((e) => ({ value: e.id, label: e.nombre })),
])

function abrirCrear() {
  mascotaEditando.value = null
  showModal.value = true
}

function abrirEditar(mascota: Mascota) {
  mascotaEditando.value = mascota
  showModal.value = true
}

async function handleSubmit(data: MascotaFormData) {
  loading.value = true
  try {
    if (mascotaEditando.value) {
      await mascotasStore.actualizar(mascotaEditando.value.id, data)
      successMessage.value = 'Mascota actualizada correctamente'
    } else {
      await mascotasStore.crear(data)
      successMessage.value = 'Mascota registrada correctamente'
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
        <h1 class="text-lg font-semibold" style="color: var(--text-primary)">Mascotas</h1>
        <p class="text-xs" style="color: var(--text-muted)">Gestión de pacientes veterinarios</p>
      </div>
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="mascotasStore.error" type="error" dismissible @dismiss="mascotasStore.limpiarError()">
          {{ mascotasStore.error }}
        </AppAlert>
      </Transition>
      <Transition name="fade">
        <AppAlert v-if="successMessage" type="success" dismissible @dismiss="successMessage = ''">
          {{ successMessage }}
        </AppAlert>
      </Transition>

      <!-- Toolbar -->
      <AppCard padding="sm">
        <div class="flex flex-col gap-3">
          <div class="flex flex-col sm:flex-row gap-3 w-full">
            <AppSearchInput
              v-model="mascotasStore.searchQuery"
              placeholder="Buscar por nombre, especie, propietario..."
              class="w-full sm:flex-1"
            />
            <AppSelect
              :model-value="mascotasStore.filtroEspecieId ?? ''"
              :options="especieOptions"
              class="w-full sm:w-48"
              @update:model-value="(v) => { mascotasStore.filtroEspecieId = v ? Number(v) : null }"
            />
          </div>
          <div class="flex items-center justify-between gap-3 sm:justify-end">
            <!-- Toggle tabla/grid -->
            <div class="flex rounded-lg overflow-hidden border" style="border-color: var(--border-color)">
              <button
                @click="vistaGrid = false"
                :class="['px-3 py-2 text-sm transition-colors', !vistaGrid ? 'bg-(--color-primary) text-white' : 'hover:bg-(--bg-hover)']"
                :style="vistaGrid ? { color: 'var(--text-muted)' } : {}"
                aria-label="Vista tabla"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
                </svg>
              </button>
              <button
                @click="vistaGrid = true"
                :class="['px-3 py-2 text-sm transition-colors', vistaGrid ? 'bg-(--color-primary) text-white' : 'hover:bg-(--bg-hover)']"
                :style="!vistaGrid ? { color: 'var(--text-muted)' } : {}"
                aria-label="Vista tarjetas"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                </svg>
              </button>
            </div>
            <AppButton @click="abrirCrear" class="flex-1 sm:flex-none">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              Nueva mascota
            </AppButton>
          </div>
        </div>
      </AppCard>

      <!-- Contenido -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ mascotasStore.totalElements }} mascota(s)
          </h2>
        </div>

        <!-- Vista tabla -->
        <div v-if="!vistaGrid">
          <MascotaTable
            :mascotas="mascotasStore.mascotas"
            :loading="mascotasStore.loading"
            @edit="abrirEditar"
          />
        </div>

        <!-- Vista grid -->
        <div v-else class="p-4 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          <template v-if="mascotasStore.mascotas.length > 0">
            <MascotaCard
              v-for="mascota in mascotasStore.mascotas"
              :key="mascota.id"
              :mascota="mascota"
              @edit="abrirEditar"
            />
          </template>
          <div v-else-if="!mascotasStore.loading" class="col-span-full py-12 text-center" style="color: var(--text-muted)">
            <span class="text-4xl block mb-2">🐾</span>
            No se encontraron mascotas
          </div>
        </div>

        <!-- Paginación -->
        <div class="px-4 border-t" style="border-color: var(--border-color)">
          <AppPagination
            :page="mascotasStore.page"
            :total-pages="mascotasStore.totalPages"
            :total-elements="mascotasStore.totalElements"
            :page-size="mascotasStore.pageSize"
            :loading="mascotasStore.loading"
            @change="mascotasStore.irAPagina"
          />
        </div>
      </AppCard>
    </div>

    <AppModal
      v-model="showModal"
      :title="mascotaEditando ? 'Editar mascota' : 'Nueva mascota'"
      size="lg"
    >
      <MascotaForm
        :mascota="mascotaEditando"
        :especies="mascotasStore.especies"
        :razas="mascotasStore.razas"
        :clientes="clientesStore.clientes"
        :loading="loading"
        @submit="handleSubmit"
        @cancel="showModal = false"
      />
    </AppModal>
  </DashboardLayout>
</template>
