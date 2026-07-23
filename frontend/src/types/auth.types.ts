export type RoleName = 'admin' | 'veterinario' | 'recepcionista' | 'auxiliar' | 'cliente'

export interface Permiso {
  id: number
  nombre: string
  descripcion: string
  modulo: string
}

export interface Rol {
  id: number
  nombre: RoleName
  descripcion: string
  permisos: Permiso[]
}

export interface Usuario {
  id: number
  username: string
  email: string
  nombre: string
  apellido: string
  activo: boolean
  roles: Rol[]
  clienteId: number | null
  medicoId: number | null   // vinculación con tabla medicos (rol VETERINARIO)
  avatar?: string
  ultimoAcceso?: string
}

export interface LoginCredentials {
  username: string
  password: string
}

export interface AuthState {
  usuario: Usuario | null
  token: string | null
  isAuthenticated: boolean
  loading: boolean
  error: string | null
}

export interface UsuarioFormData {
  username: string
  password?: string
  email: string
  nombre: string
  apellido: string
  activo?: boolean
  rolesIds: number[]
}
