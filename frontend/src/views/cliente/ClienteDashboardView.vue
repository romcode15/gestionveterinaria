<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import MascotaCard from '@/components/mascotas/MascotaCard.vue'
import CitaStatusBadge from '@/components/citas/CitaStatusBadge.vue'
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
      <div>
        <h1 class="text-lg font-semibold text-slate-800">Mi portal</h1>
        <p class="text-xs text-slate-500">Bienvenido a tu espacio</p>
      </div>
    </template>

    <!-- Saludo -->
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-slate-800">
        {{ hora }}, {{ authStore.usuario?.nombre }} 👋
      </h2>
      <p class="text-slate-500 mt-1">Aquí puedes ver tus mascotas y citas</p>
    </div>

    <!-- Stats rápidas -->
    <div class="grid grid-cols-2 sm:grid-cols-3 gap-4 mb-6">
      <AppCard padding="sm">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-primary-100 flex items-center justify-center shrink-0">
            <span class="text-xl">🐾</span>
          </div>
          <div>
            <p class="text-2xl font-bold text-slate-800">{{ misMascotas.length }}</p>
            <p class="text-xs text-slate-500">Mis mascotas</p>
          </div>
        </div>
      </AppCard>

      <AppCard padding="sm">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-accent-100 flex items-center justify-center shrink-0">
            <svg class="w-5 h-5 text-accent-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
          <div>
            <p class="text-2xl font-bold text-slate-800">{{ proximasCitas.length }}</p>
            <p class="text-xs text-slate-500">Citas próximas</p>
          </div>
        </div>
      </AppCard>

      <AppCard padding="sm">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl bg-secondary-100 flex items-center justify-center shrink-0">
            <svg class="w-5 h-5 text-secondary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <p class="text-2xl font-bold text-slate-800">
              {{ misCitas.filter((c) => c.estado === 'completada').length }}
            </p>
            <p class="text-xs text-slate-500">Citas completadas</p>
          </div>
        </div>
      </AppCard>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">

      <!-- Mis mascotas -->
      <div>
        <div class="flex items-center justify-between mb-3">
          <h3 class="font-semibold text-slate-800">Mis mascotas</h3>
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

        <AppCard v-else class="py-10 text-center text-slate-400">
          <span class="text-4xl block mb-2">🐾</span>
          <p class="text-sm">Aún no tienes mascotas registradas</p>
        </AppCard>
      </div>

      <!-- Mis citas próximas -->
      <div>
        <div class="flex items-center justify-between mb-3">
          <h3 class="font-semibold text-slate-800">Mis próximas citas</h3>
          <AppBadge variant="warning">{{ proximasCitas.length }}</AppBadge>
        </div>

        <AppCard padding="none">
          <div class="divide-y divide-slate-100">
            <div
              v-if="proximasCitas.length === 0"
              class="py-10 text-center text-slate-400 text-sm"
            >
              <svg class="w-8 h-8 text-slate-300 mx-auto mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                  d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              No tienes citas próximas
            </div>

            <div
              v-for="cita in proximasCitas"
              :key="cita.id"
              class="px-4 py-3 flex items-start gap-3"
            >
              <!-- Barra de color -->
              <div
                class="w-1 rounded-full shrink-0 self-stretch"
                :style="{ backgroundColor: cita.tipoCita.color }"
              />

              <!-- Fecha y hora -->
              <div class="text-center shrink-0 w-14">
                <p class="text-xs font-semibold text-slate-500 capitalize leading-tight">
                  {{ formatFecha(cita.fecha) }}
                </p>
                <p class="text-sm font-bold text-slate-800">{{ cita.horaInicio }}</p>
              </div>

              <!-- Info -->
              <div class="flex-1 min-w-0">
                <p class="font-medium text-slate-800 text-sm truncate">{{ cita.mascotaNombre }}</p>
                <p class="text-xs text-slate-500 truncate">{{ cita.tipoCita.nombre }}</p>
                <p class="text-xs text-slate-400 truncate mt-0.5">{{ cita.medicoNombre }}</p>
              </div>

              <CitaStatusBadge :estado="cita.estado" size="sm" />
            </div>
          </div>
        </AppCard>

        <!-- Historial completo -->
        <button
          @click="router.push('/mis-citas')"
          class="mt-3 w-full text-sm text-primary-600 hover:text-primary-700 font-medium
                 py-2 rounded-lg hover:bg-primary-50 transition-colors text-center"
        >
          Ver historial completo →
        </button>
      </div>
    </div>
  </DashboardLayout>
</template>
