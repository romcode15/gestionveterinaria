import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Recepcionista, RecepcionistaFormData } from '@/types'
import type { PageParams } from '@/services/api'
import { recepcionistasService } from '@/services/recepcionistas.service'

export const useRecepcionistasStore = defineStore('recepcionistas', () => {
  const recepcionistas  = ref<Recepcionista[]>([])
  const loading         = ref(false)
  const error           = ref<string | null>(null)

  const page            = ref(0)
  const pageSize        = ref(10)
  const totalElements   = ref(0)
  const totalPages      = ref(0)
  const searchQuery     = ref('')

  const hayMasPaginas     = computed(() => page.value < totalPages.value - 1)
  const hayPaginaAnterior = computed(() => page.value > 0)

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
        ? await recepcionistasService.buscar(searchQuery.value.trim(), p)
        : await recepcionistasService.getAll(p)

      recepcionistas.value  = res.content
      page.value            = res.number
      pageSize.value        = res.size
      totalElements.value   = res.totalElements
      totalPages.value      = res.totalPages
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar recepcionistas'
    } finally {
      loading.value = false
    }
  }

  async function irAPagina(n: number): Promise<void> { await cargar({ page: n }) }

  function getById(id: number): Recepcionista | undefined {
    return recepcionistas.value.find((r) => r.id === id)
  }

  async function crear(data: RecepcionistaFormData): Promise<Recepcionista> {
    loading.value = true
    error.value   = null
    try {
      const nuevo = await recepcionistasService.create(data)
      await cargar()
      return nuevo
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al crear recepcionista'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function actualizar(id: number, data: Partial<RecepcionistaFormData>): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const actualizado = await recepcionistasService.update(id, data)
      const idx = recepcionistas.value.findIndex((r) => r.id === id)
      if (idx !== -1) recepcionistas.value[idx] = actualizado
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al actualizar recepcionista'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function eliminar(id: number): Promise<void> {
    await recepcionistasService.delete(id)
    await cargar()
  }

  function limpiarError(): void { error.value = null }

  return {
    recepcionistas, loading, error,
    page, pageSize, totalElements, totalPages,
    searchQuery, hayMasPaginas, hayPaginaAnterior,
    cargar, irAPagina, getById, crear, actualizar, eliminar, limpiarError,
  }
})
