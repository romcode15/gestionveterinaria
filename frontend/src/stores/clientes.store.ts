import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Cliente, ClienteFormData } from '@/types'
import type { PageParams } from '@/services/api'
import { clientesService } from '@/services/clientes.service'
import { api } from '@/services/api'

export const useClientesStore = defineStore('clientes', () => {
  // ── Estado ─────────────────────────────────────────────────────────────

  const clientes     = ref<Cliente[]>([])
  const loading      = ref(false)
  const error        = ref<string | null>(null)

  // Paginación
  const page         = ref(0)
  const pageSize     = ref(10)
  const totalElements = ref(0)
  const totalPages   = ref(0)

  // Filtros
  const searchQuery  = ref('')
  const filtroEstado = ref<'todos' | 'activo' | 'inactivo'>('todos')

  // Si está activo, el store usa el portal médico en lugar del endpoint general
  const medicoId     = ref<number | null>(null)

  // ── Computed ────────────────────────────────────────────────────────────

  const hayMasPaginas = computed(() => page.value < totalPages.value - 1)
  const hayPaginaAnterior = computed(() => page.value > 0)

  // ── Acciones ────────────────────────────────────────────────────────────

  async function cargar(params: PageParams = {}): Promise<void> {
    loading.value = true
    error.value = null
    try {
      const p: PageParams = {
        page: params.page ?? page.value,
        size: params.size ?? pageSize.value,
        sort: params.sort ?? 'apellido',
        dir:  params.dir  ?? 'asc',
      }

      let res
      if (medicoId.value) {
        // Veterinario: portal médico (filtro por búsqueda de texto solamente)
        const portalParams: PageParams = { ...p }
        if (searchQuery.value.trim()) portalParams.busqueda = searchQuery.value.trim()
        try {
          res = await api.getPaged<Cliente>('/api/portal/medico/clientes', portalParams)
        } catch {
          clientes.value = []; totalElements.value = 0; totalPages.value = 0; loading.value = false; return
        }
      } else {
        // Admin / recepcionista: endpoint combinado — todos los filtros en un solo request
        res = await clientesService.getAll({
          ...p,
          busqueda: searchQuery.value.trim() || undefined,
          estado:   filtroEstado.value !== 'todos' ? filtroEstado.value : undefined,
        })
      }

      clientes.value      = res.content
      page.value          = res.number
      pageSize.value      = res.size
      totalElements.value = res.totalElements
      totalPages.value    = res.totalPages
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar clientes'
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

  function getById(id: number): Cliente | undefined {
    return clientes.value.find((c) => c.id === id)
  }

  async function crear(data: ClienteFormData): Promise<Cliente> {
    loading.value = true
    error.value = null
    try {
      const nuevo = await clientesService.create(data)
      // Recargar la página actual para mantener consistencia con el backend
      await cargar()
      return nuevo
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al crear cliente'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function actualizar(id: number, data: Partial<ClienteFormData>): Promise<void> {
    loading.value = true
    error.value = null
    try {
      const actualizado = await clientesService.update(id, data)
      const idx = clientes.value.findIndex((c) => c.id === id)
      if (idx !== -1) clientes.value[idx] = actualizado
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al actualizar cliente'
      throw e
    } finally {
      loading.value = false
    }
  }

  // Borrado lógico — cambia estado a inactivo
  async function eliminar(id: number): Promise<void> {
    await clientesService.delete(id)
    await cargar()
  }

  function limpiarError(): void {
    error.value = null
  }

  return {
    clientes,
    loading,
    error,
    page,
    pageSize,
    totalElements,
    totalPages,
    searchQuery,
    filtroEstado,
    medicoId,
    hayMasPaginas,
    hayPaginaAnterior,
    cargar,
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