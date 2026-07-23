<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppTable from '@/components/ui/AppTable.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppButton from '@/components/ui/AppButton.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import UsuarioForm from '@/components/usuarios/UsuarioForm.vue'
import { useUsuariosStore } from '@/stores/usuarios.store'
import type { UsuarioFormData } from '@/types'
import type { UsuarioListItem } from '@/services/usuarios.service'

const columns = [
  { key: 'usuario',  label: 'Usuario' },
  { key: 'username', label: 'Nombre de usuario' },
  { key: 'email',    label: 'Correo' },
  { key: 'roles',    label: 'Roles', align: 'center' },
  { key: 'estado',   label: 'Estado', align: 'center' },
] as const

const usuariosStore = useUsuariosStore()

onMounted(() => usuariosStore.cargar())

// Filtro de rol reactivo
watch(() => usuariosStore.filtroRol, () => {/* computed filters automatically */})

const showModal       = ref(false)
const usuarioEditando = ref<UsuarioListItem | null>(null)
const successMessage  = ref('')
const loading         = ref(false)
const showDeleteConfirm = ref(false)
const usuarioAEliminar  = ref<UsuarioListItem | null>(null)

const rolOptions = [
  { value: 'todos',         label: 'Todos los roles' },
  { value: 'admin',         label: 'Administrador' },
  { value: 'veterinario',   label: 'Veterinario' },
  { value: 'recepcionista', label: 'Recepcionista' },
  { value: 'auxiliar',      label: 'Auxiliar' },
  { value: 'cliente',       label: 'Cliente' },
]

const rolVariant: Record<string, 'primary' | 'success' | 'warning' | 'danger' | 'neutral'> = {
  admin:         'danger',
  veterinario:   'primary',
  recepcionista: 'success',
  auxiliar:      'warning',
  cliente:       'neutral',
}

const rolLabels: Record<string, string> = {
  admin:         'Admin',
  veterinario:   'Veterinario',
  recepcionista: 'Recepcionista',
  auxiliar:      'Auxiliar',
  cliente:       'Cliente',
}

function abrirCrear() {
  usuarioEditando.value = null
  showModal.value = true
}

function abrirEditar(usuario: UsuarioListItem) {
  usuarioEditando.value = usuario
  showModal.value = true
}

function cerrarModal() {
  showModal.value = false
  setTimeout(() => { usuarioEditando.value = null }, 300)
}

async function handleSubmit(data: UsuarioFormData) {
  loading.value = true
  try {
    if (usuarioEditando.value) {
      await usuariosStore.actualizar(usuarioEditando.value.id, data)
      successMessage.value = 'Usuario actualizado correctamente'
    } else {
      await usuariosStore.crear(data)
      successMessage.value = 'Usuario creado correctamente'
    }
    cerrarModal()
    setTimeout(() => (successMessage.value = ''), 5000)
  } finally {
    loading.value = false
  }
}

function confirmarEliminar(usuario: UsuarioListItem) {
  usuarioAEliminar.value = usuario
  showDeleteConfirm.value = true
}

async function ejecutarEliminar() {
  if (!usuarioAEliminar.value) return
  await usuariosStore.eliminar(usuarioAEliminar.value.id)
  successMessage.value = `Usuario "${usuarioAEliminar.value.username}" eliminado`
  showDeleteConfirm.value = false
  usuarioAEliminar.value = null
  setTimeout(() => (successMessage.value = ''), 3000)
}

function getInitials(u: UsuarioListItem): string {
  return `${u.nombre[0] ?? ''}${u.apellido[0] ?? ''}`.toUpperCase()
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Usuarios" subtitle="Gestión de usuarios del sistema" />
    </template>

    <div class="space-y-4">
      <!-- Alertas -->
      <Transition name="fade">
        <AppAlert v-if="usuariosStore.error" type="error" dismissible @dismiss="usuariosStore.limpiarError()">
          {{ usuariosStore.error }}
        </AppAlert>
      </Transition>
      <Transition name="fade">
        <AppAlert v-if="successMessage" type="success" dismissible @dismiss="successMessage = ''">
          {{ successMessage }}
        </AppAlert>
      </Transition>

      <!-- Toolbar: búsqueda + filtro por rol + botón nuevo -->
      <SearchToolbar
        v-model:search="usuariosStore.searchQuery"
        search-placeholder="Buscar por nombre, usuario o email..."
        :show-new-button="true"
        new-button-label="Nuevo usuario"
        @new="abrirCrear"
      >
        <template #filters>
          <AppSelect
            v-model="usuariosStore.filtroRol"
            :options="rolOptions"
            class="w-full sm:w-52"
          />
        </template>
      </SearchToolbar>

      <!-- Tabla -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ usuariosStore.usuariosFiltrados.length }} usuario(s)
          </h2>
        </div>

        <AppTable
          :columns="columns"
          :rows="usuariosStore.usuariosFiltrados"
          :loading="usuariosStore.loading"
          empty-message="No se encontraron usuarios"
        >
          <!-- Columna Usuario (nombre + apellido + avatar) -->
          <template #cell-usuario="{ row }">
            <div class="flex items-center gap-3">
              <div
                class="w-9 h-9 rounded-full flex items-center justify-center font-semibold text-sm shrink-0 text-white"
                :class="row.activo ? 'bg-primary-500' : 'bg-gray-400'"
              >
                {{ getInitials(row) }}
              </div>
              <div>
                <p class="font-medium" style="color: var(--text-primary)">{{ row.nombre }} {{ row.apellido }}</p>
                <p class="text-xs" style="color: var(--text-muted)">ID: {{ row.id }}</p>
              </div>
            </div>
          </template>

          <!-- Columna Nombre de usuario -->
          <template #cell-username="{ row }">
            <span class="font-mono text-sm" style="color: var(--text-secondary)">{{ row.username }}</span>
          </template>

          <!-- Columna Email -->
          <template #cell-email="{ row }">
            <span class="text-sm" style="color: var(--text-secondary)">{{ row.email }}</span>
          </template>

          <!-- Columna Roles -->
          <template #cell-roles="{ row }">
            <div class="flex flex-wrap gap-1 justify-center">
              <AppBadge
                v-for="rol in row.rolesNombres"
                :key="rol"
                :variant="rolVariant[rol] ?? 'neutral'"
                size="sm"
              >
                {{ rolLabels[rol] ?? rol }}
              </AppBadge>
            </div>
          </template>

          <!-- Columna Estado -->
          <template #cell-estado="{ row }">
            <AppBadge :variant="row.activo ? 'success' : 'neutral'" dot>
              {{ row.activo ? 'Activo' : 'Inactivo' }}
            </AppBadge>
          </template>

          <!-- Acciones -->
          <template #actions="{ row }">
            <div class="flex items-center justify-end gap-1">
              <!-- Editar -->
              <AppButton variant="ghost" size="sm" title="Editar" aria-label="Editar usuario"
                @click.stop="abrirEditar(row)">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </AppButton>
              <!-- Eliminar -->
              <AppButton variant="danger" size="sm" title="Eliminar" aria-label="Eliminar usuario"
                @click.stop="confirmarEliminar(row)">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </AppButton>
            </div>
          </template>
        </AppTable>
      </AppCard>
    </div>

    <!-- Modal crear / editar -->
    <AppModal
      v-model="showModal"
      :title="usuarioEditando ? 'Editar usuario' : 'Nuevo usuario'"
      size="lg"
      @close="cerrarModal"
    >
      <UsuarioForm
        :key="usuarioEditando?.id ?? 'nuevo'"
        :usuario="usuarioEditando"
        :loading="loading"
        @submit="handleSubmit"
        @cancel="cerrarModal"
      />
    </AppModal>

    <!-- Modal confirmación de eliminación -->
    <AppModal
      v-model="showDeleteConfirm"
      title="Eliminar usuario"
      size="sm"
      @close="showDeleteConfirm = false"
    >
      <div class="space-y-4">
        <p style="color: var(--text-secondary)">
          ¿Estás seguro de que quieres eliminar al usuario
          <strong style="color: var(--text-primary)">{{ usuarioAEliminar?.username }}</strong>?
          Esta acción no se puede deshacer.
        </p>
        <div class="flex justify-end gap-3">
          <AppButton variant="ghost" @click="showDeleteConfirm = false">Cancelar</AppButton>
          <AppButton variant="danger" :loading="usuariosStore.loading" @click="ejecutarEliminar">
            Eliminar
          </AppButton>
        </div>
      </div>
    </AppModal>
  </DashboardLayout>
</template>
