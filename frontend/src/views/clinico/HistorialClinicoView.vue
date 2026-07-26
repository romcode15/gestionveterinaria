<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import LoadingState from '@/components/common/LoadingState.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { api } from '@/services/api'

const route = useRoute()
const router = useRouter()
const mascotaId = Number(route.params.id)

// ── Tipos locales ──────────────────────────────────────────────────────────

interface MascotaInfo {
  id: number
  nombre: string
  especie: { nombre: string }
  raza: { nombre: string }
  clienteNombre: string
  clienteId: number
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
    }>(`/api/historial-clinico/mascota/${mascotaId}`)
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

function irARegistrarDiagnostico(consultaId: number) {
  router.push(`/diagnosticos/nuevo?citaId=${consultaId}`)
}

function irATratamiento(consultaId: number) {
  router.push(`/diagnosticos/${consultaId}/tratamiento`)
}

function irHistorialMascota() {
  router.push(`/mascotas/${mascotaId}/historial`)
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <div class="flex items-center gap-2">
        <AppButton variant="ghost" size="sm" aria-label="Volver" @click="router.back()">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </AppButton>
        <PageHeader
          :title="mascota ? `Historial de ${mascota.nombre}` : 'Historial clínico'"
          :subtitle="mascota ? `${mascota.especie.nombre} · ${mascota.raza.nombre} · Propietario: ${mascota.clienteNombre}` : 'Cargando...'"
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
      <LoadingState v-if="loading">Cargando historial...</LoadingState>

      <!-- Sin consultas -->
      <EmptyState
        v-else-if="consultas.length === 0"
        title="Sin historial médico"
        :message="`${mascota?.nombre || 'La mascota'} aún no tiene consultas registradas`"
      />

      <!-- Lista de consultas -->
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
            <!-- Cabecera de la consulta (siempre visible) -->
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
                <span v-if="consulta.tieneTratamiento" class="text-xs font-medium flex items-center gap-1" style="color: var(--color-primary)">
                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"/>
                  </svg>
                  Tratamiento
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

            <!-- Detalle expandido -->
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
                  <div class="flex items-center justify-between mb-2">
                    <p class="text-xs font-semibold uppercase tracking-wide flex items-center gap-1" style="color: var(--text-muted)">
                      <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"/>
                      </svg>
                      Tratamiento
                    </p>
                    <AppButton size="sm" variant="ghost" @click="irATratamiento(consulta.id)">
                      Ver completo
                    </AppButton>
                  </div>
                  <div class="space-y-2">
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

              <!-- Sin diagnóstico -->
              <div v-else class="pt-3">
                <p class="text-sm" style="color: var(--text-muted)">
                  Esta consulta aún no tiene diagnóstico registrado.
                </p>
                <AppButton
                  v-if="consulta.estadoCita === 'completada' || consulta.estadoCita === 'en_curso'"
                  size="sm"
                  class="mt-3"
                  @click="irARegistrarDiagnostico(consulta.id)"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                  </svg>
                  Registrar diagnóstico
                </AppButton>
              </div>
            </div>
          </AppCard>
        </div>
      </template>
    </div>
  </DashboardLayout>
</template>