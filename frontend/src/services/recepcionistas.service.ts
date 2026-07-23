import type { Recepcionista, RecepcionistaFormData } from '@/types'
import { api, type SpringPage, type PageParams } from './api'

export const recepcionistasService = {
  getAll(params: PageParams = {}): Promise<SpringPage<Recepcionista>> {
    return api.getPaged<Recepcionista>('/api/recepcionistas', {
      size: 10, sort: 'apellido', dir: 'asc', ...params,
    })
  },

  getById(id: number): Promise<Recepcionista> {
    return api.get<Recepcionista>(`/api/recepcionistas/${id}`)
  },

  buscar(nombre: string, params: PageParams = {}): Promise<SpringPage<Recepcionista>> {
    return api.getPaged<Recepcionista>(
      `/api/recepcionistas/buscar?nombre=${encodeURIComponent(nombre)}`, params,
    )
  },

  create(data: RecepcionistaFormData): Promise<Recepcionista> {
    return api.post<Recepcionista>('/api/recepcionistas', data)
  },

  update(id: number, data: Partial<RecepcionistaFormData>): Promise<Recepcionista> {
    return api.put<Recepcionista>(`/api/recepcionistas/${id}`, data)
  },

  delete(id: number): Promise<void> {
    return api.delete(`/api/recepcionistas/${id}`)
  },
}
