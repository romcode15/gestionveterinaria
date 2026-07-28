import type { Mascota, MascotaFormData, Especie, Raza } from '@/types'
import { api, type SpringPage, type PageParams } from './api'

// Reconstruye los objetos anidados especie y raza desde los campos planos del DTO
function mapMascota(raw: Record<string, unknown>): Mascota {
  const especie: Especie = {
    id:     raw['especieId']     as number,
    nombre: (raw['especieNombre'] as string) ?? '',
  }
  const raza: Raza = {
    id:       raw['razaId']      as number,
    nombre:   (raw['razaNombre'] as string) ?? '',
    especieId: raw['especieId']  as number,
  }
  return { ...(raw as unknown as Mascota), especie, raza }
}

function mapPage(page: SpringPage<Record<string, unknown>>): SpringPage<Mascota> {
  return { ...page, content: page.content.map(mapMascota) }
}

export const mascotasService = {
  getAll(params: PageParams & { busqueda?: string; especieId?: number; estado?: string } = {}): Promise<SpringPage<Mascota>> {
    return api.getPaged<Record<string, unknown>>('/api/mascotas', { size: 10, sort: 'nombre', dir: 'asc', ...params }).then(mapPage)
  },

  getById(id: number): Promise<Mascota> {
    return api.get<Record<string, unknown>>(`/api/mascotas/${id}`).then(mapMascota)
  },

  getByClienteId(clienteId: number, params: PageParams = {}): Promise<SpringPage<Mascota>> {
    return api.getPaged<Record<string, unknown>>(`/api/mascotas/cliente/${clienteId}`, params).then(mapPage)
  },

  buscar(nombre: string, params: PageParams = {}): Promise<SpringPage<Mascota>> {
    return api.getPaged<Record<string, unknown>>(`/api/mascotas/buscar?nombre=${encodeURIComponent(nombre)}`, params).then(mapPage)
  },

  // Catálogos — listas pequeñas, sin paginar
  getAllEspecies(): Promise<Especie[]> {
    return api.get<Especie[]>('/api/catalogos/especies')
  },

  getAllRazas(): Promise<Raza[]> {
    return api.get<Raza[]>('/api/catalogos/razas')
  },

  create(data: MascotaFormData): Promise<Mascota> {
    return api.post<Record<string, unknown>>('/api/mascotas', data).then(mapMascota)
  },

  update(id: number, data: Partial<MascotaFormData>): Promise<Mascota> {
    return api.put<Record<string, unknown>>(`/api/mascotas/${id}`, data).then(mapMascota)
  },

  delete(id: number): Promise<void> {
    return api.delete(`/api/mascotas/${id}`)
  },
}
