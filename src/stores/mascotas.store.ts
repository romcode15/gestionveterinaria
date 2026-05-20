import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Mascota, MascotaFormData } from '@/types'
import { mockMascotas, mockEspecies, mockRazas } from '@/data'

export const useMascotasStore = defineStore('mascotas', () => {
  const mascotas = ref<Mascota[]>([...mockMascotas])
  const loading = ref(false)
  const searchQuery = ref('')
  const filtroEspecieId = ref<number | null>(null)

  const especies = computed(() => mockEspecies)
  const razas = computed(() => mockRazas)

  const razasPorEspecie = computed(() => (especieId: number) =>
    mockRazas.filter((r) => r.especieId === especieId),
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

  function getById(id: number): Mascota | undefined {
    return mascotas.value.find((m) => m.id === id)
  }

  async function crear(data: MascotaFormData): Promise<Mascota> {
    loading.value = true
    await new Promise((r) => setTimeout(r, 500))
    const especie = mockEspecies.find((e) => e.id === data.especieId)!
    const raza = mockRazas.find((r) => r.id === data.razaId)!
    const nuevo: Mascota = {
      ...data,
      id: Math.max(...mascotas.value.map((m) => m.id)) + 1,
      especie,
      raza,
      clienteNombre: '',
      estado: 'activo',
      createdAt: new Date().toISOString(),
    }
    mascotas.value.push(nuevo)
    loading.value = false
    return nuevo
  }

  async function actualizar(id: number, data: Partial<MascotaFormData>): Promise<void> {
    loading.value = true
    await new Promise((r) => setTimeout(r, 500))
    const idx = mascotas.value.findIndex((m) => m.id === id)
    if (idx !== -1) {
      const especie = data.especieId
        ? mockEspecies.find((e) => e.id === data.especieId)!
        : mascotas.value[idx]!.especie
      const raza = data.razaId
        ? mockRazas.find((r) => r.id === data.razaId)!
        : mascotas.value[idx]!.raza
      mascotas.value[idx] = { ...mascotas.value[idx]!, ...data, especie, raza }
    }
    loading.value = false
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
    getById,
    crear,
    actualizar,
  }
})
