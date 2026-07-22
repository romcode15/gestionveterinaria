<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { api } from '@/services/api'

const route = useRoute()
const router = useRouter()
const mascotaId = Number(route.params.id)

// ── Tipos locales (misma estructura que HistorialClinicoView) ─────────────

interface MascotaInfo {
  id: number
  nombre: string
  especie: { nombre: string }
  raza: { nombre: string }
  clienteNombre: string
}

interface ConsultaItem {
  id: number
  fecha: string
  tipoCita: string
  medicoNombre: string
  motivo: string
  sintomas?: string
  diagnostico?: string
  pronostico?: string
  pesoConsulta?: number
  temperatura?: number
  observaciones?: string
  tieneDiagnostico: boolean
  tieneTratamiento: boolean
  tratamiento?: {
    id: number
    medicamentos: Array<{
      medicamento: string
      dosis: string
      frecuencia: string
      duracionDias: number
      viaAdministracion: string
    }>
  }
  estadoCita: string
}

// ── Estado ──────────────────────────────────────────────────────────────────

const loading = ref(false)
const error = ref<string | null>(null)
const mascota = ref<MascotaInfo | null>(null)
const consultas = ref<ConsultaItem[]>([])
const expandido = ref<number | null>(null)

// ── Carga de datos ─────────────────────────────────────────────────────────

async function cargarHistorial() {
  loading.value = true
  error.value = null
  try {
    const data = await api.get<{
      mascota: MascotaInfo
      consultas: ConsultaItem[]
    }>(`/api/portal/cliente/mascotas/${mascotaId}/historial`)
    mascota.value = data.mascota
    consultas.value = data.consultas
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar el historial'
  } finally {
    loading.value = false
  }
}

onMounted(cargarHistorial)

// ── Helpers ─────────────────────────────────────────────────────────────────

function toggleExpandir(id: number) {
  expandido.value = expandido.value === id ? null : id
}

function formatFecha(fecha: string): string {
  return new Date(fecha + 'T00:00:00').toLocaleDateString('es-CO', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}

function colorPronostico(p: string): string {
  const map: Record<string, string> = {
    favorable: 'success',
    reservado: 'warning',
    grave: 'danger',
    muerte: 'neutral',
  }
  return map[p] || 'neutral'
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <div class="flex items-center gap-2">
        <button
          class="p-1.5 rounded-lg transition-colors"
          style="color: var(--text-muted)"
          @click="router.back()"
          aria-label="Volver"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <PageHeader
          :title="mascota ? `Historial de ${mascota.nombre}` : 'Mi historial'"
          :subtitle="mascota ? `${mascota.especie.nombre} · ${mascota.raza.nombre}` : 'Cargando...'"
        />
      </div>
    </template>

    <div class="space-y-4 max-w-4xl mx-auto">
      <Transition name="fade">
        <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">
          {{ error }}
        </AppAlert>
      </Transition>

      <!-- Loading -->
      <div v-if="loading" class="space-y-3">
        <div class="h-20 vg-skeleton rounded-xl animate-pulse" />
        <div v-for="i in 3" :key="i" class="h-24 vg-skeleton rounded-xl animate-pulse" />
      </div>

      <!-- Sin consultas -->
      <EmptyState
        v-else-if="consultas.length === 0"
        icon="📋"
        title="Sin historial médico"
        :message="`${mascota?.nombre || 'La mascota'} aún no tiene consultas registradas`"
      />

      <!-- Lista de consultas (mismo renderizado que HistorialClinicoView pero sin botón de registrar diagnóstico) -->
      <template v-else>
        <div class="flex items-center justify-between mb-2">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ consultas.length }} consulta(s)
          </h2>
        </div>

        <div class="space-y-3">
          <AppCard
            v-for="consulta in consultas"
            :key="consulta.id"
            padding="none"
            class="overflow-hidden"
          >
            <!-- Cabecera -->
            <button
              class="w-full px-5 py-4 flex flex-col sm:flex-row sm:items-center gap-3 text-left transition-colors hover:bg-(--bg-surface-2)"
              @click="toggleExpandir(consulta.id)"
            >
              <div class="shrink-0 w-28">
                <p class="text-sm font-semibold" style="color: var(--text-primary)">
                  {{ formatFecha(consulta.fecha) }}
                </p>
                <p class="text-xs" style="color: var(--text-muted)">{{ consulta.medicoNombre }}</p>
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2 flex-wrap">
                  <span class="font-medium" style="color: var(--text-primary)">{{ consulta.tipoCita }}</span>
                  <AppBadge variant="neutral" size="sm">{{ consulta.estadoCita }}</AppBadge>
                  <StatusBadge
                    v-if="consulta.tieneDiagnostico"
                    status="diagnosticado"
                    size="sm"
                    :config="{
                      diagnosticado: { variant: 'success', label: 'Diagnosticado' }
                    }"
                  />
                </div>
                <p class="text-sm truncate mt-0.5" style="color: var(--text-muted)">
                  {{ consulta.motivo }}
                </p>
              </div>
              <div class="flex items-center gap-2 shrink-0">
                <span v-if="consulta.tieneTratamiento" class="text-xs font-medium" style="color: var(--color-primary)">
                  💊 Tratamiento
                </span>
                <svg
                  class="w-4 h-4 transition-transform"
                  :class="expandido === consulta.id ? 'rotate-180' : ''"
                  style="color: var(--text-disabled)"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </div>
            </button>

            <!-- Detalle expandido (sin botón de registrar diagnóstico) -->
            <div
              v-if="expandido === consulta.id"
              class="px-5 pb-5 pt-2 border-t"
              style="border-color: var(--border-color); background-color: var(--bg-surface-2)"
            >
              <div v-if="consulta.tieneDiagnostico">
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-3">
                  <div>
                    <p class="text-xs font-semibold uppercase tracking-wide" style="color: var(--text-muted)">
                      Síntomas
                    </p>
                    <p class="text-sm mt-1" style="color: var(--text-secondary)">
                      {{ consulta.sintomas || 'No registrados' }}
                    </p>
                  </div>
                  <div>
                    <p class="text-xs font-semibold uppercase tracking-wide" style="color: var(--text-muted)">
                      Diagnóstico
                    </p>
                    <p class="text-sm mt-1" style="color: var(--text-secondary)">
                      {{ consulta.diagnostico || 'No registrado' }}
                    </p>
                  </div>
                  <div v-if="consulta.pronostico">
                    <p class="text-xs font-semibold uppercase tracking-wide" style="color: var(--text-muted)">
                      Pronóstico
                    </p>
                    <AppBadge :variant="colorPronostico(consulta.pronostico)" size="sm" class="mt-1">
                      {{ consulta.pronostico }}
                    </AppBadge>
                  </div>
                  <div class="flex gap-4">
                    <div v-if="consulta.pesoConsulta">
                      <p class="text-xs font-semibold uppercase tracking-wide" style="color: var(--text-muted)">
                        Peso
                      </p>
                      <p class="text-sm mt-1" style="color: var(--text-secondary)">{{ consulta.pesoConsulta }} kg</p>
                    </div>
                    <div v-if="consulta.temperatura">
                      <p class="text-xs font-semibold uppercase tracking-wide" style="color: var(--text-muted)">
                        Temperatura
                      </p>
                      <p class="text-sm mt-1" style="color: var(--text-secondary)">{{ consulta.temperatura }} °C</p>
                    </div>
                  </div>
                  <div v-if="consulta.observaciones" class="sm:col-span-2">
                    <p class="text-xs font-semibold uppercase tracking-wide" style="color: var(--text-muted)">
                      Observaciones
                    </p>
                    <p class="text-sm mt-1" style="color: var(--text-secondary)">{{ consulta.observaciones }}</p>
                  </div>
                </div>

                <!-- Tratamiento -->
                <div v-if="consulta.tieneTratamiento && consulta.tratamiento" class="mt-4 pt-4 border-t" style="border-color: var(--border-color)">
                  <p class="text-xs font-semibold uppercase tracking-wide" style="color: var(--text-muted)">
                    💊 Tratamiento
                  </p>
                  <div class="space-y-2 mt-2">
                    <div
                      v-for="(med, idx) in consulta.tratamiento.medicamentos"
                      :key="idx"
                      class="flex items-start gap-2 text-sm"
                    >
                      <span class="font-medium" style="color: var(--text-primary)">{{ med.medicamento }}</span>
                      <span style="color: var(--text-muted)">{{ med.dosis }}</span>
                      <span style="color: var(--text-muted)">·</span>
                      <span style="color: var(--text-muted)">{{ med.frecuencia }}</span>
                      <span style="color: var(--text-muted)">·</span>
                      <span style="color: var(--text-muted)">{{ med.duracionDias }} días</span>
                      <span style="color: var(--text-muted)">·</span>
                      <span style="color: var(--text-muted)">{{ med.viaAdministracion }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Sin diagnóstico (solo mensaje, sin botón) -->
              <div v-else class="pt-3">
                <p class="text-sm" style="color: var(--text-muted)">
                  Esta consulta aún no tiene diagnóstico registrado.
                </p>
              </div>
            </div>
          </AppCard>
        </div>
      </template>
    </div>
  </DashboardLayout>
</template>