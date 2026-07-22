<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppTable from '@/components/ui/AppTable.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppButton from '@/components/ui/AppButton.vue'

import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'

import ClienteForm from '@/components/clientes/ClienteForm.vue'
import { useClientesStore } from '@/stores/clientes.store'
import type { Cliente, ClienteFormData } from '@/types'

// Definición de columnas para AppTable
const columns = [
  { key: 'cliente', label: 'Cliente' },
  { key: 'documento', label: 'Documento' },
  { key: 'contacto', label: 'Contacto' },
  { key: 'mascotas', label: 'Mascotas', align: 'center' },
  { key: 'estado', label: 'Estado', align: 'center' },
] as const

const clientesStore = useClientesStore()

onMounted(() => clientesStore.cargar({ page: 0 }))

// Recargar al cambiar filtros — vuelve siempre a página 0
watch([() => clientesStore.searchQuery, () => clientesStore.filtroEstado], () => {
  clientesStore.cargar({ page: 0 })
})

const showModal = ref(false)
const clienteEditando = ref<Cliente | null>(null)
const successMessage = ref('')
const loading = ref(false)

const estadoOptions = [
  { value: 'todos', label: 'Todos los estados' },
  { value: 'activo', label: 'Activos' },
  { value: 'inactivo', label: 'Inactivos' },
]

function abrirCrear() {
  clienteEditando.value = null
  showModal.value = true
}

function abrirEditar(cliente: Cliente) {
  clienteEditando.value = cliente
  showModal.value = true
}

async function handleSubmit(data: ClienteFormData) {
  loading.value = true
  try {
    if (clienteEditando.value) {
      await clientesStore.actualizar(clienteEditando.value.id, data)
      successMessage.value = 'Cliente actualizado correctamente'
    } else {
      await clientesStore.crear(data)
      successMessage.value = 'Cliente registrado correctamente'
    }
    showModal.value = false
    setTimeout(() => (successMessage.value = ''), 3000)
  } finally {
    loading.value = false
  }
}

async function handleToggleEstado(cliente: Cliente) {
  await clientesStore.eliminar(cliente.id)
  successMessage.value = `Cliente ${cliente.nombre} ${cliente.apellido} desactivado`
  setTimeout(() => (successMessage.value = ''), 3000)
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Clientes" subtitle="Gestión de clientes registrados" />
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="clientesStore.error" type="error" dismissible @dismiss="clientesStore.limpiarError()">
          {{ clientesStore.error }}
        </AppAlert>
      </Transition>
      <Transition name="fade">
        <AppAlert v-if="successMessage" type="success" dismissible @dismiss="successMessage = ''">
          {{ successMessage }}
        </AppAlert>
      </Transition>

      <!-- Toolbar con SearchToolbar -->
      <SearchToolbar
        v-model:search="clientesStore.searchQuery"
        search-placeholder="Buscar por nombre, documento, email..."
        :show-new-button="true"
        new-button-label="Nuevo cliente"
        @new="abrirCrear"
      >
        <template #filters>
          <AppSelect
            v-model="clientesStore.filtroEstado"
            :options="estadoOptions"
            class="w-full sm:w-48"
          />
        </template>
      </SearchToolbar>

      <!-- Tabla con AppTable -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ clientesStore.totalElements }} cliente(s)
          </h2>
        </div>

        <AppTable
          :columns="columns"
          :rows="clientesStore.clientes"
          :loading="clientesStore.loading"
          empty-message="No se encontraron clientes"
          @row-click="abrirEditar"
        >
          <!-- Columna Cliente (avatar + nombre + ciudad) -->
          <template #cell-cliente="{ row }">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-full vg-client-avatar flex items-center justify-center font-semibold text-sm shrink-0">
                {{ row.nombre[0] }}{{ row.apellido[0] }}
              </div>
              <div>
                <p class="font-medium" style="color: var(--text-primary)">{{ row.nombre }} {{ row.apellido }}</p>
                <p class="text-xs" style="color: var(--text-muted)">{{ row.ciudad }}</p>
              </div>
            </div>
          </template>

          <!-- Columna Documento -->
          <template #cell-documento="{ row }">
            <span class="text-xs" style="color: var(--text-muted)">{{ row.tipoDocumento }}</span>
            <p>{{ row.numeroDocumento }}</p>
          </template>

          <!-- Columna Contacto -->
          <template #cell-contacto="{ row }">
            <p style="color: var(--text-secondary)">{{ row.email }}</p>
            <p class="text-xs" style="color: var(--text-muted)">{{ row.telefono }}</p>
          </template>

          <!-- Columna Mascotas (badge) -->
          <template #cell-mascotas="{ row }">
            <AppBadge :variant="row.numeroMascotas > 0 ? 'primary' : 'neutral'">
              {{ row.numeroMascotas }}
            </AppBadge>
          </template>

          <!-- Columna Estado (badge con dot) -->
          <template #cell-estado="{ row }">
            <AppBadge :variant="row.estado === 'activo' ? 'success' : 'neutral'" dot>
              {{ row.estado === 'activo' ? 'Activo' : 'Inactivo' }}
            </AppBadge>
          </template>

          <!-- Acciones -->
          <template #actions="{ row }">
            <div class="flex items-center justify-end gap-1">
              <AppButton
                variant="ghost"
                size="sm"
                title="Editar"
                aria-label="Editar cliente"
                @click.stop="abrirEditar(row)"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </AppButton>
              <AppButton
                :variant="row.estado === 'activo' ? 'danger' : 'ghost'"
                size="sm"
                :title="row.estado === 'activo' ? 'Inactivar' : 'Activar'"
                :aria-label="row.estado === 'activo' ? 'Inactivar cliente' : 'Activar cliente'"
                @click.stop="handleToggleEstado(row)"
              >
                <svg v-if="row.estado === 'activo'" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                </svg>
                <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </AppButton>
            </div>
          </template>
        </AppTable>

        <!-- Paginación -->
        <div class="px-4 border-t" style="border-color: var(--border-color)">
          <AppPagination
            :page="clientesStore.page"
            :total-pages="clientesStore.totalPages"
            :total-elements="clientesStore.totalElements"
            :page-size="clientesStore.pageSize"
            :loading="clientesStore.loading"
            @change="clientesStore.irAPagina"
          />
        </div>
      </AppCard>
    </div>

    <AppModal
      v-model="showModal"
      :title="clienteEditando ? 'Editar cliente' : 'Nuevo cliente'"
      size="lg"
    >
      <ClienteForm
        :cliente="clienteEditando"
        :loading="loading"
        @submit="handleSubmit"
        @cancel="showModal = false"
      />
    </AppModal>
  </DashboardLayout>
</template>