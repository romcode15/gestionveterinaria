import type { Usuario } from '@/types'
import { api } from './api'

const TOKEN_KEY = 'vet_token'
const USER_KEY  = 'vet_user'

interface LoginResponse {
  token: string
  tipo: string
  usuario: {
    id: number
    username: string
    email: string
    nombre: string
    apellido: string
    activo: boolean
    clienteId: number | null
    medicoId: number | null
    roles: Array<{
      id: number
      nombre: string
      descripcion: string
      permisos: Array<{ id: number; nombre: string; descripcion: string; modulo: string }>
    }>
  }
}

export const authService = {
  async login(credentials: { username: string; password: string }): Promise<{ token: string; usuario: Usuario }> {
    const res = await api.postPublic<LoginResponse>('/api/auth/login', credentials)

    const usuario: Usuario = {
      id:           res.usuario.id,
      username:     res.usuario.username,
      email:        res.usuario.email,
      nombre:       res.usuario.nombre,
      apellido:     res.usuario.apellido,
      activo:       res.usuario.activo,
      clienteId:    res.usuario.clienteId,
      medicoId:     res.usuario.medicoId,
      ultimoAcceso: new Date().toISOString(),
      roles: res.usuario.roles.map((r) => ({
        id:          r.id,
        nombre:      r.nombre as Usuario['roles'][number]['nombre'],
        descripcion: r.descripcion,
        permisos:    r.permisos,
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

  // El token JWT es válido si existe y no está vacío.
  // La expiración real la controla el backend (86400000ms = 24h).
  isTokenValid(token: string): boolean {
    return token.length > 0
  },
}
