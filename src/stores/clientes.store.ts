import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Cliente, ClienteFormData } from '@/types'
import { clientesService } from '@/services/clientes.service'
import { authService } from '@/services/auth.service'

export const useClientesStore = defineStore('clientes', () => {
  const clientes = ref<Cliente[]>([])
  const loading = ref(false)
  const searchQuery = ref('')
  const filtroEstado = ref<'todos' | 'activo' | 'inactivo'>('todos')

  // ── computed ──────────────────────────────────────────────────────────────

  const clientesFiltrados = computed(() => {
    let result = clientes.value
    if (filtroEstado.value !== 'todos') {
      result = result.filter((c) => c.estado === filtroEstado.value)
    }
    if (searchQuery.value.trim()) {
      const q = searchQuery.value.toLowerCase()
      result = result.filter(
        (c) =>
          c.nombre.toLowerCase().includes(q) ||
          c.apellido.toLowerCase().includes(q) ||
          c.email.toLowerCase().includes(q) ||
          c.numeroDocumento.includes(q) ||
          c.telefono.includes(q),
      )
    }
    return result
  })

  // ── acciones ──────────────────────────────────────────────────────────────

  async function cargar(): Promise<void> {
    loading.value = true
    try {
      clientes.value = await clientesService.getAll()
    } finally {
      loading.value = false
    }
  }

  function getById(id: number): Cliente | undefined {
    return clientes.value.find((c) => c.id === id)
  }

  async function crear(data: ClienteFormData): Promise<Cliente> {
    loading.value = true
    try {
      const nuevo = await clientesService.create(data)
      clientes.value.push(nuevo)
      // Crear automáticamente el usuario con rol cliente
      authService.crearUsuarioCliente({
        nombre: nuevo.nombre,
        apellido: nuevo.apellido,
        email: nuevo.email,
        clienteId: nuevo.id,
      })
      return nuevo
    } finally {
      loading.value = false
    }
  }

  async function actualizar(id: number, data: Partial<ClienteFormData>): Promise<void> {
    loading.value = true
    try {
      const actualizado = await clientesService.update(id, data)
      const idx = clientes.value.findIndex((c) => c.id === id)
      if (idx !== -1) clientes.value[idx] = actualizado
    } finally {
      loading.value = false
    }
  }

  async function toggleEstado(id: number): Promise<void> {
    const actualizado = await clientesService.toggleEstado(id)
    const idx = clientes.value.findIndex((c) => c.id === id)
    if (idx !== -1) clientes.value[idx] = actualizado
  }

  return {
    clientes,
    loading,
    searchQuery,
    filtroEstado,
    clientesFiltrados,
    cargar,
    getById,
    crear,
    actualizar,
    toggleEstado,
  }
})
