import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Cita, CitaFormData, EstadoCita } from '@/types'
import { mockCitas, mockTiposCita } from '@/data'
import { mockMedicos } from '@/data'

export const useCitasStore = defineStore('citas', () => {
  const citas = ref<Cita[]>([...mockCitas])
  const loading = ref(false)
  const fechaSeleccionada = ref<string>(new Date().toISOString().split('T')[0]!)
  const filtroMedicoId = ref<number | null>(null)
  const filtroEstado = ref<EstadoCita | 'todos'>('todos')

  const tiposCita = computed(() => mockTiposCita)
  const medicos = computed(() => mockMedicos)

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

  function getById(id: number): Cita | undefined {
    return citas.value.find((c) => c.id === id)
  }

  function calcularHoraFin(horaInicio: string, duracionMinutos: number): string {
    const [h, m] = horaInicio.split(':').map(Number)
    const totalMinutos = (h ?? 0) * 60 + (m ?? 0) + duracionMinutos
    const hFin = Math.floor(totalMinutos / 60)
    const mFin = totalMinutos % 60
    return `${String(hFin).padStart(2, '0')}:${String(mFin).padStart(2, '0')}`
  }

  async function crear(data: CitaFormData): Promise<Cita> {
    loading.value = true
    await new Promise((r) => setTimeout(r, 500))
    const tipoCita = mockTiposCita.find((t) => t.id === data.tipoCitaId)!
    const medico = mockMedicos.find((m) => m.id === data.medicoId)!
    const nueva: Cita = {
      ...data,
      id: Math.max(...citas.value.map((c) => c.id)) + 1,
      horaFin: calcularHoraFin(data.horaInicio, tipoCita.duracionMinutos),
      estado: 'pendiente',
      tipoCita,
      medicoNombre: `${medico.nombre} ${medico.apellido}`,
      mascotaNombre: '',
      clienteId: 0,
      clienteNombre: '',
      createdAt: new Date().toISOString(),
    }
    citas.value.push(nueva)
    loading.value = false
    return nueva
  }

  async function actualizar(id: number, data: Partial<CitaFormData>): Promise<void> {
    loading.value = true
    await new Promise((r) => setTimeout(r, 500))
    const idx = citas.value.findIndex((c) => c.id === id)
    if (idx !== -1) {
      citas.value[idx] = { ...citas.value[idx]!, ...data }
    }
    loading.value = false
  }

  async function cambiarEstado(id: number, estado: EstadoCita): Promise<void> {
    const cita = citas.value.find((c) => c.id === id)
    if (cita) {
      cita.estado = estado
    }
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
    getById,
    crear,
    actualizar,
    cambiarEstado,
  }
})
