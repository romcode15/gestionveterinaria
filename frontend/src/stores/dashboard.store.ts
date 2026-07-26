import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '@/services/api'

interface ResumenGeneral {
  totalClientesActivos: number
  totalMascotasActivas: number
  totalMedicosDisponibles: number
  totalCitasHoy: number
}

export const useDashboardStore = defineStore('dashboard', () => {
  const resumen  = ref<ResumenGeneral | null>(null)
  const loading  = ref(false)
  const error    = ref<string | null>(null)

  async function cargarResumen(): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      resumen.value = await api.get<ResumenGeneral>('/api/dashboard/resumen-general')
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar resumen'
    } finally {
      loading.value = false
    }
  }

  return { resumen, loading, error, cargarResumen }
})
