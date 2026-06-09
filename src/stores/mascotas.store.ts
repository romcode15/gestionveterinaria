import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Mascota, MascotaFormData, Especie, Raza } from '@/types'
import { mascotasService } from '@/services/mascotas.service'

export const useMascotasStore = defineStore('mascotas', () => {
  const mascotas = ref<Mascota[]>([])
  const especiesData = ref<Especie[]>([])
  const razasData = ref<Raza[]>([])
  const loading = ref(false)
  const searchQuery = ref('')
  const filtroEspecieId = ref<number | null>(null)

  // ── computed ──────────────────────────────────────────────────────────────

  const especies = computed(() => especiesData.value)
  const razas = computed(() => razasData.value)

  const razasPorEspecie = computed(() => (especieId: number) =>
    razasData.value.filter((r) => r.especieId === especieId),
  )

  const mascotasFiltradas = computed(() => {
    let result = mascotas.value.filter((m) => m.estado === 'activo')
    if (filtroEspecieId.value !== null) {
      result = result.filter((m) => m.especieId === filtroEspecieId.value)
    }
    if (searchQuery.value.trim()) {
      const q = searchQuery.value.toLowerCase()
      result = result.filter(
        (m) =>
          m.nombre.toLowerCase().includes(q) ||
          m.clienteNombre.toLowerCase().includes(q) ||
          m.especie.nombre.toLowerCase().includes(q) ||
          m.raza.nombre.toLowerCase().includes(q),
      )
    }
    return result
  })

  const mascotasPorCliente = computed(() => (clienteId: number) =>
    mascotas.value.filter((m) => m.clienteId === clienteId && m.estado === 'activo'),
  )

  // ── acciones ──────────────────────────────────────────────────────────────

  async function cargar(): Promise<void> {
    loading.value = true
    try {
      ;[mascotas.value, especiesData.value, razasData.value] = await Promise.all([
        mascotasService.getAll(),
        mascotasService.getAllEspecies(),
        mascotasService.getAllRazas(),
      ])
    } finally {
      loading.value = false
    }
  }

  function getById(id: number): Mascota | undefined {
    return mascotas.value.find((m) => m.id === id)
  }

  async function crear(data: MascotaFormData): Promise<Mascota> {
    loading.value = true
    try {
      const nueva = await mascotasService.create(data)
      mascotas.value.push(nueva)
      return nueva
    } finally {
      loading.value = false
    }
  }

  async function actualizar(id: number, data: Partial<MascotaFormData>): Promise<void> {
    loading.value = true
    try {
      const actualizada = await mascotasService.update(id, data)
      const idx = mascotas.value.findIndex((m) => m.id === id)
      if (idx !== -1) mascotas.value[idx] = actualizada
    } finally {
      loading.value = false
    }
  }

  return {
    mascotas,
    loading,
    searchQuery,
    filtroEspecieId,
    especies,
    razas,
    razasPorEspecie,
    mascotasFiltradas,
    mascotasPorCliente,
    cargar,
    getById,
    crear,
    actualizar,
  }
})
