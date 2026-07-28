<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppPagination from '@/components/ui/AppPagination.vue'

import PageHeader from '@/components/common/PageHeader.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import LoadingState from '@/components/common/LoadingState.vue'
import TableViewLayout from '@/components/common/TableViewLayout.vue'

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
      <PageHeader title="Mis Diagnósticos" subtitle="Historial de consultas que has atendido" />
    </template>

    <TableViewLayout>
      <template #toolbar>
        <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">{{ error }}</AppAlert>
      </template>

      <template #content>
        <AppCard fill-height padding="none">
          <template #header>
            <div class="px-6 py-4 border-b shrink-0" style="border-color: var(--border-color)">
              <h2 class="font-semibold" style="color: var(--text-primary)">{{ totalElements }} diagnóstico(s)</h2>
            </div>
          </template>

          <LoadingState v-if="loading">Cargando diagnósticos...</LoadingState>
          <EmptyState v-else-if="diagnosticos.length === 0" title="Sin diagnósticos" message="No has registrado diagnósticos aún" />

          <div v-else class="divide-y" style="border-color: var(--border-color)">
            <div v-for="d in diagnosticos" :key="d.id" class="transition-colors">
              <button
                class="w-full px-6 py-4 flex flex-col sm:flex-row sm:items-center gap-3 text-left transition-colors hover:bg-(--bg-surface-2)"
                @click="toggleExpandir(d.id)"
              >
                <div class="shrink-0 w-24">
                  <p class="text-sm font-semibold" style="color: var(--text-primary)">{{ formatFecha(d.citaFecha) }}</p>
                  <p class="text-xs" style="color: var(--text-muted)">#{{ d.citaId }}</p>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 flex-wrap">
                    <span class="font-medium" style="color: var(--text-primary)">{{ d.mascotaNombre }}</span>
                    <AppBadge v-if="d.pronostico" :variant="colorPronostico(d.pronostico)">{{ d.pronostico }}</AppBadge>
                  </div>
                  <p class="text-sm truncate mt-0.5" style="color: var(--text-muted)">{{ d.diagnostico }}</p>
                </div>
                <div class="flex items-center gap-4 shrink-0 text-sm" style="color: var(--text-muted)">
                  <span v-if="d.pesoConsulta"><span class="font-medium" style="color: var(--text-secondary)">{{ d.pesoConsulta }}</span> kg</span>
                  <span v-if="d.temperatura"><span class="font-medium" style="color: var(--text-secondary)">{{ d.temperatura }}</span> °C</span>
                </div>
                <svg class="w-4 h-4 shrink-0 transition-transform" :class="expandido === d.id ? 'rotate-180' : ''"
                  style="color: var(--text-disabled)" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                </svg>
              </button>
              <div v-if="expandido === d.id" class="px-6 pb-5 border-t"
                style="border-color: var(--border-color); background-color: var(--bg-surface-2)">
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
                <div class="flex gap-2 mt-4 flex-wrap">
                  <AppButton size="sm" @click="router.push(`/diagnosticos/${d.id}/tratamiento`)">Ver tratamiento</AppButton>
                  <AppButton variant="ghost" size="sm" @click="router.push(`/mascotas/${d.mascotaId}/historial`)">
                    Historial de {{ d.mascotaNombre }}
                  </AppButton>
                </div>
              </div>
            </div>
          </div>

          <template #footer>
            <div class="border-t" style="border-color: var(--border-color)">
              <AppPagination :page="page" :total-pages="totalPages" :total-elements="totalElements"
                :page-size="pageSize" :loading="loading" @change="cargar" />
            </div>
          </template>
        </AppCard>
      </template>
    </TableViewLayout>
  </DashboardLayout>
</template>