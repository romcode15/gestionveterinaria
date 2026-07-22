<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSearchInput from '@/components/ui/AppSearchInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import ClienteTable from '@/components/clientes/ClienteTable.vue'
import ClienteForm from '@/components/clientes/ClienteForm.vue'
import { useClientesStore } from '@/stores/clientes.store'
import type { Cliente, ClienteFormData } from '@/types'

const clientesStore = useClientesStore()

onMounted(() => clientesStore.cargar({ page: 0 }))

// Recargar al cambiar filtros — vuelve siempre a página 0
watch([() => clientesStore.searchQuery, () => clientesStore.filtroEstado], () => {
  clientesStore.cargar({ page: 0 })
})

const showModal    = ref(false)
const clienteEditando = ref<Cliente | null>(null)
const successMessage  = ref('')
const loading         = ref(false)

const estadoOptions = [
  { value: 'todos',    label: 'Todos los estados' },
  { value: 'activo',   label: 'Activos' },
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

async function handleEliminar(cliente: Cliente) {
  await clientesStore.eliminar(cliente.id)
  successMessage.value = `Cliente ${cliente.nombre} ${cliente.apellido} desactivado`
  setTimeout(() => (successMessage.value = ''), 3000)
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <div>
        <h1 class="text-lg font-semibold" style="color: var(--text-primary)">Clientes</h1>
        <p class="text-xs" style="color: var(--text-muted)">Gestión de clientes registrados</p>
      </div>
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

      <!-- Toolbar -->
      <AppCard padding="sm">
        <div class="flex flex-col gap-3">
          <div class="flex flex-col sm:flex-row gap-3 w-full">
            <AppSearchInput
              v-model="clientesStore.searchQuery"
              placeholder="Buscar por nombre, documento, email..."
              class="w-full sm:flex-1"
            />
            <AppSelect
              v-model="clientesStore.filtroEstado"
              :options="estadoOptions"
              class="w-full sm:w-48"
            />
          </div>
          <div class="flex justify-end">
            <AppButton @click="abrirCrear" class="w-full sm:w-auto">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              Nuevo cliente
            </AppButton>
          </div>
        </div>
      </AppCard>

      <!-- Tabla -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ clientesStore.totalElements }} cliente(s)
          </h2>
        </div>

        <ClienteTable
          :clientes="clientesStore.clientes"
          :loading="clientesStore.loading"
          @edit="abrirEditar"
          @toggle-estado="handleEliminar"
        />

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
