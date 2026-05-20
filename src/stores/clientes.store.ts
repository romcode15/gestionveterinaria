import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Cliente, ClienteFormData } from '@/types'
import { mockClientes } from '@/data'

export const useClientesStore = defineStore('clientes', () => {
  const clientes = ref<Cliente[]>([...mockClientes])
  const loading = ref(false)
  const searchQuery = ref('')
  const filtroEstado = ref<'todos' | 'activo' | 'inactivo'>('todos')

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

  function getById(id: number): Cliente | undefined {
    return clientes.value.find((c) => c.id === id)
  }

  async function crear(data: ClienteFormData): Promise<Cliente> {
    loading.value = true
    await new Promise((r) => setTimeout(r, 500))
    const nuevo: Cliente = {
      ...data,
      id: Math.max(...clientes.value.map((c) => c.id)) + 1,
      estado: 'activo',
      numeroMascotas: 0,
      createdAt: new Date().toISOString(),
    }
    clientes.value.push(nuevo)
    loading.value = false
    return nuevo
  }

  async function actualizar(id: number, data: Partial<ClienteFormData>): Promise<void> {
    loading.value = true
    await new Promise((r) => setTimeout(r, 500))
    const idx = clientes.value.findIndex((c) => c.id === id)
    if (idx !== -1) {
      clientes.value[idx] = { ...clientes.value[idx]!, ...data }
    }
    loading.value = false
  }

  async function toggleEstado(id: number): Promise<void> {
    const cliente = clientes.value.find((c) => c.id === id)
    if (cliente) {
      cliente.estado = cliente.estado === 'activo' ? 'inactivo' : 'activo'
    }
  }

  return {
    clientes,
    loading,
    searchQuery,
    filtroEstado,
    clientesFiltrados,
    getById,
    crear,
    actualizar,
    toggleEstado,
  }
})
