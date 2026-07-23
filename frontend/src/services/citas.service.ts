import type { Cita, CitaFormData, EstadoCita, TipoCita } from '@/types'
import { api, type SpringPage, type PageParams } from './api'

// Reconstruye el objeto anidado tipoCita a partir de los campos planos del DTO
function mapCita(raw: Record<string, unknown>): Cita {
  const tipoCita: TipoCita = {
    id:              raw['tipoCitaId']              as number,
    nombre:          (raw['tipoCitaNombre']          as string) ?? '',
    duracionMinutos: (raw['tipoCitaDuracionMinutos'] as number) ?? 0,
    color:           (raw['tipoCitaColor']           as string) ?? '#6366f1',
    descripcion:     raw['tipoCitaDescripcion']      as string | undefined,
  }
  return { ...(raw as unknown as Cita), tipoCita }
}

function mapPage(page: SpringPage<Record<string, unknown>>): SpringPage<Cita> {
  return { ...page, content: page.content.map(mapCita) }
}

export const citasService = {
  getAll(params: PageParams = {}): Promise<SpringPage<Cita>> {
    return api.getPaged<Record<string, unknown>>('/api/citas', { size: 10, sort: 'fecha', dir: 'desc', ...params }).then(mapPage)
  },

  getById(id: number): Promise<Cita> {
    return api.get<Record<string, unknown>>(`/api/citas/${id}`).then(mapCita)
  },

  getByFecha(fecha: string, params: PageParams = {}): Promise<SpringPage<Cita>> {
    return api.getPaged<Record<string, unknown>>('/api/citas/fecha', { fecha, ...params }).then(mapPage)
  },

  getByClienteId(clienteId: number, params: PageParams = {}): Promise<SpringPage<Cita>> {
    return api.getPaged<Record<string, unknown>>(`/api/citas/cliente/${clienteId}`, params).then(mapPage)
  },

  getByMedicoId(medicoId: number, params: PageParams = {}): Promise<SpringPage<Cita>> {
    return api.getPaged<Record<string, unknown>>(`/api/citas/medico/${medicoId}`, params).then(mapPage)
  },

  getByEstado(estado: EstadoCita, params: PageParams = {}): Promise<SpringPage<Cita>> {
    return api.getPaged<Record<string, unknown>>(`/api/citas/estado/${estado}`, params).then(mapPage)
  },

  // Catálogo de tipos de cita — lista pequeña, sin paginar
  getAllTiposCita(): Promise<TipoCita[]> {
    return api.get<TipoCita[]>('/api/catalogos/tipos-cita')
  },

  create(data: CitaFormData): Promise<Cita> {
    return api.post<Record<string, unknown>>('/api/citas', data).then(mapCita)
  },

  update(id: number, data: Partial<CitaFormData>): Promise<Cita> {
    return api.put<Record<string, unknown>>(`/api/citas/${id}`, data).then(mapCita)
  },

  changeStatus(id: number, estado: EstadoCita): Promise<Cita> {
    return api.patch<Record<string, unknown>>(`/api/citas/${id}/estado`, { estado }).then(mapCita)
  },

  delete(id: number): Promise<void> {
    return api.delete(`/api/citas/${id}`)
  },
}
