<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { api } from '@/services/api'

// ── Tipos ──────────────────────────────────────────────────────────────────

interface AuditoriaEntry {
  id: number
  fecha: string
  usuario: string
  accion: string
  entidad: string
  registroId: number
  ip: string
  resultado: 'exitoso' | 'fallido'
  detalles?: string
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
const filtroEntidad = ref('')
const filtroFechaInicio = ref('')
const filtroFechaFin = ref('')
const soloErrores = ref(false)

// ── Opciones ───────────────────────────────────────────────────────────────

const accionOptions = [
  { value: '', label: 'Todas las acciones' },
  { value: 'CREATE', label: 'Crear' },
  { value: 'UPDATE', label: 'Actualizar' },
  { value: 'DELETE', label: 'Eliminar' },
  { value: 'LOGIN', label: 'Login' },
  { value: 'LOGOUT', label: 'Logout' },
]

const entidadOptions = [
  { value: '', label: 'Todas las entidades' },
  { value: 'Usuario', label: 'Usuario' },
  { value: 'Cliente', label: 'Cliente' },
  { value: 'Mascota', label: 'Mascota' },
  { value: 'Cita', label: 'Cita' },
  { value: 'Diagnostico', label: 'Diagnóstico' },
  { value: 'Tratamiento', label: 'Tratamiento' },
  { value: 'Vacuna', label: 'Vacuna' },
  { value: 'Producto', label: 'Producto' },
  { value: 'Proveedor', label: 'Proveedor' },
  { value: 'Lote', label: 'Lote' },
]

// ── Carga de datos ─────────────────────────────────────────────────────────

async function cargarAuditoria(p = 0) {
  loading.value = true
  error.value = null
  try {
    let endpoint = '/api/auditoria'
    const params: any = { page: p, size: pageSize.value, sort: 'fecha', dir: 'desc' }

    // Construir endpoint según filtros
    if (filtroUsuario.value) {
      endpoint = `/api/auditoria/usuario/${filtroUsuario.value}`
    } else if (filtroAccion.value) {
      endpoint = `/api/auditoria/accion/${filtroAccion.value}`
    } else if (soloErrores.value) {
      endpoint = '/api/auditoria/errores'
    } else if (filtroFechaInicio.value && filtroFechaFin.value) {
      endpoint = `/api/auditoria/rango?inicio=${filtroFechaInicio.value}&fin=${filtroFechaFin.value}`
    }

    // Si es un endpoint con query params adicionales, manejarlo
    if (endpoint.includes('?')) {
      const res = await api.getPaged<AuditoriaEntry>(endpoint, params)
      auditoria.value = res.content
      page.value = res.number
      totalPages.value = res.totalPages
      totalElements.value = res.totalElements
    } else {
      const res = await api.getPaged<AuditoriaEntry>(endpoint, params)
      auditoria.value = res.content
      page.value = res.number
      totalPages.value = res.totalPages
      totalElements.value = res.totalElements
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar auditoría'
  } finally {
    loading.value = false
  }
}

onMounted(() => cargarAuditoria())

// Resetear página al cambiar filtros
watch(
  [filtroUsuario, filtroAccion, filtroEntidad, filtroFechaInicio, filtroFechaFin, soloErrores],
  () => cargarAuditoria(0)
)

// ── Helpers ─────────────────────────────────────────────────────────────────

function colorAccion(accion: string): string {
  const map: Record<string, string> = {
    CREATE: 'success',
    UPDATE: 'info',
    DELETE: 'danger',
    LOGIN: 'primary',
    LOGOUT: 'neutral',
  }
  return map[accion] || 'neutral'
}

function formatFecha(fecha: string): string {
  return new Date(fecha).toLocaleString('es-CO', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Auditoría" subtitle="Registro de actividades del sistema" />
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">{{ error }}</AppAlert>
      </Transition>

      <!-- Toolbar con filtros -->
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
            <AppSelect
              v-model="filtroEntidad"
              :options="entidadOptions"
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
            <div class="flex items-center gap-2">
              <button
                type="button"
                @click="soloErrores = !soloErrores"
                :class="[
                  'relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-1',
                  soloErrores ? 'bg-primary-600' : 'bg-slate-300',
                ]"
                role="switch"
                :aria-checked="soloErrores"
              >
                <span
                  :class="[
                    'inline-block h-4 w-4 transform rounded-full bg-white shadow transition-transform',
                    soloErrores ? 'translate-x-6' : 'translate-x-1',
                  ]"
                />
              </button>
              <span class="text-sm" style="color: var(--text-secondary)">Solo errores</span>
            </div>
          </div>
        </template>
      </SearchToolbar>

      <!-- Tabla de auditoría -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ totalElements }} registro(s)
          </h2>
        </div>

        <div v-if="loading" class="p-4 space-y-3">
          <div v-for="i in 5" :key="i" class="h-12 vg-skeleton rounded-xl animate-pulse" />
        </div>

        <EmptyState
          v-else-if="auditoria.length === 0"
          icon="📋"
          title="Sin registros"
          message="No hay registros de auditoría para los filtros seleccionados"
        />

        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="vg-table-head">
                <th class="px-4 py-3 text-left font-semibold whitespace-nowrap" style="color: var(--text-muted)">Fecha</th>
                <th class="px-4 py-3 text-left font-semibold whitespace-nowrap" style="color: var(--text-muted)">Usuario</th>
                <th class="px-4 py-3 text-left font-semibold whitespace-nowrap" style="color: var(--text-muted)">Acción</th>
                <th class="px-4 py-3 text-left font-semibold whitespace-nowrap" style="color: var(--text-muted)">Entidad</th>
                <th class="px-4 py-3 text-center font-semibold whitespace-nowrap" style="color: var(--text-muted)">ID</th>
                <th class="px-4 py-3 text-left font-semibold whitespace-nowrap" style="color: var(--text-muted)">IP</th>
                <th class="px-4 py-3 text-center font-semibold whitespace-nowrap" style="color: var(--text-muted)">Resultado</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="entry in auditoria"
                :key="entry.id"
                class="vg-table-divider last:border-0 vg-table-row-hover transition-colors"
              >
                <td class="px-4 py-3 text-xs whitespace-nowrap" style="color: var(--text-secondary)">
                  {{ formatFecha(entry.fecha) }}
                </td>
                <td class="px-4 py-3 font-medium" style="color: var(--text-primary)">{{ entry.usuario }}</td>
                <td class="px-4 py-3">
                  <AppBadge :variant="colorAccion(entry.accion)" size="sm">
                    {{ entry.accion }}
                  </AppBadge>
                </td>
                <td class="px-4 py-3" style="color: var(--text-secondary)">{{ entry.entidad }}</td>
                <td class="px-4 py-3 text-center" style="color: var(--text-secondary)">{{ entry.registroId }}</td>
                <td class="px-4 py-3 text-xs" style="color: var(--text-muted)">{{ entry.ip }}</td>
                <td class="px-4 py-3 text-center">
                  <AppBadge :variant="entry.resultado === 'exitoso' ? 'success' : 'danger'" size="sm">
                    {{ entry.resultado }}
                  </AppBadge>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

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