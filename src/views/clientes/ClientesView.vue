<script setup lang="ts">
import { ref, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSearchInput from '@/components/ui/AppSearchInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import ClienteTable from '@/components/clientes/ClienteTable.vue'
import ClienteForm from '@/components/clientes/ClienteForm.vue'
import { useClientesStore } from '@/stores/clientes.store'
import type { Cliente, ClienteFormData } from '@/types'

const clientesStore = useClientesStore()

onMounted(() => clientesStore.cargar())

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
  await clientesStore.toggleEstado(cliente.id)
  successMessage.value = `Cliente ${cliente.estado === 'activo' ? 'inactivado' : 'activado'} correctamente`
  setTimeout(() => (successMessage.value = ''), 3000)
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <div>
        <h1 class="text-lg font-semibold text-slate-800">Clientes</h1>
        <p class="text-xs text-slate-500">Gestión de clientes registrados</p>
      </div>
    </template>

    <div class="space-y-4">
      <!-- Alert de éxito -->
      <Transition name="fade">
        <AppAlert v-if="successMessage" type="success" dismissible @dismiss="successMessage = ''">
          {{ successMessage }}
        </AppAlert>
      </Transition>

      <!-- Toolbar -->
      <AppCard padding="sm">
        <div class="flex flex-col gap-3">
          <!-- Fila 1: buscador + filtro (siempre apilados en móvil, fila en sm+) -->
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
          <!-- Fila 2: botón alineado a la derecha -->
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
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h2 class="font-semibold text-slate-800">
            {{ clientesStore.clientesFiltrados.length }} cliente(s)
          </h2>
        </div>
        <ClienteTable
          :clientes="clientesStore.clientesFiltrados"
          :loading="clientesStore.loading"
          @edit="abrirEditar"
          @toggle-estado="handleToggleEstado"
        />
      </AppCard>
    </div>

    <!-- Modal -->
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
