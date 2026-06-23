import type { Cliente, ClienteFormData } from '@/types'
import clientesJson from '@/data/json/clientes.json'

// Estado en memoria — simula la tabla de la BD durante la sesión
let db: Cliente[] = clientesJson.map((c) => ({ ...c } as Cliente))

function nextId(): number {
  return db.length > 0 ? Math.max(...db.map((c) => c.id)) + 1 : 1
}

export const clientesService = {
  async getAll(): Promise<Cliente[]> {
    await new Promise((r) => setTimeout(r, 300))
    return [...db]
  },

  async getById(id: number): Promise<Cliente | undefined> {
    await new Promise((r) => setTimeout(r, 150))
    return db.find((c) => c.id === id)
  },

  async create(data: ClienteFormData): Promise<Cliente> {
    await new Promise((r) => setTimeout(r, 500))
    const nuevo: Cliente = {
      ...data,
      id: nextId(),
      estado: 'activo',
      numeroMascotas: 0,
      createdAt: new Date().toISOString(),
    }
    db.push(nuevo)
    return { ...nuevo }
  },

  async update(id: number, data: Partial<ClienteFormData>): Promise<Cliente> {
    await new Promise((r) => setTimeout(r, 500))
    const idx = db.findIndex((c) => c.id === id)
    if (idx === -1) throw new Error(`Cliente ${id} no encontrado`)
    db[idx] = { ...db[idx]!, ...data }
    return { ...db[idx]! }
  },

  async toggleEstado(id: number): Promise<Cliente> {
    await new Promise((r) => setTimeout(r, 200))
    const cliente = db.find((c) => c.id === id)
    if (!cliente) throw new Error(`Cliente ${id} no encontrado`)
    cliente.estado = cliente.estado === 'activo' ? 'inactivo' : 'activo'
    return { ...cliente }
  },
}
