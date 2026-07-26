import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UsuarioFormData } from '@/types'
import { usuariosService, type UsuarioListItem } from '@/services/usuarios.service'

export const useUsuariosStore = defineStore('usuarios', () => {
  // ── Estado ─────────────────────────────────────────────────────────────

  const usuarios      = ref<UsuarioListItem[]>([])
  const loading       = ref(false)
  const error         = ref<string | null>(null)

  // Paginación
  const page          = ref(0)
  const pageSize      = ref(15)
  const totalElements = ref(0)
  const totalPages    = ref(0)

  // Filtros — cualquier cambio dispara cargar({ page: 0 })
  const searchQuery   = ref('')
  const filtroRol     = ref<string>('todos')

  // ── Acciones ────────────────────────────────────────────────────────────

  async function cargar(params: { page?: number } = {}): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const res = await usuariosService.getAll({
        page:     params.page ?? page.value,
        size:     pageSize.value,
        busqueda: searchQuery.value.trim() || undefined,
        rol:      filtroRol.value !== 'todos' ? filtroRol.value : undefined,
      })
      usuarios.value      = res.content
      page.value          = res.number
      totalElements.value = res.totalElements
      totalPages.value    = res.totalPages
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar usuarios'
    } finally {
      loading.value = false
    }
  }

  async function irAPagina(numeroPagina: number): Promise<void> {
    await cargar({ page: numeroPagina })
  }

  async function crear(data: UsuarioFormData): Promise<UsuarioListItem> {
    loading.value = true
    error.value   = null
    try {
      const nuevo = await usuariosService.create(data)
      await cargar({ page: 0 })
      return nuevo
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al crear usuario'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function actualizar(id: number, data: Partial<UsuarioFormData>): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const actualizado = await usuariosService.update(id, data)
      const idx = usuarios.value.findIndex((u) => u.id === id)
      if (idx !== -1) usuarios.value[idx] = actualizado
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al actualizar usuario'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function eliminar(id: number): Promise<void> {
    await usuariosService.delete(id)
    // Si borramos el último de la página actual, volver a página anterior
    const paginaDestino = usuarios.value.length === 1 && page.value > 0
      ? page.value - 1
      : page.value
    await cargar({ page: paginaDestino })
  }

  function limpiarError(): void {
    error.value = null
  }

  return {
    usuarios,
    loading,
    error,
    page,
    pageSize,
    totalElements,
    totalPages,
    searchQuery,
    filtroRol,
    cargar,
    irAPagina,
    crear,
    actualizar,
    eliminar,
    limpiarError,
  }
})
