import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Medico, MedicoFormData, Especialidad } from '@/types'
import { medicosService } from '@/services/medicos.service'

export const useMedicosStore = defineStore('medicos', () => {
  const medicos = ref<Medico[]>([])
  const especialidadesData = ref<Especialidad[]>([])
  const loading = ref(false)
  const searchQuery = ref('')

  // ── computed ──────────────────────────────────────────────────────────────

  const especialidades = computed(() => especialidadesData.value)

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

  // ── acciones ──────────────────────────────────────────────────────────────

  async function cargar(): Promise<void> {
    loading.value = true
    try {
      ;[medicos.value, especialidadesData.value] = await Promise.all([
        medicosService.getAll(),
        medicosService.getAllEspecialidades(),
      ])
    } finally {
      loading.value = false
    }
  }

  function getById(id: number): Medico | undefined {
    return medicos.value.find((m) => m.id === id)
  }

  async function crear(data: MedicoFormData): Promise<Medico> {
    loading.value = true
    try {
      const nuevo = await medicosService.create(data)
      medicos.value.push(nuevo)
      return nuevo
    } finally {
      loading.value = false
    }
  }

  async function actualizar(id: number, data: Partial<MedicoFormData>): Promise<void> {
    loading.value = true
    try {
      const actualizado = await medicosService.update(id, data)
      const idx = medicos.value.findIndex((m) => m.id === id)
      if (idx !== -1) medicos.value[idx] = actualizado
    } finally {
      loading.value = false
    }
  }

  return {
    medicos,
    loading,
    searchQuery,
    especialidades,
    medicosFiltrados,
    cargar,
    getById,
    crear,
    actualizar,
  }
})
