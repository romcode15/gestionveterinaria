import type { Mascota, MascotaFormData, Especie, Raza } from '@/types'
import { api, type SpringPage, type PageParams } from './api'

export const mascotasService = {
  getAll(params: PageParams = {}): Promise<SpringPage<Mascota>> {
    return api.getPaged<Mascota>('/api/mascotas', { size: 10, sort: 'nombre', dir: 'asc', ...params })
  },

  getById(id: number): Promise<Mascota> {
    return api.get<Mascota>(`/api/mascotas/${id}`)
  },

  getByClienteId(clienteId: number, params: PageParams = {}): Promise<SpringPage<Mascota>> {
    return api.getPaged<Mascota>(`/api/mascotas/cliente/${clienteId}`, params)
  },

  buscar(nombre: string, params: PageParams = {}): Promise<SpringPage<Mascota>> {
    return api.getPaged<Mascota>(`/api/mascotas/buscar?nombre=${encodeURIComponent(nombre)}`, params)
  },

  // Catálogos — listas pequeñas, sin paginar
  getAllEspecies(): Promise<Especie[]> {
    return api.get<Especie[]>('/api/catalogos/especies')
  },

  getAllRazas(): Promise<Raza[]> {
    return api.get<Raza[]>('/api/catalogos/razas')
  },

  create(data: MascotaFormData): Promise<Mascota> {
    return api.post<Mascota>('/api/mascotas', data)
  },

  update(id: number, data: Partial<MascotaFormData>): Promise<Mascota> {
    return api.put<Mascota>(`/api/mascotas/${id}`, data)
  },

  delete(id: number): Promise<void> {
    return api.delete(`/api/mascotas/${id}`)
  },
}
