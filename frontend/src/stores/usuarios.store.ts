import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UsuarioFormData } from '@/types'
import { usuariosService, type UsuarioListItem } from '@/services/usuarios.service'

export const useUsuariosStore = defineStore('usuarios', () => {
  // ── Estado ─────────────────────────────────────────────────────────────

  const usuarios    = ref<UsuarioListItem[]>([])
  const loading     = ref(false)
  const error       = ref<string | null>(null)

  // Filtros (locales, sin paginación porque la API devuelve lista simple)
  const searchQuery  = ref('')
  const filtroRol    = ref<string>('todos')

  // ── Computed ────────────────────────────────────────────────────────────

  const usuariosFiltrados = computed(() => {
    let lista = usuarios.value

    if (filtroRol.value !== 'todos') {
      lista = lista.filter((u) =>
        u.rolesNombres.some((r) => r.toLowerCase() === filtroRol.value),
      )
    }

    if (searchQuery.value.trim()) {
      const q = searchQuery.value.trim().toLowerCase()
      lista = lista.filter(
        (u) =>
          u.nombre.toLowerCase().includes(q) ||
          u.apellido.toLowerCase().includes(q) ||
          u.username.toLowerCase().includes(q) ||
          u.email.toLowerCase().includes(q),
      )
    }

    return lista
  })

  // ── Acciones ────────────────────────────────────────────────────────────

  async function cargar(): Promise<void> {
    loading.value = true
    error.value = null
    try {
      usuarios.value = await usuariosService.getAll()
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar usuarios'
    } finally {
      loading.value = false
    }
  }

  async function crear(data: UsuarioFormData): Promise<UsuarioListItem> {
    loading.value = true
    error.value = null
    try {
      const nuevo = await usuariosService.create(data)
      usuarios.value.push(nuevo)
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
    error.value = null
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
    usuarios.value = usuarios.value.filter((u) => u.id !== id)
  }

  function limpiarError(): void {
    error.value = null
  }

  return {
    usuarios,
    loading,
    error,
    searchQuery,
    filtroRol,
    usuariosFiltrados,
    cargar,
    crear,
    actualizar,
    eliminar,
    limpiarError,
  }
})
