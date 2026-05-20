import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Medico, MedicoFormData } from '@/types'
import { mockMedicos, mockEspecialidades } from '@/data'

export const useMedicosStore = defineStore('medicos', () => {
  const medicos = ref<Medico[]>([...mockMedicos])
  const loading = ref(false)
  const searchQuery = ref('')

  const especialidades = computed(() => mockEspecialidades)

  const medicosFiltrados = computed(() => {
    if (!searchQuery.value.trim()) return medicos.value
    const q = searchQuery.value.toLowerCase()
    return medicos.value.filter(
      (m) =>
        m.nombre.toLowerCase().includes(q) ||
        m.apellido.toLowerCase().includes(q) ||
        m.numeroLicencia.toLowerCase().includes(q) ||
        m.especialidades.some((e) => e.nombre.toLowerCase().includes(q)),
    )
  })

  function getById(id: number): Medico | undefined {
    return medicos.value.find((m) => m.id === id)
  }

  async function crear(data: MedicoFormData): Promise<Medico> {
    loading.value = true
    await new Promise((r) => setTimeout(r, 500))
    const especialidadesSeleccionadas = mockEspecialidades.filter((e) =>
      data.especialidadesIds.includes(e.id),
    )
    const nuevo: Medico = {
      ...data,
      id: Math.max(...medicos.value.map((m) => m.id)) + 1,
      especialidades: especialidadesSeleccionadas,
      estado: 'activo',
      createdAt: new Date().toISOString(),
    }
    medicos.value.push(nuevo)
    loading.value = false
    return nuevo
  }

  async function actualizar(id: number, data: Partial<MedicoFormData>): Promise<void> {
    loading.value = true
    await new Promise((r) => setTimeout(r, 500))
    const idx = medicos.value.findIndex((m) => m.id === id)
    if (idx !== -1) {
      const especialidadesSeleccionadas = data.especialidadesIds
        ? mockEspecialidades.filter((e) => data.especialidadesIds!.includes(e.id))
        : medicos.value[idx]!.especialidades
      medicos.value[idx] = {
        ...medicos.value[idx]!,
        ...data,
        especialidades: especialidadesSeleccionadas,
      }
    }
    loading.value = false
  }

  return {
    medicos,
    loading,
    searchQuery,
    especialidades,
    medicosFiltrados,
    getById,
    crear,
    actualizar,
  }
})
