<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import LoadingState from '@/components/common/LoadingState.vue'
import FormActions from '@/components/forms/FormActions.vue'
import { api } from '@/services/api'

interface Vacuna {
  id: number
  nombre: string
  descripcion?: string
  especieAplicable?: string
  intervaloDias?: number
  activo: boolean
}

const loading = ref(false)
const error = ref<string | null>(null)
const successMsg = ref('')
const vacunas = ref<Vacuna[]>([])
const searchQuery = ref('')

const showModal = ref(false)
const editando = ref<Vacuna | null>(null)
const guardando = ref(false)
const formErrors = ref<Record<string, string>>({})

const form = ref({
  nombre: '',
  descripcion: '',
  especieAplicable: '',
  intervaloDias: null as number | null,
})

const especieOptions = [
  { value: '', label: 'Todas' },
  { value: 'perro', label: 'Perro' },
  { value: 'gato', label: 'Gato' },
  { value: 'otro', label: 'Otro' },
]

async function cargarVacunas() {
  loading.value = true
  error.value = null
  try {
    const data = await api.get<Vacuna[]>('/api/vacunas')
    vacunas.value = data
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar vacunas'
  } finally {
    loading.value = false
  }
}

onMounted(cargarVacunas)

function resetForm() {
  form.value = { nombre: '', descripcion: '', especieAplicable: '', intervaloDias: null }
  formErrors.value = {}
  editando.value = null
}

function abrirCrear() {
  resetForm()
  showModal.value = true
}

function abrirEditar(vacuna: Vacuna) {
  resetForm()
  editando.value = vacuna
  form.value = {
    nombre: vacuna.nombre,
    descripcion: vacuna.descripcion || '',
    especieAplicable: vacuna.especieAplicable || '',
    intervaloDias: vacuna.intervaloDias || null,
  }
  showModal.value = true
}

async function guardar() {
  formErrors.value = {}
  if (!form.value.nombre.trim()) { formErrors.value.nombre = 'Requerido'; return }

  guardando.value = true
  try {
    const payload = {
      nombre: form.value.nombre,
      descripcion: form.value.descripcion || null,
      especieAplicable: form.value.especieAplicable || null,
      intervaloDias: form.value.intervaloDias || null,
    }

    if (editando.value) {
      await api.put(`/api/vacunas/${editando.value.id}`, payload)
      successMsg.value = 'Vacuna actualizada'
    } else {
      await api.post('/api/vacunas', payload)
      successMsg.value = 'Vacuna creada'
    }
    showModal.value = false
    setTimeout(() => (successMsg.value = ''), 3000)
    await cargarVacunas()
  } catch (e: unknown) {
    const err = e as { campos?: Record<string, string>; message?: string }
    if (err.campos) formErrors.value = err.campos
    else error.value = err.message ?? 'Error al guardar'
  } finally {
    guardando.value = false
  }
}

async function toggleEstado(vacuna: Vacuna) {
  if (!confirm(`¿${vacuna.activo ? 'Desactivar' : 'Activar'} la vacuna "${vacuna.nombre}"?`)) return
  try {
    await api.delete(`/api/vacunas/${vacuna.id}`) // DELETE hace borrado lógico
    successMsg.value = `Vacuna ${vacuna.activo ? 'desactivada' : 'activada'}`
    setTimeout(() => (successMsg.value = ''), 3000)
    await cargarVacunas()
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cambiar estado'
  }
}

const vacunasFiltradas = computed(() => {
  if (!searchQuery.value) return vacunas.value
  const q = searchQuery.value.toLowerCase()
  return vacunas.value.filter(v =>
    v.nombre.toLowerCase().includes(q) ||
    (v.descripcion && v.descripcion.toLowerCase().includes(q))
  )
})
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Catálogo de vacunas" subtitle="Gestión de vacunas disponibles" />
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
        search-placeholder="Buscar por nombre..."
        :show-new-button="true"
        new-button-label="Nueva vacuna"
        @new="abrirCrear"
      />

      <AppCard padding="none">
        <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ vacunasFiltradas.length }} vacuna(s)
          </h2>
        </div>

        <LoadingState v-if="loading">Cargando vacunas...</LoadingState>

        <EmptyState
          v-else-if="vacunasFiltradas.length === 0"
          icon="💉"
          title="Sin vacunas"
          message="No hay vacunas registradas en el catálogo"
        />

        <div v-else class="divide-y" style="border-color: var(--border-color)">
          <div
            v-for="v in vacunasFiltradas"
            :key="v.id"
            class="px-6 py-4 flex flex-col sm:flex-row sm:items-center gap-3"
          >
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 flex-wrap">
                <span class="font-semibold" style="color: var(--text-primary)">{{ v.nombre }}</span>
                <AppBadge :variant="v.activo ? 'success' : 'neutral'" size="sm">
                  {{ v.activo ? 'Activo' : 'Inactivo' }}
                </AppBadge>
              </div>
              <p v-if="v.descripcion" class="text-sm" style="color: var(--text-secondary)">{{ v.descripcion }}</p>
              <div class="flex gap-4 text-xs mt-1" style="color: var(--text-muted)">
                <span v-if="v.especieAplicable">Especie: {{ v.especieAplicable }}</span>
                <span v-if="v.intervaloDias">Intervalo: {{ v.intervaloDias }} días</span>
              </div>
            </div>
            <div class="flex gap-2 shrink-0">
              <AppButton size="sm" variant="ghost" @click="abrirEditar(v)">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </AppButton>
              <AppButton
                size="sm"
                :variant="v.activo ? 'danger' : 'ghost'"
                @click="toggleEstado(v)"
              >
                {{ v.activo ? 'Desactivar' : 'Activar' }}
              </AppButton>
            </div>
          </div>
        </div>
      </AppCard>
    </div>

    <AppModal v-model="showModal" :title="editando ? 'Editar vacuna' : 'Nueva vacuna'" size="md">
      <form @submit.prevent="guardar" class="space-y-4">
        <AppInput
          v-model="form.nombre"
          label="Nombre"
          placeholder="Ej: Rabia"
          required
          :error="formErrors.nombre"
        />
        <AppTextarea
          v-model="form.descripcion"
          label="Descripción"
          placeholder="Descripción de la vacuna..."
          :rows="2"
        />
        <AppSelect
          v-model="form.especieAplicable"
          label="Especie aplicable"
          :options="especieOptions"
        />
        <AppInput
          v-model="form.intervaloDias"
          label="Intervalo de revacunación (días)"
          type="number"
          placeholder="Ej: 365"
        />
        <FormActions
          :loading="guardando"
          submit-label="Guardar"
          @cancel="showModal = false"
        />
      </form>
    </AppModal>
  </DashboardLayout>
</template>