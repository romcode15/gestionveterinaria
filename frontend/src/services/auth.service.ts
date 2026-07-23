import type { Usuario, RoleName } from '@/types'
import { api } from './api'

const TOKEN_KEY = 'vet_token'
const USER_KEY  = 'vet_user'

// ── Nueva interfaz que coincide con la respuesta del backend (con permisos) ──
interface LoginResponse {
  token: string
  tipo: string
  id: number
  username: string
  email: string
  nombre: string
  apellido: string
  clienteId: number | null
  medicoId: number | null
  recepcionistaId: number | null
  roles: Array<{
    id: number
    nombre: string
    descripcion: string
    permisos: Array<{
      id: number
      nombre: string
      descripcion: string
      modulo: string
    }>
  }>
}

export const authService = {
  async login(credentials: { username: string; password: string }): Promise<{ token: string; usuario: Usuario }> {
    const res = await api.postPublic<LoginResponse>('/api/auth/login', credentials)

    // Mapeo correcto: roles vienen con permisos desde el backend
    const usuario: Usuario = {
      id: res.id,
      username: res.username,
      email: res.email,
      nombre: res.nombre,
      apellido: res.apellido,
      activo: true,
      clienteId: res.clienteId ?? null,
      medicoId:  res.medicoId  ?? null,
      roles: res.roles.map((rol) => ({
        id: rol.id,
        nombre: rol.nombre as RoleName,
        descripcion: rol.descripcion,
        permisos: rol.permisos.map((p) => ({
          id: p.id,
          nombre: p.nombre,
          descripcion: p.descripcion,
          modulo: p.modulo,
        })),
      })),
    }

    localStorage.setItem(TOKEN_KEY, res.token)
    localStorage.setItem(USER_KEY, JSON.stringify(usuario))

    return { token: res.token, usuario }
  },

  logout(): void {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  },

  getStoredToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
  },

  getStoredUser(): Usuario | null {
    const raw = localStorage.getItem(USER_KEY)
    if (!raw) return null
    try {
      return JSON.parse(raw) as Usuario
    } catch {
      return null
    }
  },

  isTokenValid(token: string): boolean {
    return token.length > 0
  },

  async cambiarPassword(passwordActual: string, passwordNueva: string): Promise<void> {
    const { api } = await import('./api')
    return api.patch<void>('/api/auth/cambiar-password', { passwordActual, passwordNueva })
  },
}