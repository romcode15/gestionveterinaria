<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import LoadingState from '@/components/common/LoadingState.vue'
import { api } from '@/services/api'

const router = useRouter()

// ── Tipos ──────────────────────────────────────────────────────────────────

interface AlertaStockBajo {
  productoId: number
  nombre: string
  categoria: string
  stockActual: number
  stockMinimo: number
}

interface AlertaLoteProximo {
  productoId: number
  productoNombre: string
  loteId: number
  numeroLote: string
  fechaVencimiento: string
  diasRestantes: number
}

interface AlertaLoteVencido {
  productoId: number
  productoNombre: string
  loteId: number
  numeroLote: string
  fechaVencimiento: string
  diasVencido: number
}

interface Alertas {
  stockBajo: AlertaStockBajo[]
  lotesProximos: AlertaLoteProximo[]
  lotesVencidos: AlertaLoteVencido[]
}

// ── Estado ─────────────────────────────────────────────────────────────────

const loading = ref(false)
const error = ref<string | null>(null)
const alertas = ref<Alertas | null>(null)
const diasAlerta = ref(30)

async function cargarAlertas() {
  loading.value = true
  error.value = null
  try {
    const data = await api.get<Alertas>(`/api/inventario/alertas?dias=${diasAlerta.value}`)
    alertas.value = data
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar alertas'
  } finally {
    loading.value = false
  }
}

onMounted(cargarAlertas)

function irAProductos() {
  router.push('/inventario/productos')
}

function irAProveedores() {
  router.push('/inventario/proveedores')
}

function formatFecha(f: string) {
  return new Date(f + 'T00:00:00').toLocaleDateString('es-CO')
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Inventario" subtitle="Alertas y resumen" />
    </template>

    <!-- Contenedor flex para toda la vista -->
    <div class="flex flex-col h-full gap-4 p-6 overflow-y-auto">
      <Transition name="fade">
        <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">{{ error }}</AppAlert>
      </Transition>

      <!-- Acceso rápido -->
      <div class="flex gap-3 flex-wrap shrink-0">
        <AppButton @click="irAProductos">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
          Productos
        </AppButton>
        <AppButton variant="secondary" @click="irAProveedores">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          Proveedores
        </AppButton>
      </div>

      <LoadingState v-if="loading">Cargando alertas...</LoadingState>

      <template v-else-if="alertas">
        <AppCard>
          <div class="flex items-center justify-between mb-3">
            <h3 class="font-semibold flex items-center gap-2" style="color: var(--text-primary)">
              <svg class="w-4 h-4 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
              </svg>
              Stock bajo ({{ alertas.stockBajo.length }})
            </h3>
            <AppBadge variant="danger">{{ alertas.stockBajo.length }}</AppBadge>
          </div>
          <div v-if="alertas.stockBajo.length === 0" style="color: var(--text-muted)">Sin alertas de stock bajo</div>
          <div v-else class="space-y-2">
            <div v-for="item in alertas.stockBajo" :key="item.productoId"
              class="flex flex-col sm:flex-row sm:items-center justify-between p-2 rounded-lg"
              style="background-color: var(--bg-surface-2)">
              <div>
                <span class="font-medium" style="color: var(--text-primary)">{{ item.nombre }}</span>
                <span class="text-xs ml-2" style="color: var(--text-muted)">{{ item.categoria }}</span>
              </div>
              <span class="text-sm font-medium" style="color: var(--color-danger)">
                {{ item.stockActual }} / {{ item.stockMinimo }} (mínimo)
              </span>
            </div>
          </div>
        </AppCard>

        <AppCard>
          <div class="flex items-center justify-between mb-3">
            <h3 class="font-semibold flex items-center gap-2" style="color: var(--text-primary)">
              <svg class="w-4 h-4 text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
              </svg>
              Lotes próximos a vencer ({{ alertas.lotesProximos.length }})
            </h3>
            <AppBadge variant="warning">{{ alertas.lotesProximos.length }}</AppBadge>
          </div>
          <div v-if="alertas.lotesProximos.length === 0" style="color: var(--text-muted)">Sin lotes próximos a vencer</div>
          <div v-else class="space-y-2">
            <div v-for="item in alertas.lotesProximos" :key="item.loteId"
              class="flex flex-col sm:flex-row sm:items-center justify-between p-2 rounded-lg"
              style="background-color: var(--bg-surface-2)">
              <div>
                <span class="font-medium" style="color: var(--text-primary)">{{ item.productoNombre }}</span>
                <span class="text-xs ml-2" style="color: var(--text-muted)">Lote {{ item.numeroLote }}</span>
              </div>
              <span class="text-sm" style="color: var(--text-secondary)">
                Vence: {{ formatFecha(item.fechaVencimiento) }}
                <span class="font-medium" style="color: var(--color-warning)">({{ item.diasRestantes }} días)</span>
              </span>
            </div>
          </div>
        </AppCard>

        <AppCard>
          <div class="flex items-center justify-between mb-3">
            <h3 class="font-semibold flex items-center gap-2" style="color: var(--text-primary)">
              <svg class="w-4 h-4 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
              </svg>
              Lotes vencidos ({{ alertas.lotesVencidos.length }})
            </h3>
            <AppBadge variant="danger">{{ alertas.lotesVencidos.length }}</AppBadge>
          </div>
          <div v-if="alertas.lotesVencidos.length === 0" style="color: var(--text-muted)">Sin lotes vencidos</div>
          <div v-else class="space-y-2">
            <div v-for="item in alertas.lotesVencidos" :key="item.loteId"
              class="flex flex-col sm:flex-row sm:items-center justify-between p-2 rounded-lg"
              style="background-color: var(--bg-surface-2)">
              <div>
                <span class="font-medium" style="color: var(--text-primary)">{{ item.productoNombre }}</span>
                <span class="text-xs ml-2" style="color: var(--text-muted)">Lote {{ item.numeroLote }}</span>
              </div>
              <span class="text-sm" style="color: var(--color-danger)">
                Vencido: {{ formatFecha(item.fechaVencimiento) }} ({{ item.diasVencido }} días)
              </span>
            </div>
          </div>
        </AppCard>
      </template>

      <EmptyState v-else title="Sin alertas" message="No hay alertas de inventario" />
    </div>
  </DashboardLayout>
</template>