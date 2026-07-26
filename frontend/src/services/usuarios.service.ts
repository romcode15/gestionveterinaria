import type { UsuarioFormData } from '@/types'
import { api, type SpringPage, type PageParams } from './api'

export interface UsuarioListItem {
  id: number
  username: string
  email: string
  nombre: string
  apellido: string
  activo: boolean
  rolesNombres: string[]
  ultimoAcceso?: string
  createdAt?: string
  clienteId?: number | null
  medicoId?: number | null
  recepcionistaId?: number | null
}

export const usuariosService = {
  getAll(params: PageParams & { busqueda?: string; rol?: string } = {}): Promise<SpringPage<UsuarioListItem>> {
    return api.getPaged<UsuarioListItem>('/api/usuarios', { size: 15, sort: 'apellido', dir: 'asc', ...params })
  },

  getById(id: number): Promise<UsuarioListItem> {
    return api.get<UsuarioListItem>(`/api/usuarios/${id}`)
  },

  create(data: UsuarioFormData): Promise<UsuarioListItem> {
    return api.post<UsuarioListItem>('/api/usuarios', data)
  },

  update(id: number, data: Partial<UsuarioFormData>): Promise<UsuarioListItem> {
    return api.put<UsuarioListItem>(`/api/usuarios/${id}`, data)
  },

  delete(id: number): Promise<void> {
    return api.delete(`/api/usuarios/${id}`)
  },
}
