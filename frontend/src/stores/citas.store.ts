import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Cita, CitaFormData, EstadoCita, TipoCita, Medico } from '@/types'
import { citasService } from '@/services/citas.service'
import { medicosService } from '@/services/medicos.service'

export const useCitasStore = defineStore('citas', () => {
  const citas = ref<Cita[]>([])
  const tiposCitaData = ref<TipoCita[]>([])
  const medicosData = ref<Medico[]>([])
  const loading = ref(false)
  const fechaSeleccionada = ref<string>(new Date().toISOString().split('T')[0]!)
  const filtroMedicoId = ref<number | null>(null)
  const filtroEstado = ref<EstadoCita | 'todos'>('todos')

  // ── computed ──────────────────────────────────────────────────────────────

  const tiposCita = computed(() => tiposCitaData.value)
  const medicos = computed(() => medicosData.value)

  const citasPorFecha = computed(() => (fecha: string) =>
    citas.value.filter((c) => c.fecha === fecha),
  )

  const citasFiltradas = computed(() => {
    let result = citas.value
    if (filtroMedicoId.value !== null) {
      result = result.filter((c) => c.medicoId === filtroMedicoId.value)
    }
    if (filtroEstado.value !== 'todos') {
      result = result.filter((c) => c.estado === filtroEstado.value)
    }
    return result.sort((a, b) => {
      if (a.fecha !== b.fecha) return a.fecha.localeCompare(b.fecha)
      return a.horaInicio.localeCompare(b.horaInicio)
    })
  })

  const citasHoy = computed(() => {
    const hoy = new Date().toISOString().split('T')[0]!
    return citas.value.filter((c) => c.fecha === hoy)
  })

  const estadisticas = computed(() => ({
    total: citas.value.length,
    pendientes: citas.value.filter((c) => c.estado === 'pendiente').length,
    confirmadas: citas.value.filter((c) => c.estado === 'confirmada').length,
    enCurso: citas.value.filter((c) => c.estado === 'en_curso').length,
    completadas: citas.value.filter((c) => c.estado === 'completada').length,
    canceladas: citas.value.filter((c) => c.estado === 'cancelada').length,
  }))

  // ── acciones ──────────────────────────────────────────────────────────────

  async function cargar(): Promise<void> {
    loading.value = true
    try {
      ;[citas.value, tiposCitaData.value, medicosData.value] = await Promise.all([
        citasService.getAll(),
        citasService.getAllTiposCita(),
        medicosService.getAll(),
      ])
    } finally {
      loading.value = false
    }
  }

  // Carga solo las citas del cliente logueado (para el portal cliente)
  async function cargarMisCitas(clienteId: number): Promise<void> {
    loading.value = true
    try {
      ;[citas.value, tiposCitaData.value] = await Promise.all([
        citasService.getByClienteId(clienteId),
        citasService.getAllTiposCita(),
      ])
    } finally {
      loading.value = false
    }
  }

  function getById(id: number): Cita | undefined {
    return citas.value.find((c) => c.id === id)
  }

  async function crear(data: CitaFormData): Promise<Cita> {
    loading.value = true
    try {
      const nueva = await citasService.create(data)
      citas.value.push(nueva)
      return nueva
    } finally {
      loading.value = false
    }
  }

  async function actualizar(id: number, data: Partial<CitaFormData>): Promise<void> {
    loading.value = true
    try {
      const actualizada = await citasService.update(id, data)
      const idx = citas.value.findIndex((c) => c.id === id)
      if (idx !== -1) citas.value[idx] = actualizada
    } finally {
      loading.value = false
    }
  }

  async function cambiarEstado(id: number, estado: EstadoCita): Promise<void> {
    const actualizada = await citasService.changeStatus(id, estado)
    const idx = citas.value.findIndex((c) => c.id === id)
    if (idx !== -1) citas.value[idx] = actualizada
  }

  return {
    citas,
    loading,
    fechaSeleccionada,
    filtroMedicoId,
    filtroEstado,
    tiposCita,
    medicos,
    citasPorFecha,
    citasFiltradas,
    citasHoy,
    estadisticas,
    cargar,
    cargarMisCitas,
    getById,
    crear,
    actualizar,
    cambiarEstado,
  }
})
