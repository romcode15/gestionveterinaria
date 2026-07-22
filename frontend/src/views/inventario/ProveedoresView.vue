<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { api } from '@/services/api'

interface Proveedor {
  id: number
  nombre: string
  ruc: string
  contacto: string
  telefono: string
  email: string
  direccion: string
  activo: boolean
}

const loading = ref(false)
const error = ref<string | null>(null)
const successMsg = ref('')
const proveedores = ref<Proveedor[]>([])
const searchQuery = ref('')
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(20)

const showModal = ref(false)
const editando = ref<Proveedor | null>(null)
const guardando = ref(false)
const formErrors = ref<Record<string, string>>({})

const form = ref({
  nombre: '',
  ruc: '',
  contacto: '',
  telefono: '',
  email: '',
  direccion: '',
})

async function cargarProveedores(p = 0) {
  loading.value = true
  error.value = null
  try {
    const params: any = { page: p, size: pageSize.value, sort: 'nombre', dir: 'asc' }
    if (searchQuery.value) params.search = searchQuery.value

    const res = await api.getPaged<Proveedor>('/api/inventario/proveedores', params)
    proveedores.value = res.content
    page.value = res.number
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar proveedores'
  } finally {
    loading.value = false
  }
}

onMounted(() => cargarProveedores())

watch(searchQuery, () => cargarProveedores(0))

function resetForm() {
  form.value = { nombre: '', ruc: '', contacto: '', telefono: '', email: '', direccion: '' }
  formErrors.value = {}
  editando.value = null
}

function abrirCrear() {
  resetForm()
  showModal.value = true
}

function abrirEditar(proveedor: Proveedor) {
  resetForm()
  editando.value = proveedor
  form.value = {
    nombre: proveedor.nombre,
    ruc: proveedor.ruc || '',
    contacto: proveedor.contacto || '',
    telefono: proveedor.telefono || '',
    email: proveedor.email || '',
    direccion: proveedor.direccion || '',
  }
  showModal.value = true
}

async function guardar() {
  formErrors.value = {}

  if (!form.value.nombre.trim()) { formErrors.value.nombre = 'Requerido'; return }
  if (!form.value.ruc.trim()) { formErrors.value.ruc = 'Requerido'; return }

  guardando.value = true
  try {
    const payload = {
      nombre: form.value.nombre,
      ruc: form.value.ruc,
      contacto: form.value.contacto || null,
      telefono: form.value.telefono || null,
      email: form.value.email || null,
      direccion: form.value.direccion || null,
    }

    if (editando.value) {
      await api.put(`/api/inventario/proveedores/${editando.value.id}`, payload)
      successMsg.value = 'Proveedor actualizado'
    } else {
      await api.post('/api/inventario/proveedores', payload)
      successMsg.value = 'Proveedor creado'
    }
    showModal.value = false
    setTimeout(() => (successMsg.value = ''), 3000)
    await cargarProveedores(page.value)
  } catch (e: unknown) {
    const err = e as { campos?: Record<string, string>; message?: string }
    if (err.campos) formErrors.value = err.campos
    else error.value = err.message ?? 'Error al guardar'
  } finally {
    guardando.value = false
  }
}

async function toggleEstado(proveedor: Proveedor) {
  if (!confirm(`¿${proveedor.activo ? 'Desactivar' : 'Activar'} el proveedor "${proveedor.nombre}"?`)) return
  try {
    await api.delete(`/api/inventario/proveedores/${proveedor.id}`)
    successMsg.value = `Proveedor ${proveedor.activo ? 'desactivado' : 'activado'}`
    setTimeout(() => (successMsg.value = ''), 3000)
    await cargarProveedores(page.value)
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cambiar estado'
  }
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Proveedores" subtitle="Gestión de proveedores" />
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">{{ error }}</AppAlert>
      </Transition>
      <Transition name="fade">
        <AppAlert v-if="successMsg" type="success" dismissible @dismiss="successMsg = ''">{{ successMsg }}</AppAlert>
      </Transition>

      <SearchToolbar
        v-model:search="searchQuery"
        search-placeholder="Buscar por nombre, RUC, contacto..."
        :show-new-button="true"
        new-button-label="Nuevo proveedor"
        @new="abrirCrear"
      />

      <AppCard padding="none">
        <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ totalElements }} proveedor(es)
          </h2>
        </div>

        <div v-if="loading" class="p-4 space-y-3">
          <div v-for="i in 4" :key="i" class="h-16 vg-skeleton rounded-xl animate-pulse" />
        </div>

        <EmptyState
          v-else-if="proveedores.length === 0"
          icon="🏢"
          title="Sin proveedores"
          message="No hay proveedores registrados"
        />

        <div v-else class="divide-y" style="border-color: var(--border-color)">
          <div
            v-for="p in proveedores"
            :key="p.id"
            class="px-6 py-4 flex flex-col sm:flex-row sm:items-center gap-3"
          >
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 flex-wrap">
                <span class="font-semibold" style="color: var(--text-primary)">{{ p.nombre }}</span>
                <AppBadge :variant="p.activo ? 'success' : 'neutral'" size="sm">
                  {{ p.activo ? 'Activo' : 'Inactivo' }}
                </AppBadge>
              </div>
              <div class="text-sm" style="color: var(--text-secondary)">
                <span>RUC: {{ p.ruc }}</span>
                <span v-if="p.contacto" class="ml-3">Contacto: {{ p.contacto }}</span>
              </div>
              <div class="text-xs" style="color: var(--text-muted)">
                <span v-if="p.telefono">{{ p.telefono }}</span>
                <span v-if="p.email" class="ml-3">{{ p.email }}</span>
                <span v-if="p.direccion" class="ml-3">{{ p.direccion }}</span>
              </div>
            </div>
            <div class="flex gap-2 shrink-0">
              <AppButton size="sm" variant="ghost" @click="abrirEditar(p)">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </AppButton>
              <AppButton size="sm" variant="ghost" :class="p.activo ? 'vg-icon-btn-danger' : 'vg-icon-btn'" @click="toggleEstado(p)">
                {{ p.activo ? 'Desactivar' : 'Activar' }}
              </AppButton>
            </div>
          </div>
        </div>

        <div class="px-4 border-t" style="border-color: var(--border-color)">
          <AppPagination
            :page="page"
            :total-pages="totalPages"
            :total-elements="totalElements"
            :page-size="pageSize"
            :loading="loading"
            @change="cargarProveedores"
          />
        </div>
      </AppCard>
    </div>

    <AppModal v-model="showModal" :title="editando ? 'Editar proveedor' : 'Nuevo proveedor'" size="md">
      <form @submit.prevent="guardar" class="space-y-4">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="form.nombre"
            label="Nombre"
            placeholder="Razón social"
            required
            :error="formErrors.nombre"
          />
          <AppInput
            v-model="form.ruc"
            label="RUC"
            placeholder="1234567890"
            required
            :error="formErrors.ruc"
          />
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="form.contacto"
            label="Contacto"
            placeholder="Nombre del contacto"
          />
          <AppInput
            v-model="form.telefono"
            label="Teléfono"
            placeholder="3001234567"
          />
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="form.email"
            label="Email"
            type="email"
            placeholder="correo@proveedor.com"
          />
          <AppInput
            v-model="form.direccion"
            label="Dirección"
            placeholder="Dirección completa"
          />
        </div>
        <div class="flex gap-3 justify-end pt-2">
          <AppButton type="button" variant="ghost" @click="showModal = false">Cancelar</AppButton>
          <AppButton type="submit" :loading="guardando">Guardar</AppButton>
        </div>
      </form>
    </AppModal>
  </DashboardLayout>
</template>