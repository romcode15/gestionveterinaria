<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import { api } from '@/services/api'
import { useAuthStore } from '@/stores/auth.store'

const route     = useRoute()
const router    = useRouter()
const authStore = useAuthStore()

const diagnosticoId = Number(route.params.id)

// ── Tipos locales ──────────────────────────────────────────────────────────

interface ViaAdministracion { id: number; nombre: string }

interface DetalleItem {
  id?: number
  medicamento: string
  dosis: string
  frecuencia: string
  duracionDias: number | string
  viaAdministracionId: number | string
  viaAdministracionNombre?: string
  instrucciones: string
}

interface Tratamiento {
  id: number
  diagnosticoId: number
  mascotaNombre: string
  medicoNombre: string
  instruccionesGenerales: string | null
  fechaInicio: string
  fechaFin: string | null
  proximaVisita: string | null
  detalles: DetalleItem[]
  createdAt: string
  updatedAt: string
}

interface Diagnostico {
  id: number
  citaFecha: string
  mascotaNombre: string
  medicoNombre: string
  diagnostico: string
  pronostico: string | null
}

// ── Estado ─────────────────────────────────────────────────────────────────

const diagnostico     = ref<Diagnostico | null>(null)
const tratamiento     = ref<Tratamiento | null>(null)
const vias            = ref<ViaAdministracion[]>([])
const loading         = ref(false)
const guardando       = ref(false)
const error           = ref<string | null>(null)
const successMsg      = ref('')
const modoEdicion     = ref(false)

// Formulario
const form = ref({
  instruccionesGenerales: '',
  fechaInicio:  new Date().toISOString().split('T')[0]!,
  fechaFin:     '',
  proximaVisita: '',
  detalles: [] as DetalleItem[],
})

const formErrors = ref<Record<string, string>>({})

// ── Carga inicial ──────────────────────────────────────────────────────────

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([cargarDiagnostico(), cargarVias()])
    await cargarTratamiento()
  } finally {
    loading.value = false
  }
})

async function cargarDiagnostico() {
  try {
    diagnostico.value = await api.get<Diagnostico>(`/api/diagnosticos/${diagnosticoId}`)
  } catch {
    error.value = 'No se encontró el diagnóstico'
  }
}

async function cargarTratamiento() {
  try {
    tratamiento.value = await api.get<Tratamiento>(
      `/api/tratamientos/diagnostico/${diagnosticoId}`
    )
    // Prellenar formulario con datos existentes
    cargarFormDesdetratamiento()
  } catch {
    // 404 = aún no existe tratamiento, es válido
    tratamiento.value = null
  }
}

async function cargarVias() {
  try {
    vias.value = await api.get<ViaAdministracion[]>('/api/vias-administracion')
  } catch {
    vias.value = []
  }
}

function cargarFormDesdetratamiento() {
  if (!tratamiento.value) return
  const t = tratamiento.value
  form.value = {
    instruccionesGenerales: t.instruccionesGenerales ?? '',
    fechaInicio:   t.fechaInicio,
    fechaFin:      t.fechaFin ?? '',
    proximaVisita: t.proximaVisita ?? '',
    detalles: t.detalles.map(d => ({
      id:                   d.id,
      medicamento:          d.medicamento,
      dosis:                d.dosis,
      frecuencia:           d.frecuencia,
      duracionDias:         d.duracionDias,
      viaAdministracionId:  d.viaAdministracionId,
      instrucciones:        d.instrucciones ?? '',
    })),
  }
}

// ── Líneas de detalle ──────────────────────────────────────────────────────

function agregarDetalle() {
  form.value.detalles.push({
    medicamento: '', dosis: '', frecuencia: '',
    duracionDias: '', viaAdministracionId: '', instrucciones: '',
  })
}

function eliminarDetalle(idx: number) {
  form.value.detalles.splice(idx, 1)
}

// ── Guardar ────────────────────────────────────────────────────────────────

async function guardar() {
  formErrors.value = {}

  if (!form.value.fechaInicio) {
    formErrors.value.fechaInicio = 'La fecha de inicio es obligatoria'
    return
  }
  if (form.value.detalles.length === 0) {
    formErrors.value.detalles = 'Debe agregar al menos un medicamento'
    return
  }
  for (const [i, d] of form.value.detalles.entries()) {
    if (!d.medicamento.trim()) { formErrors.value[`det_${i}_medicamento`] = 'Requerido'; return }
    if (!d.dosis.trim())       { formErrors.value[`det_${i}_dosis`]       = 'Requerido'; return }
    if (!d.frecuencia.trim())  { formErrors.value[`det_${i}_frecuencia`]  = 'Requerido'; return }
    if (!d.duracionDias)       { formErrors.value[`det_${i}_duracion`]    = 'Requerido'; return }
    if (!d.viaAdministracionId){ formErrors.value[`det_${i}_via`]         = 'Requerido'; return }
  }

  guardando.value = true
  try {
    const payload = {
      diagnosticoId,
      instruccionesGenerales: form.value.instruccionesGenerales || null,
      fechaInicio:   form.value.fechaInicio,
      fechaFin:      form.value.fechaFin      || null,
      proximaVisita: form.value.proximaVisita || null,
      detalles: form.value.detalles.map(d => ({
        medicamento:         d.medicamento,
        dosis:               d.dosis,
        frecuencia:          d.frecuencia,
        duracionDias:        Number(d.duracionDias),
        viaAdministracionId: Number(d.viaAdministracionId),
        instrucciones:       d.instrucciones || null,
      })),
    }

    if (tratamiento.value) {
      tratamiento.value = await api.put<Tratamiento>(
        `/api/tratamientos/${tratamiento.value.id}`, payload
      )
    } else {
      tratamiento.value = await api.post<Tratamiento>('/api/tratamientos', payload)
    }

    modoEdicion.value = false
    cargarFormDesdetratamiento()
    successMsg.value = tratamiento.value ? 'Tratamiento actualizado' : 'Tratamiento registrado'
    setTimeout(() => (successMsg.value = ''), 3000)
  } catch (e: unknown) {
    const err = e as { campos?: Record<string, string>; message?: string }
    if (err.campos) formErrors.value = err.campos
    else error.value = err.message ?? 'Error al guardar el tratamiento'
  } finally {
    guardando.value = false
  }
}

// ── Computed ───────────────────────────────────────────────────────────────

const viasOptions = computed(() => [
  { value: '', label: 'Seleccionar vía...' },
  ...vias.value.map(v => ({ value: v.id, label: v.nombre })),
])

const puedeEditar = computed(() =>
  authStore.hasRole('admin') || authStore.hasRole('veterinario')
)
</script>

<template>
  <DashboardLayout>
    <template #header>
      <div class="flex items-center gap-2">
        <button
          class="p-1.5 rounded-lg transition-colors"
          style="color: var(--text-muted)"
          :class="'hover:bg-(--bg-surface-2)'"
          @click="router.back()"
          aria-label="Volver"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
          </svg>
        </button>
        <div>
          <h1 class="text-lg font-semibold" style="color: var(--text-primary)">Tratamiento</h1>
          <p class="text-xs" style="color: var(--text-muted)">
            {{ diagnostico ? `${diagnostico.mascotaNombre} · ${diagnostico.citaFecha}` : 'Cargando...' }}
          </p>
        </div>
      </div>
    </template>

    <div class="space-y-4 max-w-3xl mx-auto">
      <Transition name="fade">
        <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">{{ error }}</AppAlert>
      </Transition>
      <Transition name="fade">
        <AppAlert v-if="successMsg" type="success" dismissible @dismiss="successMsg = ''">{{ successMsg }}</AppAlert>
      </Transition>

      <!-- Resumen del diagnóstico -->
      <AppCard v-if="diagnostico" padding="sm">
        <div class="flex items-start gap-3">
          <div class="w-9 h-9 rounded-lg flex items-center justify-center shrink-0"
            style="background-color: rgba(5,150,105,0.1)">
            <svg class="w-5 h-5" style="color: var(--color-primary)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"
                d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
            </svg>
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 flex-wrap">
              <p class="font-semibold" style="color: var(--text-primary)">{{ diagnostico.mascotaNombre }}</p>
              <AppBadge v-if="diagnostico.pronostico" variant="neutral">{{ diagnostico.pronostico }}</AppBadge>
            </div>
            <p class="text-sm mt-0.5" style="color: var(--text-muted)">{{ diagnostico.diagnostico }}</p>
            <p class="text-xs mt-1" style="color: var(--text-disabled)">
              Dr. {{ diagnostico.medicoNombre }} · {{ diagnostico.citaFecha }}
            </p>
          </div>
        </div>
      </AppCard>

      <!-- Loading -->
      <div v-if="loading" class="space-y-3">
        <div class="h-32 vg-skeleton rounded-xl animate-pulse"/>
        <div class="h-48 vg-skeleton rounded-xl animate-pulse"/>
      </div>

      <!-- Sin tratamiento registrado -->
      <AppCard
        v-else-if="!tratamiento && !modoEdicion"
        class="py-12 text-center"
      >
        <svg class="w-12 h-12 mx-auto mb-3" style="color: var(--text-disabled)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"/>
        </svg>
        <p class="font-medium" style="color: var(--text-secondary)">Aún no hay tratamiento registrado</p>
        <p class="text-sm mt-1" style="color: var(--text-muted)">Registra el plan de medicación para esta consulta</p>
        <div v-if="puedeEditar" class="mt-4">
          <AppButton @click="() => { agregarDetalle(); modoEdicion = true }">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
            </svg>
            Registrar tratamiento
          </AppButton>
        </div>
      </AppCard>

      <!-- Vista de tratamiento (modo lectura) -->
      <template v-else-if="tratamiento && !modoEdicion">
        <!-- Cabecera del tratamiento -->
        <AppCard padding="sm">
          <div class="flex items-start justify-between gap-3">
            <div class="grid grid-cols-2 sm:grid-cols-3 gap-4 flex-1">
              <div>
                <p class="text-xs font-semibold uppercase tracking-wide" style="color: var(--text-muted)">Inicio</p>
                <p class="text-sm font-medium mt-1" style="color: var(--text-primary)">{{ tratamiento.fechaInicio }}</p>
              </div>
              <div v-if="tratamiento.fechaFin">
                <p class="text-xs font-semibold uppercase tracking-wide" style="color: var(--text-muted)">Fin</p>
                <p class="text-sm font-medium mt-1" style="color: var(--text-primary)">{{ tratamiento.fechaFin }}</p>
              </div>
              <div v-if="tratamiento.proximaVisita">
                <p class="text-xs font-semibold uppercase tracking-wide" style="color: var(--text-muted)">Próxima visita</p>
                <p class="text-sm font-medium mt-1 text-green-600">{{ tratamiento.proximaVisita }}</p>
              </div>
            </div>
            <AppButton v-if="puedeEditar" variant="ghost" size="sm" @click="modoEdicion = true">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
              </svg>
              Editar
            </AppButton>
          </div>
          <div v-if="tratamiento.instruccionesGenerales" class="mt-3 pt-3 border-t" style="border-color: var(--border-default)">
            <p class="text-xs font-semibold uppercase tracking-wide mb-1" style="color: var(--text-muted)">Instrucciones generales</p>
            <p class="text-sm" style="color: var(--text-secondary)">{{ tratamiento.instruccionesGenerales }}</p>
          </div>
        </AppCard>

        <!-- Líneas de medicamentos -->
        <AppCard padding="none">
          <div class="px-6 py-4 border-b" style="border-color: var(--border-default)">
            <h3 class="font-semibold" style="color: var(--text-primary)">
              Medicamentos ({{ tratamiento.detalles.length }})
            </h3>
          </div>
          <div class="divide-y" style="border-color: var(--border-default)">
            <div
              v-for="(d, i) in tratamiento.detalles"
              :key="d.id ?? i"
              class="px-6 py-4"
            >
              <div class="flex items-start gap-3">
                <div class="w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold shrink-0"
                  style="background-color: rgba(5,150,105,0.1); color: var(--color-primary)">
                  {{ i + 1 }}
                </div>
                <div class="flex-1">
                  <p class="font-semibold" style="color: var(--text-primary)">{{ d.medicamento }}</p>
                  <div class="flex flex-wrap gap-3 mt-1 text-sm" style="color: var(--text-muted)">
                    <span>{{ d.dosis }}</span>
                    <span>·</span>
                    <span>{{ d.frecuencia }}</span>
                    <span>·</span>
                    <span>{{ d.duracionDias }} días</span>
                    <span>·</span>
                    <span>{{ d.viaAdministracionNombre }}</span>
                  </div>
                  <p v-if="d.instrucciones" class="text-xs mt-1" style="color: var(--text-muted)">
                    {{ d.instrucciones }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </AppCard>
      </template>

      <!-- Formulario (modo edición / creación) -->
      <form v-if="modoEdicion" class="space-y-4" @submit.prevent="guardar">

        <!-- Fechas e instrucciones -->
        <AppCard>
          <h3 class="font-semibold mb-4" style="color: var(--text-primary)">Información general</h3>
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <AppInput
              v-model="form.fechaInicio"
              label="Fecha de inicio"
              type="date"
              required
              :error="formErrors.fechaInicio"
            />
            <AppInput
              v-model="form.fechaFin"
              label="Fecha de fin (opcional)"
              type="date"
            />
            <AppInput
              v-model="form.proximaVisita"
              label="Próxima visita (opcional)"
              type="date"
            />
          </div>
          <div class="mt-4">
            <AppTextarea
              v-model="form.instruccionesGenerales"
              label="Instrucciones generales (opcional)"
              placeholder="Ej: Mantener en reposo, dieta blanda..."
              :rows="2"
            />
          </div>
        </AppCard>

        <!-- Medicamentos -->
        <AppCard padding="none">
          <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-default)">
            <h3 class="font-semibold" style="color: var(--text-primary)">Medicamentos</h3>
            <AppButton type="button" size="sm" variant="secondary" @click="agregarDetalle">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
              </svg>
              Agregar
            </AppButton>
          </div>

          <div v-if="formErrors.detalles" class="px-6 pt-3">
            <p class="text-xs text-red-500">{{ formErrors.detalles }}</p>
          </div>

          <div
            v-if="form.detalles.length === 0"
            class="py-10 text-center text-sm"
            style="color: var(--text-muted)"
          >
            Haz clic en "Agregar" para añadir un medicamento
          </div>

          <div v-else class="divide-y" style="border-color: var(--border-default)">
            <div
              v-for="(d, i) in form.detalles"
              :key="i"
              class="px-6 py-5"
            >
              <div class="flex items-center justify-between mb-3">
                <span class="text-sm font-semibold" style="color: var(--text-secondary)">
                  Medicamento {{ i + 1 }}
                </span>
                <button
                  type="button"
                  class="p-1.5 rounded-lg transition-colors vg-icon-btn-danger"
                  @click="eliminarDetalle(i)"
                  aria-label="Eliminar medicamento"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                  </svg>
                </button>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <AppInput
                  v-model="d.medicamento"
                  label="Medicamento"
                  placeholder="Ej: Amoxicilina 250mg"
                  required
                  :error="formErrors[`det_${i}_medicamento`]"
                />
                <AppInput
                  v-model="d.dosis"
                  label="Dosis"
                  placeholder="Ej: 1 comprimido"
                  required
                  :error="formErrors[`det_${i}_dosis`]"
                />
                <AppInput
                  v-model="d.frecuencia"
                  label="Frecuencia"
                  placeholder="Ej: Cada 8 horas"
                  required
                  :error="formErrors[`det_${i}_frecuencia`]"
                />
                <AppInput
                  v-model="d.duracionDias"
                  label="Duración (días)"
                  type="number"
                  placeholder="Ej: 7"
                  required
                  :error="formErrors[`det_${i}_duracion`]"
                />
                <AppSelect
                  v-model="d.viaAdministracionId"
                  label="Vía de administración"
                  :options="viasOptions"
                  required
                  :error="formErrors[`det_${i}_via`]"
                />
                <AppInput
                  v-model="d.instrucciones"
                  label="Instrucciones (opcional)"
                  placeholder="Ej: Con comida"
                />
              </div>
            </div>
          </div>
        </AppCard>

        <!-- Botones -->
        <div class="flex gap-3 justify-end">
          <AppButton
            type="button"
            variant="ghost"
            @click="() => { modoEdicion = false; cargarFormDesdetratamiento() }"
          >
            Cancelar
          </AppButton>
          <AppButton type="submit" :loading="guardando">
            {{ tratamiento ? 'Guardar cambios' : 'Registrar tratamiento' }}
          </AppButton>
        </div>
      </form>
    </div>
  </DashboardLayout>
</template>
