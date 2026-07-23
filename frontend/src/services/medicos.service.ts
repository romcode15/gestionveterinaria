import type { Medico, MedicoFormData, Especialidad } from '@/types'
import { api, type SpringPage, type PageParams } from './api'

export const medicosService = {
  getAll(params: PageParams = {}): Promise<SpringPage<Medico>> {
    return api.getPaged<Medico>('/api/medicos', { size: 10, sort: 'apellido', dir: 'asc', ...params })
  },

  getById(id: number): Promise<Medico> {
    return api.get<Medico>(`/api/medicos/${id}`)
  },

  getDisponibles(params: PageParams = {}): Promise<SpringPage<Medico>> {
    return api.getPaged<Medico>('/api/medicos/disponibles', params)
  },

  buscar(nombre: string, params: PageParams = {}): Promise<SpringPage<Medico>> {
    return api.getPaged<Medico>(`/api/medicos/buscar?nombre=${encodeURIComponent(nombre)}`, params)
  },

  // Catálogo de especialidades — lista pequeña, sin paginar
  getAllEspecialidades(): Promise<Especialidad[]> {
    return api.get<Especialidad[]>('/api/catalogos/especialidades')
  },

  create(data: MedicoFormData): Promise<Medico> {
    return api.post<Medico>('/api/medicos', data)
  },

  update(id: number, data: Partial<MedicoFormData>): Promise<Medico> {
    return api.put<Medico>(`/api/medicos/${id}`, data)
  },

  delete(id: number): Promise<void> {
    return api.delete(`/api/medicos/${id}`)
  },
}
