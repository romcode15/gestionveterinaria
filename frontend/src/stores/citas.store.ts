import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Cita, CitaFormData, EstadoCita, TipoCita } from '@/types'
import type { Medico } from '@/types'
import type { PageParams } from '@/services/api'
import { citasService } from '@/services/citas.service'
import { medicosService } from '@/services/medicos.service'

export const useCitasStore = defineStore('citas', () => {
  // ── Estado ─────────────────────────────────────────────────────────────

  const citas         = ref<Cita[]>([])
  const citasHoy      = ref<Cita[]>([])
  const tiposCitaData = ref<TipoCita[]>([])
  const medicosData   = ref<Medico[]>([])
  const loading       = ref(false)
  const error         = ref<string | null>(null)

  // Paginación
  const page          = ref(0)
  const pageSize      = ref(10)
  const totalElements = ref(0)
  const totalPages    = ref(0)

  // Filtros activos
  const fechaSeleccionada = ref<string>(new Date().toISOString().split('T')[0]!)
  const filtroMedicoId    = ref<number | null>(null)
  const filtroEstado      = ref<EstadoCita | 'todos'>('todos')

  // ── Computed ────────────────────────────────────────────────────────────

  const tiposCita         = computed(() => tiposCitaData.value)
  const medicos           = computed(() => medicosData.value)
  const hayMasPaginas     = computed(() => page.value < totalPages.value - 1)
  const hayPaginaAnterior = computed(() => page.value > 0)

  // Citas filtradas por estado y médico (para la vista de lista)
  const citasFiltradas = computed(() => {
    let resultado = citas.value
    if (filtroEstado.value !== 'todos') {
      resultado = resultado.filter((c) => c.estado === filtroEstado.value)
    }
    if (filtroMedicoId.value !== null) {
      resultado = resultado.filter((c) => c.medicoId === filtroMedicoId.value)
    }
    return resultado
  })

  // Getter por fecha — filtra el array en memoria (para la agenda de calendario)
  function citasPorFecha(fecha: string): Cita[] {
    return citas.value.filter((c) => c.fecha === fecha)
  }

  // Estadísticas sobre la página actual (útil para la vista de agenda del día)
  const estadisticas = computed(() => ({
    total:      citas.value.length,
    pendientes: citas.value.filter((c) => c.estado === 'pendiente').length,
    confirmadas: citas.value.filter((c) => c.estado === 'confirmada').length,
    enCurso:    citas.value.filter((c) => c.estado === 'en_curso').length,
    completadas: citas.value.filter((c) => c.estado === 'completada').length,
    canceladas: citas.value.filter((c) => c.estado === 'cancelada').length,
  }))

  // ── Acciones ────────────────────────────────────────────────────────────

  async function cargarCatalogos(): Promise<void> {
    const [tipos, medicosPage] = await Promise.all([
      citasService.getAllTiposCita(),
      medicosService.getAll({ size: 100, sort: 'apellido', dir: 'asc' }),
    ])
    tiposCitaData.value = tipos
    medicosData.value   = medicosPage.content
  }

  async function cargar(params: PageParams = {}): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const p: PageParams = {
        page: params.page ?? page.value,
        size: params.size ?? pageSize.value,
        sort: params.sort ?? 'fecha',
        dir:  params.dir  ?? 'desc',
      }

      let res
      if (filtroEstado.value !== 'todos') {
        res = await citasService.getByEstado(filtroEstado.value, p)
      } else if (filtroMedicoId.value !== null) {
        res = await citasService.getByMedicoId(filtroMedicoId.value, p)
      } else {
        res = await citasService.getAll(p)
      }

      citas.value         = res.content
      page.value          = res.number
      pageSize.value      = res.size
      totalElements.value = res.totalElements
      totalPages.value    = res.totalPages
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar citas'
    } finally {
      loading.value = false
    }
  }

  // Carga las citas de un día específico para la agenda
  async function cargarPorFecha(fecha: string, params: PageParams = {}): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const res = await citasService.getByFecha(fecha, {
        size: 50,
        sort: 'horaInicio',
        dir: 'asc',
        ...params,
      })
      citas.value         = res.content
      page.value          = res.number
      totalElements.value = res.totalElements
      totalPages.value    = res.totalPages
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar citas del día'
    } finally {
      loading.value = false
    }
  }

  // Carga las citas del cliente autenticado (portal cliente)
  async function cargarMisCitas(clienteId: number, params: PageParams = {}): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const res = await citasService.getByClienteId(clienteId, {
        size: 20,
        sort: 'fecha',
        dir: 'desc',
        ...params,
      })
      citas.value         = res.content
      page.value          = res.number
      totalElements.value = res.totalElements
      totalPages.value    = res.totalPages
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar mis citas'
    } finally {
      loading.value = false
    }
  }

  async function irAPagina(numeroPagina: number): Promise<void> {
    await cargar({ page: numeroPagina })
  }

  async function siguientePagina(): Promise<void> {
    if (hayMasPaginas.value) await cargar({ page: page.value + 1 })
  }

  async function paginaAnterior(): Promise<void> {
    if (hayPaginaAnterior.value) await cargar({ page: page.value - 1 })
  }

  function getById(id: number): Cita | undefined {
    return citas.value.find((c) => c.id === id)
  }

  async function crear(data: CitaFormData): Promise<Cita> {
    loading.value = true
    error.value   = null
    try {
      const nueva = await citasService.create(data)
      await cargar()
      return nueva
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al crear cita'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function actualizar(id: number, data: Partial<CitaFormData>): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const actualizada = await citasService.update(id, data)
      const idx = citas.value.findIndex((c) => c.id === id)
      if (idx !== -1) citas.value[idx] = actualizada
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al actualizar cita'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function cambiarEstado(id: number, estado: EstadoCita): Promise<void> {
    error.value = null
    try {
      const actualizada = await citasService.changeStatus(id, estado)
      const idx = citas.value.findIndex((c) => c.id === id)
      if (idx !== -1) citas.value[idx] = actualizada
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cambiar estado'
      throw e
    }
  }

  async function eliminar(id: number): Promise<void> {
    await citasService.delete(id)
    await cargar()
  }

  function limpiarError(): void {
    error.value = null
  }

  async function cargarHoy(medicoId?: number | null): Promise<void> {
    const hoy = new Date().toISOString().split('T')[0]!
    try {
      if (medicoId) {
        // Veterinario: usa su portal propio
        const { api } = await import('@/services/api')
        const res = await api.getPaged<Record<string, unknown>>('/api/portal/medico/citas/fecha', { fecha: hoy, size: 50, sort: 'horaInicio', dir: 'asc' })
        citasHoy.value = res.content.map((raw) => ({
          ...(raw as unknown as Cita),
          tipoCita: {
            id:              raw['tipoCitaId']              as number,
            nombre:          (raw['tipoCitaNombre']          as string) ?? '',
            duracionMinutos: (raw['tipoCitaDuracionMinutos'] as number) ?? 0,
            color:           (raw['tipoCitaColor']           as string) ?? '#059669',
            descripcion:     raw['tipoCitaDescripcion']      as string | undefined,
          },
        }))
      } else {
        // Admin/recepcionista: todas las citas de hoy (ya pasan por citasService con mapper)
        const res = await citasService.getByFecha(hoy, { size: 50, sort: 'horaInicio', dir: 'asc' })
        citasHoy.value = res.content
      }
    } catch {
      citasHoy.value = []
    }
  }

  return {
    citas,
    citasHoy,
    loading,
    error,
    page,
    pageSize,
    totalElements,
    totalPages,
    fechaSeleccionada,
    filtroMedicoId,
    filtroEstado,
    tiposCita,
    medicos,
    citasFiltradas,
    citasPorFecha,
    estadisticas,
    hayMasPaginas,
    hayPaginaAnterior,
    cargar,
    cargarCatalogos,
    cargarPorFecha,
    cargarMisCitas,
    cargarHoy,
    irAPagina,
    siguientePagina,
    paginaAnterior,
    getById,
    crear,
    actualizar,
    cambiarEstado,
    eliminar,
    limpiarError,
  }
})