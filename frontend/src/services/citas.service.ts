import type { Cita, CitaFormData, EstadoCita, TipoCita } from '@/types'
import { api, type SpringPage, type PageParams } from './api'

export const citasService = {
  getAll(params: PageParams = {}): Promise<SpringPage<Cita>> {
    return api.getPaged<Cita>('/api/citas', { size: 20, sort: 'fecha', dir: 'desc', ...params })
  },

  getById(id: number): Promise<Cita> {
    return api.get<Cita>(`/api/citas/${id}`)
  },

  getByFecha(fecha: string, params: PageParams = {}): Promise<SpringPage<Cita>> {
    return api.getPaged<Cita>(`/api/citas/fecha?fecha=${fecha}`, params)
  },

  getByClienteId(clienteId: number, params: PageParams = {}): Promise<SpringPage<Cita>> {
    return api.getPaged<Cita>(`/api/citas/cliente/${clienteId}`, params)
  },

  getByMedicoId(medicoId: number, params: PageParams = {}): Promise<SpringPage<Cita>> {
    return api.getPaged<Cita>(`/api/citas/medico/${medicoId}`, params)
  },

  getByEstado(estado: EstadoCita, params: PageParams = {}): Promise<SpringPage<Cita>> {
    return api.getPaged<Cita>(`/api/citas/estado/${estado}`, params)
  },

  // Catálogo de tipos de cita — lista pequeña, sin paginar
  getAllTiposCita(): Promise<TipoCita[]> {
    return api.get<TipoCita[]>('/api/catalogos/tipos-cita')
  },

  create(data: CitaFormData): Promise<Cita> {
    return api.post<Cita>('/api/citas', data)
  },

  update(id: number, data: Partial<CitaFormData>): Promise<Cita> {
    return api.put<Cita>(`/api/citas/${id}`, data)
  },

  changeStatus(id: number, estado: EstadoCita): Promise<Cita> {
    return api.patch<Cita>(`/api/citas/${id}/estado`, { estado })
  },

  delete(id: number): Promise<void> {
    return api.delete(`/api/citas/${id}`)
  },
}
