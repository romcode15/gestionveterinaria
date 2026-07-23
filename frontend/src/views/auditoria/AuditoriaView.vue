<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppTable from '@/components/ui/AppTable.vue'
import AppToggle from '@/components/ui/AppToggle.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import type { TableColumn } from '@/types'
import { api } from '@/services/api'

// ── Tipos ──────────────────────────────────────────────────────────────────

interface AuditoriaEntry {
  id: number
  usuarioId: number | null
  username: string
  accion: string
  entidad: string
  entidadId: string | null
  descripcion: string | null
  ipOrigen: string | null
  endpoint: string | null
  exitoso: boolean
  errorMensaje: string | null
  createdAt: string
}

// ── Estado ─────────────────────────────────────────────────────────────────

const auditoria = ref<AuditoriaEntry[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(20)

// Filtros
const filtroUsuario = ref('')
const filtroAccion = ref('')
const filtroFechaInicio = ref('')
const filtroFechaFin = ref('')
const soloErrores = ref(false)

// ── Columnas de la tabla ───────────────────────────────────────────────────

const columns: TableColumn<AuditoriaEntry>[] = [
  { key: 'createdAt',  label: 'Fecha' },
  { key: 'username',   label: 'Usuario' },
  { key: 'accion',     label: 'Acción' },
  { key: 'entidad',    label: 'Entidad' },
  { key: 'entidadId',  label: 'ID', align: 'center' },
  { key: 'ipOrigen',   label: 'IP' },
  { key: 'exitoso',    label: 'Resultado', align: 'center' },
]

// ── Opciones de filtros ────────────────────────────────────────────────────

const accionOptions = [
  { value: '', label: 'Todas las acciones' },
  { value: 'CREATE',  label: 'Crear' },
  { value: 'UPDATE',  label: 'Actualizar' },
  { value: 'DELETE',  label: 'Eliminar' },
  { value: 'LOGIN',   label: 'Login' },
  { value: 'LOGOUT',  label: 'Logout' },
]

// ── Carga de datos ─────────────────────────────────────────────────────────

async function cargarAuditoria(p = 0) {
  loading.value = true
  error.value = null
  try {
    const params: Record<string, unknown> = { page: p, size: pageSize.value, sort: 'createdAt', dir: 'desc' }
    let endpoint = '/api/auditoria'

    if (filtroUsuario.value) {
      endpoint = `/api/auditoria/usuario/${filtroUsuario.value}`
    } else if (filtroAccion.value) {
      endpoint = `/api/auditoria/accion/${filtroAccion.value}`
    } else if (soloErrores.value) {
      endpoint = '/api/auditoria/errores'
    } else if (filtroFechaInicio.value && filtroFechaFin.value) {
      endpoint = `/api/auditoria/rango?inicio=${filtroFechaInicio.value}&fin=${filtroFechaFin.value}`
    }

    const res = await api.getPaged<AuditoriaEntry>(endpoint, params)
    auditoria.value = res.content
    page.value = res.number
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar auditoría'
  } finally {
    loading.value = false
  }
}

onMounted(() => cargarAuditoria())

watch(
  [filtroUsuario, filtroAccion, filtroFechaInicio, filtroFechaFin, soloErrores],
  () => cargarAuditoria(0),
)

// ── Helpers ────────────────────────────────────────────────────────────────

const accionVariant: Record<string, string> = {
  CREATE:  'success',
  UPDATE:  'info',
  DELETE:  'danger',
  LOGIN:   'primary',
  LOGOUT:  'neutral',
}

function colorAccion(accion: string): string {
  return accionVariant[accion] ?? 'neutral'
}

function formatFecha(fecha: string): string {
  return new Date(fecha).toLocaleString('es-CO', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Auditoría" subtitle="Registro de actividades del sistema" />
    </template>

    <div class="space-y-4">
      <!-- Error global -->
      <Transition name="fade">
        <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">
          {{ error }}
        </AppAlert>
      </Transition>

      <!-- Filtros -->
      <SearchToolbar :show-new-button="false">
        <template #filters>
          <div class="flex flex-wrap gap-3 w-full">
            <AppInput
              v-model="filtroUsuario"
              placeholder="Usuario"
              class="w-full sm:w-40"
            />
            <AppSelect
              v-model="filtroAccion"
              :options="accionOptions"
              class="w-full sm:w-40"
            />
            <AppInput
              v-model="filtroFechaInicio"
              type="date"
              class="w-full sm:w-36"
            />
            <AppInput
              v-model="filtroFechaFin"
              type="date"
              class="w-full sm:w-36"
            />
            <AppToggle
              v-model="soloErrores"
              label="Solo errores"
              aria-label="Mostrar solo errores"
            />
          </div>
        </template>
      </SearchToolbar>

      <!-- Tabla -->
      <AppCard padding="none">
        <div
          class="px-6 py-4 border-b"
          style="border-color: var(--border-color)"
        >
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ totalElements }} registro(s)
          </h2>
        </div>

        <AppTable
          :columns="columns"
          :rows="auditoria"
          :loading="loading"
          empty-message="No hay registros para los filtros seleccionados"
          row-key="id"
        >
          <!-- Fecha formateada -->
          <template #cell-createdAt="{ value }">
            <span class="text-xs whitespace-nowrap" style="color: var(--text-secondary)">
              {{ formatFecha(value as string) }}
            </span>
          </template>

          <!-- Usuario destacado -->
          <template #cell-username="{ value }">
            <span class="font-medium" style="color: var(--text-primary)">{{ value }}</span>
          </template>

          <!-- Badge de acción -->
          <template #cell-accion="{ value }">
            <AppBadge :variant="colorAccion(value as string)" size="sm">
              {{ value }}
            </AppBadge>
          </template>

          <!-- IP en muted -->
          <template #cell-ipOrigen="{ value }">
            <span class="text-xs" style="color: var(--text-muted)">{{ value ?? '—' }}</span>
          </template>

          <!-- Badge de resultado -->
          <template #cell-exitoso="{ value }">
            <AppBadge
              :variant="value ? 'success' : 'danger'"
              size="sm"
              dot
            >
              {{ value ? 'exitoso' : 'fallido' }}
            </AppBadge>
          </template>
        </AppTable>

        <div class="px-4 border-t" style="border-color: var(--border-color)">
          <AppPagination
            :page="page"
            :total-pages="totalPages"
            :total-elements="totalElements"
            :page-size="pageSize"
            :loading="loading"
            @change="cargarAuditoria"
          />
        </div>
      </AppCard>
    </div>
  </DashboardLayout>
</template>
