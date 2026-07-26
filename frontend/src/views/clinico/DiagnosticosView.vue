<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppPagination from '@/components/ui/AppPagination.vue'

import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import LoadingState from '@/components/common/LoadingState.vue'
import FormActions from '@/components/forms/FormActions.vue'

import { api } from '@/services/api'
import type { SpringPage } from '@/services/api'
import { useAuthStore } from '@/stores/auth.store'

const route  = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// ── Tipos locales ──────────────────────────────────────────────────────────

interface DiagnosticoItem {
  id: number
  citaId: number
  citaFecha: string
  mascotaId: number
  mascotaNombre: string
  medicoId: number
  medicoNombre: string
  sintomas: string
  diagnostico: string
  pronostico: string | null
  pesoConsulta: number | null
  temperatura: number | null
  observaciones: string | null
  createdAt: string
}

interface DiagnosticoForm {
  citaId: number | null
  sintomas: string
  diagnostico: string
  pronostico: string
  pesoConsulta: string
  temperatura: string
  observaciones: string
}

// ── Estado ─────────────────────────────────────────────────────────────────

const diagnosticos   = ref<DiagnosticoItem[]>([])
const loading        = ref(false)
const error          = ref<string | null>(null)
const successMsg     = ref('')
const page           = ref(0)
const totalPages     = ref(0)
const totalElements  = ref(0)
const pageSize       = ref(20)
const expandido      = ref<number | null>(null)

// Filtro por mascota (viene del query param ?mascotaId=)
const filtroMascotaId = ref<number | null>(
  route.query.mascotaId ? Number(route.query.mascotaId) : null
)
// Filtro por médico (se activa automáticamente cuando el usuario es veterinario)
const filtroMedicoId = ref<number | null>(null)

// Modal de nuevo diagnóstico
const showModal      = ref(false)
const citaIdQuery    = ref<number | null>(
  route.query.citaId ? Number(route.query.citaId) : null
)
const guardando      = ref(false)
const formErrors     = ref<Record<string, string>>({})

const form = ref<DiagnosticoForm>({
  citaId:       citaIdQuery.value,
  sintomas:     '',
  diagnostico:  '',
  pronostico:   '',
  pesoConsulta: '',
  temperatura:  '',
  observaciones: '',
})

const pronosticoOptions = [
  { value: '',           label: 'Sin especificar' },
  { value: 'favorable',  label: 'Favorable' },
  { value: 'reservado',  label: 'Reservado' },
  { value: 'grave',      label: 'Grave' },
  { value: 'muerte',     label: 'Muerte' },
]

// Si viene citaId en la URL, abre el modal automáticamente
onMounted(async () => {
  // Si es veterinario y no viene filtro de mascota, carga sus diagnósticos automáticamente
  if (authStore.isMedico && !filtroMascotaId.value && authStore.medicoId) {
    filtroMedicoId.value = authStore.medicoId
  }
  await cargar()
  if (citaIdQuery.value) {
    showModal.value = true
  }
})

watch(() => route.query.citaId, (val) => {
  if (val) {
    citaIdQuery.value = Number(val)
    form.value.citaId = Number(val)
    showModal.value = true
  }
})

// ── Carga paginada ─────────────────────────────────────────────────────────

async function cargar(p = 0) {
  loading.value = true
  error.value   = null
  try {
    let endpoint: string

    if (filtroMascotaId.value) {
      // Historial de una mascota concreta
      endpoint = `/api/diagnosticos/mascota/${filtroMascotaId.value}`
    } else if (filtroMedicoId.value) {
      // Diagnósticos del veterinario logueado
      endpoint = `/api/diagnosticos/medico/${filtroMedicoId.value}`
    } else {
      // Sin filtro — estado vacío (solo admin debería llegar aquí con filtro vacío)
      diagnosticos.value  = []
      totalElements.value = 0
      totalPages.value    = 0
      loading.value = false
      return
    }

    const res = await api.getPaged<DiagnosticoItem>(endpoint, {
      page: p, size: pageSize.value, sort: 'cita.fecha', dir: 'desc'
    })
    diagnosticos.value  = res.content
    page.value          = res.number
    totalPages.value    = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar diagnósticos'
  } finally {
    loading.value = false
  }
}

// ── Guardar diagnóstico ────────────────────────────────────────────────────

function resetForm() {
  form.value = {
    citaId: citaIdQuery.value,
    sintomas: '', diagnostico: '', pronostico: '',
    pesoConsulta: '', temperatura: '', observaciones: '',
  }
  formErrors.value = {}
}

async function guardar() {
  formErrors.value = {}

  if (!form.value.citaId) {
    formErrors.value.citaId = 'La cita es obligatoria'
    return
  }
  if (!form.value.sintomas.trim()) {
    formErrors.value.sintomas = 'Los síntomas son obligatorios'
    return
  }
  if (!form.value.diagnostico.trim()) {
    formErrors.value.diagnostico = 'El diagnóstico es obligatorio'
    return
  }

  guardando.value = true
  try {
    await api.post('/api/diagnosticos', {
      citaId:       form.value.citaId,
      sintomas:     form.value.sintomas,
      diagnostico:  form.value.diagnostico,
      pronostico:   form.value.pronostico || null,
      pesoConsulta: form.value.pesoConsulta ? Number(form.value.pesoConsulta) : null,
      temperatura:  form.value.temperatura  ? Number(form.value.temperatura)  : null,
      observaciones: form.value.observaciones || null,
    })
    showModal.value = false
    successMsg.value = 'Diagnóstico registrado correctamente'
    setTimeout(() => (successMsg.value = ''), 3000)
    resetForm()
    if (filtroMascotaId.value) await cargar()
    // Limpiar query param
    if (route.query.citaId) router.replace({ query: {} })
  } catch (e: unknown) {
    const err = e as { campos?: Record<string, string>; message?: string }
    if (err.campos) {
      formErrors.value = err.campos
    } else {
      error.value = err.message ?? 'Error al guardar diagnóstico'
    }
  } finally {
    guardando.value = false
  }
}

// ── Helpers ────────────────────────────────────────────────────────────────

function colorPronostico(p: string | null) {
  const m: Record<string, string> = {
    favorable: 'success', reservado: 'warning',
    grave: 'danger', muerte: 'neutral',
  }
  return (p && m[p]) ? m[p] as 'success' | 'warning' | 'danger' | 'neutral' : 'neutral'
}

function formatFecha(f: string) {
  return new Date(f + 'T12:00:00').toLocaleDateString('es-ES', {
    day: '2-digit', month: 'short', year: 'numeric',
  })
}

function limpiarFiltro() {
  filtroMascotaId.value = null
  // Si es veterinario, vuelve a mostrar sus diagnósticos; si no, vacío
  if (!authStore.isMedico) {
    diagnosticos.value = []
    totalElements.value = 0
    totalPages.value = 0
  } else {
    cargar()
  }
  if (route.query.mascotaId) router.replace({ query: {} })
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Diagnósticos" subtitle="Módulo clínico — registros de consultas" />
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">{{ error }}</AppAlert>
      </Transition>
      <Transition name="fade">
        <AppAlert v-if="successMsg" type="success" dismissible @dismiss="successMsg = ''">{{ successMsg }}</AppAlert>
      </Transition>

      <!-- Toolbar con SearchToolbar -->
      <SearchToolbar
        :show-new-button="true"
        new-button-label="Nuevo diagnóstico"
        @new="() => { resetForm(); showModal = true }"
      >
        <template #filters>
          <div class="flex items-center gap-2">
            <svg class="w-5 h-5" style="color: var(--text-muted)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
                d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            <span class="text-sm" style="color: var(--text-muted)">
              <span v-if="filtroMascotaId">Mostrando diagnósticos de la mascota #{{ filtroMascotaId }}</span>
              <span v-else>Busca por mascota para ver su historial, o registra un nuevo diagnóstico desde una cita</span>
            </span>
            <AppButton
              v-if="filtroMascotaId"
              variant="secondary"
              size="sm"
              @click="limpiarFiltro"
            >
              Limpiar filtro
            </AppButton>
          </div>
        </template>
      </SearchToolbar>

      <!-- Estado vacío sin filtro (solo admin/recepcionista sin mascota seleccionada) -->
      <EmptyState
        v-if="!filtroMascotaId && !filtroMedicoId && !loading"
        title="Accede desde el historial de una mascota"
        message="O registra un nuevo diagnóstico indicando el ID de la cita completada"
      >
        <template #actions>
          <AppButton @click="router.push('/mascotas')">Ir a Mascotas</AppButton>
        </template>
      </EmptyState>

      <!-- Lista de diagnósticos -->
      <AppCard v-else padding="none">
        <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ totalElements }} diagnóstico(s)
          </h2>
        </div>

        <div v-if="loading" class="p-4">
          <LoadingState>Cargando diagnósticos...</LoadingState>
        </div>

        <EmptyState
          v-else-if="diagnosticos.length === 0"
          title="Sin diagnósticos"
          message="No hay diagnósticos registrados para esta mascota"
        />

        <div v-else class="divide-y" style="border-color: var(--border-color)">
          <div v-for="d in diagnosticos" :key="d.id">
            <!-- Fila -->
            <button
              class="w-full px-6 py-4 flex flex-col sm:flex-row sm:items-center gap-3 text-left hover:bg-(--bg-surface-2) transition-colors"
              @click="expandido = expandido === d.id ? null : d.id"
            >
              <div class="shrink-0 w-24">
                <p class="text-sm font-semibold" style="color: var(--text-primary)">{{ formatFecha(d.citaFecha) }}</p>
                <p class="text-xs" style="color: var(--text-muted)">{{ d.medicoNombre }}</p>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2 flex-wrap">
                  <span class="font-medium" style="color: var(--text-primary)">{{ d.mascotaNombre }}</span>
                  <AppBadge v-if="d.pronostico" :variant="colorPronostico(d.pronostico)">
                    {{ d.pronostico }}
                  </AppBadge>
                </div>
                <p class="text-sm truncate mt-0.5" style="color: var(--text-muted)">{{ d.diagnostico }}</p>
              </div>
              <div class="flex items-center gap-3 shrink-0 text-sm" style="color: var(--text-muted)">
                <span v-if="d.pesoConsulta">{{ d.pesoConsulta }} kg</span>
                <span v-if="d.temperatura">{{ d.temperatura }} °C</span>
                <svg class="w-4 h-4 transition-transform" :class="expandido === d.id ? 'rotate-180' : ''"
                  style="color: var(--text-disabled)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                </svg>
              </div>
            </button>

            <!-- Detalle expandido -->
            <div
              v-if="expandido === d.id"
              class="px-6 pb-5 border-t"
              style="border-color: var(--border-color); background-color: var(--bg-surface-2)"
            >
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-4">
                <div>
                  <p class="text-xs font-semibold uppercase tracking-wide mb-1" style="color: var(--text-muted)">Síntomas</p>
                  <p class="text-sm" style="color: var(--text-secondary)">{{ d.sintomas }}</p>
                </div>
                <div>
                  <p class="text-xs font-semibold uppercase tracking-wide mb-1" style="color: var(--text-muted)">Diagnóstico</p>
                  <p class="text-sm" style="color: var(--text-secondary)">{{ d.diagnostico }}</p>
                </div>
                <div v-if="d.observaciones" class="sm:col-span-2">
                  <p class="text-xs font-semibold uppercase tracking-wide mb-1" style="color: var(--text-muted)">Observaciones</p>
                  <p class="text-sm" style="color: var(--text-secondary)">{{ d.observaciones }}</p>
                </div>
              </div>
              <div class="flex gap-2 mt-4">
                <AppButton size="sm" @click="router.push(`/diagnosticos/${d.id}/tratamiento`)">
                  Ver tratamiento
                </AppButton>
              </div>
            </div>
          </div>
        </div>

        <div class="px-4 border-t" style="border-color: var(--border-color)">
          <AppPagination
            :page="page" :total-pages="totalPages"
            :total-elements="totalElements" :page-size="pageSize"
            :loading="loading" @change="cargar"
          />
        </div>
      </AppCard>
    </div>

    <!-- Modal nuevo diagnóstico -->
    <AppModal v-model="showModal" title="Registrar diagnóstico" size="lg">
      <form class="space-y-4" @submit.prevent="guardar">
        <AppInput
          v-model="form.citaId"
          id="citaId"
          label="ID de cita"
          type="number"
          placeholder="Ej: 42"
          :error="formErrors.citaId"
          required
        />

        <AppTextarea
          v-model="form.sintomas"
          id="sintomas"
          label="Síntomas"
          placeholder="Describe los síntomas observados..."
          :rows="3"
          :error="formErrors.sintomas"
          required
        />

        <AppTextarea
          v-model="form.diagnostico"
          id="diagnostico"
          label="Diagnóstico"
          placeholder="Diagnóstico clínico..."
          :rows="3"
          :error="formErrors.diagnostico"
          required
        />

        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <AppSelect
            v-model="form.pronostico"
            id="pronostico"
            label="Pronóstico"
            :options="pronosticoOptions"
          />
          <AppInput
            v-model="form.pesoConsulta"
            id="peso"
            label="Peso (kg)"
            type="number"
            step="0.01"
            placeholder="Ej: 12.5"
          />
          <AppInput
            v-model="form.temperatura"
            id="temperatura"
            label="Temperatura (°C)"
            type="number"
            step="0.1"
            placeholder="Ej: 38.5"
          />
        </div>

        <AppTextarea
          v-model="form.observaciones"
          id="observaciones"
          label="Observaciones"
          placeholder="Notas adicionales..."
          :rows="2"
        />

        <FormActions
          :loading="guardando"
          submit-label="Guardar diagnóstico"
          @cancel="showModal = false"
        />
      </form>
    </AppModal>
  </DashboardLayout>
</template>