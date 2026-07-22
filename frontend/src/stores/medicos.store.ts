import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Medico, MedicoFormData, Especialidad } from '@/types'
import type { PageParams } from '@/services/api'
import { medicosService } from '@/services/medicos.service'

export const useMedicosStore = defineStore('medicos', () => {
  // ── Estado ─────────────────────────────────────────────────────────────

  const medicos           = ref<Medico[]>([])
  const especialidadesData = ref<Especialidad[]>([])
  const loading           = ref(false)
  const error             = ref<string | null>(null)

  // Paginación
  const page              = ref(0)
  const pageSize          = ref(20)
  const totalElements     = ref(0)
  const totalPages        = ref(0)

  // Filtros
  const searchQuery       = ref('')

  // ── Computed ────────────────────────────────────────────────────────────

  const especialidades    = computed(() => especialidadesData.value)
  const hayMasPaginas     = computed(() => page.value < totalPages.value - 1)
  const hayPaginaAnterior = computed(() => page.value > 0)

  // ── Acciones ────────────────────────────────────────────────────────────

  async function cargarCatalogos(): Promise<void> {
    especialidadesData.value = await medicosService.getAllEspecialidades()
  }

  async function cargar(params: PageParams = {}): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const p: PageParams = {
        page: params.page ?? page.value,
        size: params.size ?? pageSize.value,
        sort: params.sort ?? 'apellido',
        dir:  params.dir  ?? 'asc',
      }

      const res = searchQuery.value.trim()
        ? await medicosService.buscar(searchQuery.value.trim(), p)
        : await medicosService.getAll(p)

      medicos.value       = res.content
      page.value          = res.number
      pageSize.value      = res.size
      totalElements.value = res.totalElements
      totalPages.value    = res.totalPages
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar médicos'
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

  function getById(id: number): Medico | undefined {
    return medicos.value.find((m) => m.id === id)
  }

  async function crear(data: MedicoFormData): Promise<Medico> {
    loading.value = true
    error.value   = null
    try {
      const nuevo = await medicosService.create(data)
      await cargar()
      return nuevo
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al crear médico'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function actualizar(id: number, data: Partial<MedicoFormData>): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const actualizado = await medicosService.update(id, data)
      const idx = medicos.value.findIndex((m) => m.id === id)
      if (idx !== -1) medicos.value[idx] = actualizado
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al actualizar médico'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function eliminar(id: number): Promise<void> {
    await medicosService.delete(id)
    await cargar()
  }

  function limpiarError(): void {
    error.value = null
  }

  return {
    medicos,
    loading,
    error,
    page,
    pageSize,
    totalElements,
    totalPages,
    searchQuery,
    especialidades,
    hayMasPaginas,
    hayPaginaAnterior,
    cargar,
    cargarCatalogos,
    irAPagina,
    siguientePagina,
    paginaAnterior,
    getById,
    crear,
    actualizar,
    eliminar,
    limpiarError,
  }
})
