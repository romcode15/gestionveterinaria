<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import { api } from '@/services/api'
import type { SpringPage } from '@/services/api'

const router = useRouter()

// ── Tipos locales ──────────────────────────────────────────────────────────

interface DiagnosticoItem {
  id: number
  citaId: number
  citaFecha: string
  mascotaId: number
  mascotaNombre: string
  medicoNombre: string
  sintomas: string
  diagnostico: string
  pronostico: string | null
  pesoConsulta: number | null
  temperatura: number | null
  observaciones: string | null
  createdAt: string
  updatedAt: string
}

// ── Estado ─────────────────────────────────────────────────────────────────

const diagnosticos   = ref<DiagnosticoItem[]>([])
const loading        = ref(false)
const error          = ref<string | null>(null)
const page           = ref(0)
const totalPages     = ref(0)
const totalElements  = ref(0)
const pageSize       = ref(20)

// Diagnóstico expandido para ver detalle
const expandido      = ref<number | null>(null)

// ── Carga de datos ─────────────────────────────────────────────────────────

async function cargar(p = 0) {
  loading.value = true
  error.value   = null
  try {
    const res = await api.getPaged<DiagnosticoItem>(
      '/api/portal/medico/diagnosticos',
      { page: p, size: pageSize.value, sort: 'createdAt', dir: 'desc' }
    )
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

onMounted(() => cargar())

// ── Helpers ────────────────────────────────────────────────────────────────

function toggleExpandir(id: number) {
  expandido.value = expandido.value === id ? null : id
}

function colorPronostico(p: string | null): string {
  switch (p) {
    case 'favorable': return 'success'
    case 'reservado': return 'warning'
    case 'grave':     return 'danger'
    case 'muerte':    return 'neutral'
    default:          return 'neutral'
  }
}

function formatFecha(f: string): string {
  return new Date(f + 'T12:00:00').toLocaleDateString('es-ES', {
    day: '2-digit', month: 'short', year: 'numeric'
  })
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <div>
        <h1 class="text-lg font-semibold" style="color: var(--text-primary)">Mis Diagnósticos</h1>
        <p class="text-xs" style="color: var(--text-muted)">Historial de consultas que has atendido</p>
      </div>
    </template>

    <div class="space-y-4">
      <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">
        {{ error }}
      </AppAlert>

      <!-- Tabla / lista de diagnósticos -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-default)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ totalElements }} diagnóstico(s)
          </h2>
        </div>

        <!-- Loading skeleton -->
        <div v-if="loading" class="p-4 space-y-3">
          <div v-for="i in 5" :key="i" class="h-16 vg-skeleton rounded-xl animate-pulse" />
        </div>

        <!-- Sin datos -->
        <div
          v-else-if="diagnosticos.length === 0"
          class="py-16 text-center"
          style="color: var(--text-muted)"
        >
          <svg class="w-12 h-12 mx-auto mb-3" style="color: var(--text-disabled)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
              d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
          </svg>
          <p>No has registrado diagnósticos aún</p>
        </div>

        <!-- Lista -->
        <div v-else class="divide-y" style="border-color: var(--border-default)">
          <div
            v-for="d in diagnosticos"
            :key="d.id"
            class="transition-colors"
          >
            <!-- Fila principal -->
            <button
              class="w-full px-6 py-4 flex flex-col sm:flex-row sm:items-center gap-3 text-left transition-colors hover:bg-(--bg-surface-2)"
              @click="toggleExpandir(d.id)"
            >
              <!-- Fecha -->
              <div class="shrink-0 w-24">
                <p class="text-sm font-semibold" style="color: var(--text-primary)">
                  {{ formatFecha(d.citaFecha) }}
                </p>
                <p class="text-xs" style="color: var(--text-muted)">#{{ d.citaId }}</p>
              </div>

              <!-- Mascota -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2 flex-wrap">
                  <span class="font-medium" style="color: var(--text-primary)">
                    {{ d.mascotaNombre }}
                  </span>
                  <AppBadge
                    v-if="d.pronostico"
                    :variant="colorPronostico(d.pronostico)"
                  >
                    {{ d.pronostico }}
                  </AppBadge>
                </div>
                <p class="text-sm truncate mt-0.5" style="color: var(--text-muted)">
                  {{ d.diagnostico }}
                </p>
              </div>

              <!-- Signos vitales -->
              <div class="flex items-center gap-4 shrink-0 text-sm" style="color: var(--text-muted)">
                <span v-if="d.pesoConsulta">
                  <span class="font-medium" style="color: var(--text-secondary)">{{ d.pesoConsulta }}</span> kg
                </span>
                <span v-if="d.temperatura">
                  <span class="font-medium" style="color: var(--text-secondary)">{{ d.temperatura }}</span> °C
                </span>
              </div>

              <!-- Chevron -->
              <svg
                class="w-4 h-4 shrink-0 transition-transform"
                :class="expandido === d.id ? 'rotate-180' : ''"
                style="color: var(--text-disabled)"
                fill="none" stroke="currentColor" viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
              </svg>
            </button>

            <!-- Detalle expandido -->
            <div
              v-if="expandido === d.id"
              class="px-6 pb-5 border-t"
              style="border-color: var(--border-default); background-color: var(--bg-surface-2)"
            >
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-4">
                <!-- Síntomas -->
                <div>
                  <p class="text-xs font-semibold uppercase tracking-wide mb-1" style="color: var(--text-muted)">
                    Síntomas
                  </p>
                  <p class="text-sm" style="color: var(--text-secondary)">{{ d.sintomas }}</p>
                </div>

                <!-- Diagnóstico -->
                <div>
                  <p class="text-xs font-semibold uppercase tracking-wide mb-1" style="color: var(--text-muted)">
                    Diagnóstico
                  </p>
                  <p class="text-sm" style="color: var(--text-secondary)">{{ d.diagnostico }}</p>
                </div>

                <!-- Observaciones -->
                <div v-if="d.observaciones" class="sm:col-span-2">
                  <p class="text-xs font-semibold uppercase tracking-wide mb-1" style="color: var(--text-muted)">
                    Observaciones
                  </p>
                  <p class="text-sm" style="color: var(--text-secondary)">{{ d.observaciones }}</p>
                </div>
              </div>

              <!-- Acciones del detalle -->
              <div class="flex gap-2 mt-4 flex-wrap">
                <button
                  class="text-xs px-3 py-1.5 rounded-lg font-medium transition-colors"
                  style="background-color: var(--color-primary); color: white"
                  @click="router.push(`/diagnosticos/${d.id}/tratamiento`)"
                >
                  Ver tratamiento
                </button>
                <button
                  class="text-xs px-3 py-1.5 rounded-lg font-medium transition-colors"
                  style="background-color: var(--bg-surface-3); color: var(--text-secondary)"
                  @click="router.push(`/mascotas/${d.mascotaId}/historial`)"
                >
                  Historial de {{ d.mascotaNombre }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Paginación -->
        <div class="px-4 border-t" style="border-color: var(--border-default)">
          <AppPagination
            :page="page"
            :total-pages="totalPages"
            :total-elements="totalElements"
            :page-size="pageSize"
            :loading="loading"
            @change="cargar"
          />
        </div>
      </AppCard>
    </div>
  </DashboardLayout>
</template>
