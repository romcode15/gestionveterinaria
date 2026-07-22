<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'

import DashboardLayout from '@/layouts/DashboardLayout.vue'

import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppTable from '@/components/ui/AppTable.vue'
import AppBadge from '@/components/ui/AppBadge.vue'

import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import EntityCard from '@/components/common/EntityCard.vue'

import MascotaAvatar from '@/components/mascotas/MascotaAvatar.vue'
import MascotaForm from '@/components/mascotas/MascotaForm.vue'

import { useMascotasStore } from '@/stores/mascotas.store'
import { useClientesStore } from '@/stores/clientes.store'

import type { Mascota, MascotaFormData } from '@/types'

// Columnas de la tabla de mascotas
const columns = [
  {
    key: 'mascota',
    label: 'Mascota',
  },
  {
    key: 'especie_raza',
    label: 'Especie / Raza',
  },
  {
    key: 'sexo',
    label: 'Sexo',
    align: 'center',
  },
  {
    key: 'edad',
    label: 'Edad',
    align: 'center',
  },
  {
    key: 'peso',
    label: 'Peso',
    align: 'center',
  },
  {
    key: 'propietario',
    label: 'Propietario',
  },
] as const

// Calcula edad de la mascota
function calcularEdad(fechaNacimiento?: string): string {
  if (!fechaNacimiento) return '-'
  const hoy = new Date()
  const nacimiento = new Date(fechaNacimiento)
  const años = hoy.getFullYear() - nacimiento.getFullYear()
  if (años === 0) {
    const meses = hoy.getMonth() - nacimiento.getMonth()
    return `${meses < 0 ? meses + 12 : meses}m`
  }
  return `${años}a`
}

const mascotasStore = useMascotasStore()
const clientesStore = useClientesStore()

// Carga inicial de información
onMounted(async () => {
  await Promise.all([
    mascotasStore.cargar({ page: 0 }),
    mascotasStore.cargarCatalogos(),
    clientesStore.cargar({ size: 200 }),
  ])
})

// Búsqueda dinámica
watch(
  () => mascotasStore.searchQuery,
  () => {
    mascotasStore.cargar({ page: 0 })
  },
)

// Estado de la vista
const showModal = ref(false)
const mascotaEditando = ref<Mascota | null>(null)
const successMessage = ref('')
const loading = ref(false)
const vistaGrid = ref(false)

// Opciones del filtro de especies
const especieOptions = computed(() => [
  {
    value: '',
    label: 'Todas las especies',
  },
  ...mascotasStore.especies.map((e) => ({
    value: e.id,
    label: e.nombre,
  })),
])

// Abrir formulario nuevo
function abrirCrear() {
  mascotaEditando.value = null
  showModal.value = true
}

// Abrir formulario edición
function abrirEditar(mascota: Mascota) {
  mascotaEditando.value = mascota
  showModal.value = true
}

// Guardar mascota
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
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Mascotas" subtitle="Gestión de pacientes veterinarios" />
    </template>

    <div class="space-y-4">
      <!-- ALERTAS -->
      <Transition name="fade">
        <AppAlert
          v-if="mascotasStore.error"
          type="error"
          dismissible
          @dismiss="mascotasStore.limpiarError()"
        >
          {{ mascotasStore.error }}
        </AppAlert>
      </Transition>

      <Transition name="fade">
        <AppAlert
          v-if="successMessage"
          type="success"
          dismissible
          @dismiss="successMessage = ''"
        >
          {{ successMessage }}
        </AppAlert>
      </Transition>

      <!-- TOOLBAR con SearchToolbar -->
      <SearchToolbar
        v-model:search="mascotasStore.searchQuery"
        search-placeholder="Buscar por nombre, especie, propietario..."
        :show-new-button="true"
        new-button-label="Nueva mascota"
        @new="abrirCrear"
      >
        <template #filters>
          <AppSelect
            :model-value="mascotasStore.filtroEspecieId ?? ''"
            :options="especieOptions"
            class="w-full sm:w-48"
            @update:model-value="(v) => { mascotasStore.filtroEspecieId = v ? Number(v) : null }"
          />
        </template>

        <template #actions>
          <!-- Toggle de vista tabla/grid -->
          <div class="flex rounded-lg overflow-hidden border" style="border-color: var(--border-color)">
            <button
              @click="vistaGrid = false"
              :class="[
                'px-3 py-2 text-sm transition-colors',
                !vistaGrid ? 'bg-(--color-primary) text-white' : 'hover:bg-(--bg-hover)'
              ]"
              :style="vistaGrid ? { color: 'var(--text-muted)' } : {}"
              aria-label="Vista tabla"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
              </svg>
            </button>
            <button
              @click="vistaGrid = true"
              :class="[
                'px-3 py-2 text-sm transition-colors',
                vistaGrid ? 'bg-(--color-primary) text-white' : 'hover:bg-(--bg-hover)'
              ]"
              :style="!vistaGrid ? { color: 'var(--text-muted)' } : {}"
              aria-label="Vista tarjetas"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
              </svg>
            </button>
          </div>
        </template>
      </SearchToolbar>

      <!-- CONTENIDO -->
      <AppCard padding="none">
        <div
          class="px-6 py-4 border-b"
          style="border-color: var(--border-color)"
        >
          <h2 class="font-semibold" style="color:var(--text-primary)">
            {{ mascotasStore.totalElements }} mascota(s)
          </h2>
        </div>

        <!-- ===========================
             TABLA
        ============================ -->
        <div v-if="!vistaGrid">
          <AppTable
            :columns="columns"
            :rows="mascotasStore.mascotas"
            :loading="mascotasStore.loading"
            empty-message="No se encontraron mascotas"
            @row-click="abrirEditar"
          >
            <!-- MASCOTA -->
            <template #cell-mascota="{ row }">
              <div class="flex items-center gap-3">
                <MascotaAvatar :especie-nombre="row.especie.nombre" :nombre="row.nombre" size="sm" />
                <div>
                  <p class="font-medium" style="color:var(--text-primary)">{{ row.nombre }}</p>
                  <p class="text-xs" style="color:var(--text-muted)">{{ row.clienteNombre }}</p>
                </div>
              </div>
            </template>

            <!-- ESPECIE -->
            <template #cell-especie_raza="{ row }">
              <p style="color:var(--text-secondary)">{{ row.especie.nombre }}</p>
              <p class="text-xs" style="color:var(--text-muted)">{{ row.raza.nombre }}</p>
            </template>

            <!-- SEXO -->
            <template #cell-sexo="{ row }">
              <AppBadge :variant="row.sexo === 'macho' ? 'info' : 'warning'" size="sm">
                {{ row.sexo === 'macho' ? '♂ Macho' : '♀ Hembra' }}
              </AppBadge>
            </template>

            <!-- EDAD -->
            <template #cell-edad="{ row }">
              {{ calcularEdad(row.fechaNacimiento) }}
            </template>

            <!-- PESO -->
            <template #cell-peso="{ row }">
              {{ row.peso ? row.peso + ' kg' : '-' }}
            </template>

            <!-- PROPIETARIO -->
            <template #cell-propietario="{ row }">
              {{ row.clienteNombre }}
            </template>

            <!-- ACCIONES -->
            <template #actions="{ row }">
              <button @click.stop="abrirEditar(row)" class="p-1.5 rounded-lg vg-icon-btn" title="Editar mascota">
                ✏️
              </button>
            </template>
          </AppTable>
        </div>

        <!-- ===========================
             GRID ENTITY CARD
        ============================ -->
        <div
          v-else
          class="p-4 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4"
        >
          <EntityCard
            v-for="mascota in mascotasStore.mascotas"
            :key="mascota.id"
            :title="mascota.nombre"
            :subtitle="`${mascota.especie.nombre} - ${mascota.raza.nombre}`"
            :status="{
              label: mascota.estado ? 'Activo' : 'Inactivo',
              variant: mascota.estado ? 'success' : 'danger',
              dot: true,
            }"
          >
            <template #avatar>
              <MascotaAvatar :especie-nombre="mascota.especie.nombre" :nombre="mascota.nombre" size="md" />
            </template>

            <template #details>
              <div class="space-y-1 text-sm">
                <p style="color:var(--text-secondary)">
                  Sexo:
                  <strong>{{ mascota.sexo }}</strong>
                </p>
                <p style="color:var(--text-secondary)">
                  Edad:
                  <strong>{{ calcularEdad(mascota.fechaNacimiento) }}</strong>
                </p>
                <p style="color:var(--text-secondary)">
                  Peso:
                  <strong>{{ mascota.peso ? mascota.peso + ' kg' : '-' }}</strong>
                </p>
                <p style="color:var(--text-secondary)">
                  Propietario:
                  <strong>{{ mascota.clienteNombre }}</strong>
                </p>
              </div>
            </template>

            <template #actions>
              <button @click="abrirEditar(mascota)" class="p-1.5 rounded-lg vg-icon-btn">
                ✏️
              </button>
            </template>
          </EntityCard>

          <div
            v-if="mascotasStore.mascotas.length === 0 && !mascotasStore.loading"
            class="col-span-full py-12 text-center"
            style="color:var(--text-muted)"
          >
            🐾
            <p>No se encontraron mascotas</p>
          </div>
        </div>

        <!-- PAGINACION -->
        <div class="px-4 border-t" style="border-color:var(--border-color)">
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

    <!-- MODAL -->
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