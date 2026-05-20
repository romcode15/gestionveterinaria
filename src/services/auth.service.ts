import type { LoginCredentials, Usuario } from '@/types'
import { mockUsuarios, mockCredentials } from '@/data'

const TOKEN_KEY = 'vet_token'
const USER_KEY = 'vet_user'

function generateToken(userId: number): string {
  return `mock_token_${userId}_${Date.now()}`
}

export const authService = {
  async login(credentials: LoginCredentials): Promise<{ token: string; usuario: Usuario }> {
    // Simular delay de red
    await new Promise((resolve) => setTimeout(resolve, 800))

    const expectedPassword = mockCredentials[credentials.username]
    if (!expectedPassword || expectedPassword !== credentials.password) {
      throw new Error('Usuario o contraseña incorrectos')
    }

    const usuario = mockUsuarios.find((u) => u.username === credentials.username)
    if (!usuario) {
      throw new Error('Usuario no encontrado')
    }

    if (!usuario.activo) {
      throw new Error('Usuario inactivo. Contacte al administrador.')
    }

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
    // En producción se verificaría la expiración del JWT
    return token.startsWith('mock_token_')
  },
}
