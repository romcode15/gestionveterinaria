export * from './auth.types'
export * from './persona.types'
export * from './mascota.types'
export * from './cita.types'

// Estructura Page<T> de Spring Data — usada en todos los listados paginados
export interface SpringPage<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number    // página actual (0-based)
  first: boolean
  last: boolean
  empty: boolean
}

// Alias mantenido por compatibilidad con código existente
export interface PaginatedResponse<T> {
  data: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

export interface ApiResponse<T> {
  success: boolean
  data: T
  message?: string
}

export type SortDirection = 'asc' | 'desc'

export interface TableColumn<T = unknown> {
  key: string
  label: string
  sortable?: boolean
  width?: string
  align?: 'left' | 'center' | 'right'
  render?: (row: T) => string
}