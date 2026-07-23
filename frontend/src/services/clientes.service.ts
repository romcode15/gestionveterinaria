import type { Cliente, ClienteFormData } from '@/types'
import { api, type SpringPage, type PageParams } from './api'

export const clientesService = {
  getAll(params: PageParams = {}): Promise<SpringPage<Cliente>> {
    return api.getPaged<Cliente>('/api/clientes', { size: 10, sort: 'apellido', dir: 'asc', ...params })
  },

  buscar(nombre: string, params: PageParams = {}): Promise<SpringPage<Cliente>> {
    return api.getPaged<Cliente>(`/api/clientes/buscar?nombre=${encodeURIComponent(nombre)}`, params)
  },

  porEstado(estado: 'activo' | 'inactivo', params: PageParams = {}): Promise<SpringPage<Cliente>> {
    return api.getPaged<Cliente>(`/api/clientes/estado/${estado}`, params)
  },

  getById(id: number): Promise<Cliente> {
    return api.get<Cliente>(`/api/clientes/${id}`)
  },

  create(data: ClienteFormData): Promise<Cliente> {
    return api.post<Cliente>('/api/clientes', data)
  },

  update(id: number, data: Partial<ClienteFormData>): Promise<Cliente> {
    return api.put<Cliente>(`/api/clientes/${id}`, data)
  },

  // Borrado lógico — cambia estado a 'inactivo'
  delete(id: number): Promise<void> {
    return api.delete(`/api/clientes/${id}`)
  },
}
