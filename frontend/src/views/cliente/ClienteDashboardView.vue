<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppButton from '@/components/ui/AppButton.vue'
import MascotaCard from '@/components/mascotas/MascotaCard.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'

import PageHeader from '@/components/common/PageHeader.vue'
import EntitySummary from '@/components/common/EntitySummary.vue'
import EmptyState from '@/components/common/EmptyState.vue'

import { useAuthStore } from '@/stores/auth.store'
import { useMascotasStore } from '@/stores/mascotas.store'
import { useCitasStore } from '@/stores/citas.store'

const router = useRouter()
const authStore = useAuthStore()
const mascotasStore = useMascotasStore()
const citasStore = useCitasStore()

onMounted(async () => {
  const clienteId = authStore.clienteId
  if (!clienteId) return
  await Promise.all([
    mascotasStore.cargar(),
    citasStore.cargarMisCitas(clienteId),
  ])
})

// Mascotas solo del cliente logueado
const misMascotas = computed(() => {
  if (!authStore.clienteId) return []
  return mascotasStore.mascotasPorCliente(authStore.clienteId)
})

// Citas ya cargadas filtradas solo para este cliente, ordenadas por fecha y hora
const misCitas = computed(() =>
  [...citasStore.citas].sort((a, b) => {
    if (a.fecha !== b.fecha) return a.fecha.localeCompare(b.fecha)
    return a.horaInicio.localeCompare(b.horaInicio)
  }),
)

// Próximas citas (pendientes o confirmadas)
const proximasCitas = computed(() =>
  misCitas.value.filter((c) => c.estado === 'pendiente' || c.estado === 'confirmada').slice(0, 5),
)

const hora = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return 'Buenos días'
  if (h < 18) return 'Buenas tardes'
  return 'Buenas noches'
})

const summaryItems = computed(() => [
  { label: 'Mis mascotas',       value: misMascotas.value.length,
    svgPath: 'M14.121 14.121L19 19m-7-7l7-7m-7 7l-2.879 2.879M12 12L9.121 9.121m0 5.758a3 3 0 10-4.243 4.243 3 3 0 004.243-4.243zm0-5.758a3 3 0 10-4.243-4.243 3 3 0 004.243 4.243z',
    iconColor: '#0d9488', iconBg: 'rgba(13,148,136,0.12)' },
  { label: 'Citas próximas',     value: proximasCitas.value.length,
    svgPath: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z',
    iconColor: '#f59e0b', iconBg: 'rgba(245,158,11,0.12)' },
  { label: 'Citas completadas',  value: misCitas.value.filter((c) => c.estado === 'completada').length,
    svgPath: 'M5 13l4 4L19 7',
    iconColor: '#059669', iconBg: 'rgba(5,150,105,0.12)' },
])

function formatFecha(fecha: string): string {
  return new Date(fecha + 'T00:00:00').toLocaleDateString('es-CO', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
  })
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Mi portal" subtitle="Bienvenido a tu espacio" />
    </template>

    <!-- Saludo -->
    <div class="mb-6">
      <h2 class="text-2xl font-bold" style="color: var(--text-primary)">
        {{ hora }}, {{ authStore.usuario?.nombre }}
      </h2>
      <p class="mt-1" style="color: var(--text-muted)">Aquí puedes ver tus mascotas y citas</p>
    </div>

    <!-- Stats con EntitySummary -->
    <EntitySummary :items="summaryItems" class="mb-6" />

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Mis mascotas -->
      <div>
        <div class="flex items-center justify-between mb-3">
          <h3 class="font-semibold" style="color: var(--text-primary)">Mis mascotas</h3>
          <AppBadge variant="primary">{{ misMascotas.length }}</AppBadge>
        </div>

        <div v-if="misMascotas.length > 0" class="space-y-3">
          <MascotaCard
            v-for="mascota in misMascotas"
            :key="mascota.id"
            :mascota="mascota"
            :readonly="true"
            @edit="() => {}"
          />
        </div>

        <EmptyState
          v-else
          title="Sin mascotas"
          message="Aún no tienes mascotas registradas"
        />
      </div>

      <!-- Mis citas próximas -->
      <div>
        <div class="flex items-center justify-between mb-3">
          <h3 class="font-semibold" style="color: var(--text-primary)">Mis próximas citas</h3>
          <AppBadge variant="warning">{{ proximasCitas.length }}</AppBadge>
        </div>

        <AppCard padding="none">
          <div class="divide-y" style="border-color: var(--border-color)">
            <EmptyState
              v-if="proximasCitas.length === 0"
              title="Sin citas próximas"
              message="No tienes citas programadas próximamente"
            />

            <div
              v-for="cita in proximasCitas"
              :key="cita.id"
              class="px-4 py-3 flex items-start gap-3"
            >
              <div
                class="w-1 rounded-full shrink-0 self-stretch"
                :style="{ backgroundColor: cita.tipoCita.color }"
              />
              <div class="text-center shrink-0 w-14">
                <p class="text-xs font-semibold capitalize leading-tight" style="color: var(--text-muted)">
                  {{ formatFecha(cita.fecha) }}
                </p>
                <p class="text-sm font-bold" style="color: var(--text-primary)">{{ cita.horaInicio }}</p>
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-medium text-sm truncate" style="color: var(--text-primary)">{{ cita.mascotaNombre }}</p>
                <p class="text-xs truncate" style="color: var(--text-secondary)">{{ cita.tipoCita.nombre }}</p>
                <p class="text-xs truncate mt-0.5" style="color: var(--text-muted)">{{ cita.medicoNombre }}</p>
              </div>
              <StatusBadge :status="cita.estado" size="sm" />
            </div>
          </div>
        </AppCard>

        <AppButton
          variant="ghost"
          full-width
          class="mt-3"
          @click="router.push('/mis-citas')"
        >
          Ver historial completo →
        </AppButton>
      </div>
    </div>
  </DashboardLayout>
</template>