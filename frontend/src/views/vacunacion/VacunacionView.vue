<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import LoadingState from '@/components/common/LoadingState.vue'
import FormActions from '@/components/forms/FormActions.vue'
import { api } from '@/services/api'
import { useAuthStore } from '@/stores/auth.store'

const authStore = useAuthStore()
const esMedico  = authStore.isMedico

// ── Tipos locales ──────────────────────────────────────────────────────────

interface VacunaRegistro {
  id: number
  mascotaId: number
  mascotaNombre: string
  vacunaId: number
  vacunaNombre: string
  medicoId: number
  medicoNombre: string
  fechaAplicacion: string
  fechaProximaDosis?: string
  lote?: string
  estado: 'vigente' | 'vencida'
  createdAt: string
}

interface VacunaCatalogo {
  id: number
  nombre: string
  descripcion?: string
  especieAplicable?: string
  intervaloDias?: number
  activo: boolean
}

interface MascotaSimple {
  id: number
  nombre: string
}

// ── Estado ──────────────────────────────────────────────────────────────────

const registros = ref<VacunaRegistro[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const successMsg = ref('')
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(20)

// Filtros
const filtroEstado = ref<string>('todos')
const searchQuery = ref('')

// Alertas
const alertas = ref<{ proximas: number; vencidas: number }>({ proximas: 0, vencidas: 0 })

// Modal registro
const showModal = ref(false)
const guardando = ref(false)
const formErrors = ref<Record<string, string>>({})
const catalogoVacunas = ref<VacunaCatalogo[]>([])
const medicos = ref<{ id: number; nombre: string; apellido: string }[]>([])
const mascotas = ref<MascotaSimple[]>([])

const form = ref({
  mascotaId: null as number | null,
  vacunaId: null as number | null,
  // Si el usuario es veterinario, preseleccionar su medicoId
  medicoId: esMedico ? (authStore.medicoId ?? null) : null as number | null,
  fechaAplicacion: new Date().toISOString().split('T')[0],
  lote: '',
  fechaProximaDosis: '',
})

// ── Computed ─────────────────────────────────────────────────────────────────

const estadoOptions = [
  { value: 'todos', label: 'Todos los estados' },
  { value: 'vigente', label: 'Vigentes' },
  { value: 'vencida', label: 'Vencidas' },
]

const vacunaOptions = computed(() =>
  catalogoVacunas.value
    .filter(v => v.activo)
    .map(v => ({ value: v.id, label: v.nombre }))
)

const medicoOptions = computed(() =>
  medicos.value.map(m => ({
    value: m.id,
    label: `${m.nombre} ${m.apellido}`,
  }))
)

const mascotaOptions = computed(() =>
  mascotas.value.map(m => ({
    value: m.id,
    label: m.nombre,
  }))
)

// ── Carga de datos ─────────────────────────────────────────────────────────

async function cargarDatos() {
  await Promise.all([
    cargarRegistros(),
    cargarCatalogos(),
    cargarAlertas(),
  ])
}

async function cargarRegistros(p = 0) {
  loading.value = true
  error.value = null
  try {
    const params: Record<string, unknown> = { page: p, size: pageSize.value, sort: 'fechaAplicacion', dir: 'desc' }
    if (filtroEstado.value !== 'todos') params.estado = filtroEstado.value
    if (searchQuery.value) params.search = searchQuery.value
    // Si es veterinario, filtrar solo sus registros
    if (esMedico && authStore.medicoId) params.medicoId = authStore.medicoId

    const res = await api.getPaged<VacunaRegistro>('/api/mascota-vacunas', params)
    registros.value = res.content
    page.value = res.number
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar registros'
  } finally {
    loading.value = false
  }
}

async function cargarCatalogos() {
  try {
    const [vacunasRes, medicosRes, mascotasRes] = await Promise.all([
      api.get<VacunaCatalogo[]>('/api/vacunas'),
      api.get<{ id: number; nombre: string; apellido: string }[]>('/api/medicos'),
      api.get<MascotaSimple[]>('/api/mascotas/simple'),
    ])
    catalogoVacunas.value = vacunasRes
    medicos.value = medicosRes
    mascotas.value = mascotasRes
  } catch (e) {
    console.error('Error cargando catálogos:', e)
  }
}

async function cargarAlertas() {
  try {
    const data = await api.get<{ proximas: number; vencidas: number }>('/api/mascota-vacunas/alertas?dias=30')
    alertas.value = data
  } catch (e) {
    console.error('Error cargando alertas:', e)
  }
}

onMounted(cargarDatos)

watch([searchQuery, filtroEstado], () => {
  cargarRegistros(0)
})

// ── Guardar registro ───────────────────────────────────────────────────────

function resetForm() {
  form.value = {
    mascotaId: null,
    vacunaId: null,
    // Mantener medicoId preseleccionado para veterinario
    medicoId: esMedico ? (authStore.medicoId ?? null) : null,
    fechaAplicacion: new Date().toISOString().split('T')[0],
    lote: '',
    fechaProximaDosis: '',
  }
  formErrors.value = {}
}

async function guardarRegistro() {
  formErrors.value = {}

  if (!form.value.mascotaId) { formErrors.value.mascotaId = 'Requerido'; return }
  if (!form.value.vacunaId) { formErrors.value.vacunaId = 'Requerido'; return }
  if (!form.value.medicoId) { formErrors.value.medicoId = 'Requerido'; return }
  if (!form.value.fechaAplicacion) { formErrors.value.fechaAplicacion = 'Requerido'; return }

  guardando.value = true
  try {
    await api.post('/api/mascota-vacunas', {
      mascotaId: form.value.mascotaId,
      vacunaId: form.value.vacunaId,
      medicoId: form.value.medicoId,
      fechaAplicacion: form.value.fechaAplicacion,
      lote: form.value.lote || null,
      fechaProximaDosis: form.value.fechaProximaDosis || null,
    })
    showModal.value = false
    successMsg.value = 'Vacuna registrada correctamente'
    setTimeout(() => (successMsg.value = ''), 3000)
    resetForm()
    await cargarDatos()
  } catch (e: unknown) {
    const err = e as { campos?: Record<string, string>; message?: string }
    if (err.campos) formErrors.value = err.campos
    else error.value = err.message ?? 'Error al guardar'
  } finally {
    guardando.value = false
  }
}

// ── Helpers ─────────────────────────────────────────────────────────────────

function calcularProximaDosis() {
  const vacuna = catalogoVacunas.value.find(v => v.id === Number(form.value.vacunaId))
  if (vacuna?.intervaloDias && form.value.fechaAplicacion) {
    const fecha = new Date(form.value.fechaAplicacion)
    fecha.setDate(fecha.getDate() + vacuna.intervaloDias)
    form.value.fechaProximaDosis = fecha.toISOString().split('T')[0]
  } else {
    form.value.fechaProximaDosis = ''
  }
}

function formatFecha(f: string) {
  return new Date(f + 'T00:00:00').toLocaleDateString('es-CO', {
    day: 'numeric', month: 'short', year: 'numeric'
  })
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Vacunación" subtitle="Registro de vacunas aplicadas" />
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">{{ error }}</AppAlert>
      </Transition>
      <Transition name="fade">
        <AppAlert v-if="successMsg" type="success" dismissible @dismiss="successMsg = ''">{{ successMsg }}</AppAlert>
      </Transition>

      <!-- Alertas -->
      <div v-if="alertas.proximas > 0 || alertas.vencidas > 0" class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <AppCard v-if="alertas.proximas > 0" padding="sm" class="border-l-4" style="border-color: var(--color-warning)">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium" style="color: var(--text-primary)">
                ⚠️ {{ alertas.proximas }} vacuna(s) próxima(s) a vencer (30 días)
              </p>
            </div>
            <AppButton size="sm" variant="ghost" @click="filtroEstado = 'vigente'; searchQuery = ''">
              Ver listado
            </AppButton>
          </div>
        </AppCard>
        <AppCard v-if="alertas.vencidas > 0" padding="sm" class="border-l-4" style="border-color: var(--color-danger)">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm font-medium" style="color: var(--text-primary)">
                ❌ {{ alertas.vencidas }} vacuna(s) vencidas
              </p>
            </div>
            <AppButton size="sm" variant="ghost" @click="filtroEstado = 'vencida'; searchQuery = ''">
              Ver listado
            </AppButton>
          </div>
        </AppCard>
      </div>

      <!-- Toolbar -->
      <SearchToolbar
        v-model:search="searchQuery"
        search-placeholder="Buscar por mascota, vacuna, médico..."
        :show-new-button="true"
        new-button-label="Registrar vacuna"
        @new="() => { resetForm(); showModal = true }"
      >
        <template #filters>
          <AppSelect
            v-model="filtroEstado"
            :options="estadoOptions"
            class="w-full sm:w-48"
          />
        </template>
      </SearchToolbar>

      <!-- Lista de registros -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ totalElements }} registro(s)
          </h2>
        </div>

        <LoadingState v-if="loading">Cargando registros...</LoadingState>

        <EmptyState
          v-else-if="registros.length === 0"
          icon="💉"
          title="Sin registros"
          message="No se encontraron registros de vacunación"
        />

        <div v-else class="divide-y" style="border-color: var(--border-color)">
          <div
            v-for="reg in registros"
            :key="reg.id"
            class="px-6 py-4 flex flex-col sm:flex-row sm:items-center gap-3"
          >
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 flex-wrap">
                <span class="font-semibold" style="color: var(--text-primary)">{{ reg.mascotaNombre }}</span>
                <AppBadge
                  :variant="reg.estado === 'vigente' ? 'success' : 'danger'"
                  size="sm"
                >
                  {{ reg.estado === 'vigente' ? 'Vigente' : 'Vencida' }}
                </AppBadge>
              </div>
              <p class="text-sm" style="color: var(--text-secondary)">{{ reg.vacunaNombre }}</p>
              <p class="text-xs" style="color: var(--text-muted)">
                Dr. {{ reg.medicoNombre }} · {{ formatFecha(reg.fechaAplicacion) }}
                <span v-if="reg.fechaProximaDosis">
                  · Próxima: {{ formatFecha(reg.fechaProximaDosis) }}
                </span>
                <span v-if="reg.lote"> · Lote: {{ reg.lote }}</span>
              </p>
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
            @change="cargarRegistros"
          />
        </div>
      </AppCard>
    </div>

    <!-- Modal registrar vacuna -->
    <AppModal v-model="showModal" title="Registrar vacuna" size="lg">
      <form @submit.prevent="guardarRegistro" class="space-y-4">
        <AppSelect
          v-model="form.mascotaId"
          label="Mascota"
          :options="mascotaOptions"
          placeholder="Seleccionar mascota..."
          required
          :error="formErrors.mascotaId"
        />
        <AppSelect
          v-model="form.vacunaId"
          label="Vacuna"
          :options="vacunaOptions"
          placeholder="Seleccionar vacuna..."
          required
          :error="formErrors.vacunaId"
          @update:model-value="calcularProximaDosis"
        />
        <!-- Médico: solo visible para admin/recepcionista; para veterinario se preselecciona automáticamente -->
        <AppSelect
          v-if="!esMedico"
          v-model="form.medicoId"
          label="Médico"
          :options="medicoOptions"
          placeholder="Seleccionar médico..."
          required
          :error="formErrors.medicoId"
        />
        <div v-else class="p-3 rounded-lg text-sm" style="background: var(--bg-hover); border: 1px solid var(--border-color)">
          <p class="text-xs font-semibold mb-1" style="color: var(--text-muted)">MÉDICO APLICADOR</p>
          <p style="color: var(--text-primary)">
            {{ medicos.find(m => m.id === form.medicoId)?.nombre }}
            {{ medicos.find(m => m.id === form.medicoId)?.apellido }}
          </p>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="form.fechaAplicacion"
            label="Fecha de aplicación"
            type="date"
            required
            :error="formErrors.fechaAplicacion"
          />
          <AppInput
            v-model="form.fechaProximaDosis"
            label="Próxima dosis (auto calculado)"
            type="date"
            readonly
            class="opacity-60"
          />
        </div>
        <AppInput
          v-model="form.lote"
          label="Lote (opcional)"
          placeholder="Número de lote"
        />
        <FormActions
          :loading="guardando"
          submit-label="Registrar"
          @cancel="showModal = false"
        />
      </form>
    </AppModal>
  </DashboardLayout>
</template>