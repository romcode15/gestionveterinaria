import type { Cliente, ClienteFormData } from '@/types'
import { api, type SpringPage, type PageParams } from './api'

export const clientesService = {
  /**
   * Endpoint combinado — todos los filtros son opcionales y se aplican simultáneamente.
   * busqueda y estado pueden venir juntos, separados o ninguno.
   */
  getAll(params: PageParams & { busqueda?: string; estado?: string } = {}): Promise<SpringPage<Cliente>> {
    return api.getPaged<Cliente>('/api/clientes', { size: 10, sort: 'apellido', dir: 'asc', ...params })
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

  delete(id: number): Promise<void> {
    return api.delete(`/api/clientes/${id}`)
  },
}
