import type { Usuario, Rol, Permiso } from '@/types'
import usuariosJson from '@/data/json/usuarios.json'
import rolesJson from '@/data/json/roles.json'
import permisosJson from '@/data/json/permisos.json'

const TOKEN_KEY = 'vet_token'
const USER_KEY = 'vet_user'

// ── helpers de resolución (simulan JOINs) ──────────────────────────────────

function resolvePermisos(permisosIds: number[]): Permiso[] {
  return permisosJson.filter((p) => permisosIds.includes(p.id)) as Permiso[]
}

function resolveRoles(rolesIds: number[]): Rol[] {
  return rolesJson
    .filter((r) => rolesIds.includes(r.id))
    .map((r) => ({
      id: r.id,
      nombre: r.nombre as Rol['nombre'],
      descripcion: r.descripcion,
      permisos: resolvePermisos(r.permisosIds),
    }))
}

function resolveUsuario(raw: (typeof usuariosJson)[number]): Usuario {
  return {
    id: raw.id,
    username: raw.username,
    email: raw.email,
    nombre: raw.nombre,
    apellido: raw.apellido,
    activo: raw.activo,
    ultimoAcceso: raw.ultimoAcceso,
    clienteId: raw.clienteId ?? null,
    roles: resolveRoles(raw.rolesIds),
  }
}

// ── utilidades de token ────────────────────────────────────────────────────

function generateToken(userId: number): string {
  return `mock_token_${userId}_${Date.now()}`
}

// ── API pública del servicio ───────────────────────────────────────────────

export const authService = {
  async login(credentials: { username: string; password: string }): Promise<{ token: string; usuario: Usuario }> {
    await new Promise((r) => setTimeout(r, 600)) // simula latencia

    const rawUser = usuariosJson.find((u) => u.username === credentials.username)

    if (!rawUser || rawUser.password !== credentials.password) {
      throw new Error('Usuario o contraseña incorrectos')
    }
    if (!rawUser.activo) {
      throw new Error('Usuario inactivo. Contacte al administrador.')
    }

    const usuario = resolveUsuario(rawUser)
    const token = generateToken(usuario.id)

    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(USER_KEY, JSON.stringify(usuario))

    return { token, usuario }
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
    return token.startsWith('mock_token_')
  },

  /**
   * Crea automáticamente un usuario con rol cliente vinculado a un clienteId.
   * Simula la transacción backend: INSERT usuario + asignar rol cliente.
   */
  crearUsuarioCliente(datos: {
    nombre: string
    apellido: string
    email: string
    clienteId: number
  }): void {
    const raw = usuariosJson as Array<{
      id: number
      username: string
      password: string
      email: string
      nombre: string
      apellido: string
      activo: boolean
      rolesIds: number[]
      clienteId: number | null
      ultimoAcceso: string
    }>

    const nextId = raw.length > 0 ? Math.max(...raw.map((u) => u.id)) + 1 : 1
    const username = datos.email.split('@')[0]?.toLowerCase().replace(/[^a-z0-9.]/g, '') ?? `cliente${nextId}`

    raw.push({
      id: nextId,
      username,
      password: 'cli123',
      email: datos.email,
      nombre: datos.nombre,
      apellido: datos.apellido,
      activo: true,
      rolesIds: [4], // rol cliente
      clienteId: datos.clienteId,
      ultimoAcceso: new Date().toISOString(),
    })
  },
}
