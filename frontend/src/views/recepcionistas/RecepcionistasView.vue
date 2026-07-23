<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppTable from '@/components/ui/AppTable.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppInput from '@/components/ui/AppInput.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import { useRecepcionistasStore } from '@/stores/recepcionistas.store'
import type { Recepcionista, RecepcionistaFormData } from '@/types'

const columns = [
  { key: 'nombre',  label: 'Recepcionista' },
  { key: 'contacto', label: 'Contacto' },
  { key: 'acceso',  label: 'Acceso al sistema' },
  { key: 'estado',  label: 'Estado', align: 'center' },
] as const

const store = useRecepcionistasStore()

onMounted(() => store.cargar({ page: 0 }))
watch(() => store.searchQuery, () => store.cargar({ page: 0 }))

const showModal    = ref(false)
const editando     = ref<Recepcionista | null>(null)
const successMsg   = ref('')
const loading      = ref(false)

// Formulario reactivo
const form = ref<RecepcionistaFormData>({ nombre: '', apellido: '', email: '', telefono: '' })
const errors = ref({ nombre: '', apellido: '', email: '' })

function abrirCrear() {
  editando.value = null
  form.value = { nombre: '', apellido: '', email: '', telefono: '' }
  errors.value = { nombre: '', apellido: '', email: '' }
  showModal.value = true
}

function abrirEditar(r: Recepcionista) {
  editando.value = r
  form.value = { nombre: r.nombre, apellido: r.apellido, email: r.email, telefono: r.telefono ?? '' }
  errors.value = { nombre: '', apellido: '', email: '' }
  showModal.value = true
}

function validate(): boolean {
  errors.value = { nombre: '', apellido: '', email: '' }
  let ok = true
  if (!form.value.nombre.trim())   { errors.value.nombre   = 'El nombre es obligatorio';   ok = false }
  if (!form.value.apellido.trim()) { errors.value.apellido = 'El apellido es obligatorio';  ok = false }
  if (!form.value.email.trim())    { errors.value.email    = 'El email es obligatorio';     ok = false }
  return ok
}

async function handleSubmit() {
  if (!validate()) return
  loading.value = true
  try {
    if (editando.value) {
      await store.actualizar(editando.value.id, form.value)
      successMsg.value = 'Recepcionista actualizada correctamente'
    } else {
      await store.crear(form.value)
      successMsg.value = 'Recepcionista creada y usuario de acceso generado automáticamente'
    }
    showModal.value = false
    setTimeout(() => (successMsg.value = ''), 4000)
  } finally {
    loading.value = false
  }
}

async function handleEliminar(r: Recepcionista) {
  await store.eliminar(r.id)
  successMsg.value = `Recepcionista ${r.nombre} ${r.apellido} desactivada`
  setTimeout(() => (successMsg.value = ''), 3000)
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Recepcionistas" subtitle="Personal de recepción y atención al cliente" />
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="store.error" type="error" dismissible @dismiss="store.limpiarError()">
          {{ store.error }}
        </AppAlert>
      </Transition>
      <Transition name="fade">
        <AppAlert v-if="successMsg" type="success" dismissible @dismiss="successMsg = ''">
          {{ successMsg }}
        </AppAlert>
      </Transition>

      <SearchToolbar
        v-model:search="store.searchQuery"
        search-placeholder="Buscar por nombre o apellido..."
        :show-new-button="true"
        new-button-label="Nueva recepcionista"
        @new="abrirCrear"
      />

      <AppCard padding="none">
        <div class="px-6 py-4 border-b" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ store.totalElements }} recepcionista(s)
          </h2>
        </div>

        <AppTable
          :columns="columns"
          :rows="store.recepcionistas"
          :loading="store.loading"
          empty-message="No se encontraron recepcionistas"
          @row-click="abrirEditar"
        >
          <template #cell-nombre="{ row }">
            <div class="flex items-center gap-3">
              <div class="w-9 h-9 rounded-full flex items-center justify-center font-semibold text-sm shrink-0"
                style="background: rgba(20,184,166,0.15); color: #0d9488;">
                {{ row.nombre[0] }}{{ row.apellido[0] }}
              </div>
              <div>
                <p class="font-medium" style="color: var(--text-primary)">{{ row.nombre }} {{ row.apellido }}</p>
              </div>
            </div>
          </template>

          <template #cell-contacto="{ row }">
            <p style="color: var(--text-secondary)">{{ row.email }}</p>
            <p class="text-xs" style="color: var(--text-muted)">{{ row.telefono }}</p>
          </template>

          <template #cell-acceso="{ row }">
            <span v-if="row.username" class="text-xs font-mono px-2 py-0.5 rounded"
              style="background: var(--bg-hover); color: var(--text-secondary)">
              👤 {{ row.username }}
            </span>
            <span v-else class="text-xs" style="color: var(--text-muted)">Sin acceso</span>
          </template>

          <template #cell-estado="{ row }">
            <AppBadge :variant="row.estado === 'activo' ? 'success' : 'neutral'" dot>
              {{ row.estado === 'activo' ? 'Activa' : 'Inactiva' }}
            </AppBadge>
          </template>

          <template #actions="{ row }">
            <div class="flex items-center justify-end gap-1">
              <AppButton variant="ghost" size="sm" title="Editar" @click.stop="abrirEditar(row)">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </AppButton>
              <AppButton variant="danger" size="sm" title="Desactivar" @click.stop="handleEliminar(row)">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" />
                </svg>
              </AppButton>
            </div>
          </template>
        </AppTable>

        <div class="px-4 border-t" style="border-color: var(--border-color)">
          <AppPagination
            :page="store.page"
            :total-pages="store.totalPages"
            :total-elements="store.totalElements"
            :page-size="store.pageSize"
            :loading="store.loading"
            @change="store.irAPagina"
          />
        </div>
      </AppCard>
    </div>

    <!-- Modal formulario -->
    <AppModal
      v-model="showModal"
      :title="editando ? 'Editar recepcionista' : 'Nueva recepcionista'"
      size="md"
    >
      <form class="space-y-4" @submit.prevent="handleSubmit">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="form.nombre"
            label="Nombre"
            placeholder="Nombre"
            :error="errors.nombre"
            required
          />
          <AppInput
            v-model="form.apellido"
            label="Apellido"
            placeholder="Apellido"
            :error="errors.apellido"
            required
          />
        </div>
        <AppInput
          v-model="form.email"
          label="Email"
          type="email"
          placeholder="correo@ejemplo.com"
          :error="errors.email"
          required
        />
        <AppInput
          v-model="form.telefono"
          label="Teléfono"
          placeholder="Teléfono (opcional)"
        />

        <div v-if="!editando" class="p-3 rounded-lg text-sm"
          style="background: rgba(16,185,129,0.08); color: var(--text-secondary); border: 1px solid rgba(16,185,129,0.2)">
          ✅ Se creará automáticamente un usuario de acceso.<br>
          <strong>Usuario:</strong> email · <strong>Contraseña inicial:</strong> email
        </div>

        <div class="flex justify-end gap-3 pt-2">
          <AppButton type="button" variant="ghost" @click="showModal = false">Cancelar</AppButton>
          <AppButton type="submit" :loading="loading">
            {{ editando ? 'Guardar cambios' : 'Crear recepcionista' }}
          </AppButton>
        </div>
      </form>
    </AppModal>
  </DashboardLayout>
</template>
