/**
 * Cliente HTTP centralizado para el backend Spring Boot.
 * - Agrega el token JWT en cada petición automáticamente.
 * - Maneja errores de red y respuestas no OK de forma uniforme.
 * - Soporta la estructura Page<T> de Spring Data.
 */

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

const TOKEN_KEY = 'vet_token'

// ── Estructura Page<T> que devuelve Spring Data ───────────────────────────

export interface SpringPage<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number       // página actual (0-based)
  first: boolean
  last: boolean
  empty: boolean
}

// ── Parámetros de paginación ──────────────────────────────────────────────

export interface PageParams {
  page?: number
  size?: number
  sort?: string
  dir?: 'asc' | 'desc'
  // Parámetros extra de filtro (ej: fecha, estado, medicoId…)
  [key: string]: string | number | boolean | undefined
}

// ── Error tipado de la API ────────────────────────────────────────────────

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    public readonly mensaje: string,
    public readonly campos?: Record<string, string>,
  ) {
    super(mensaje)
    this.name = 'ApiError'
  }
}

// ── Helper: construir query string desde un objeto ────────────────────────

function buildQuery(params: Record<string, string | number | boolean | undefined>): string {
  const entries = Object.entries(params).filter(([, v]) => v !== undefined && v !== null)
  if (entries.length === 0) return ''
  return '?' + entries.map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`).join('&')
}

// ── Helper: obtener token del localStorage ────────────────────────────────

function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

// ── Helper: construir headers con JWT ─────────────────────────────────────

function buildHeaders(extra?: Record<string, string>): Headers {
  const headers = new Headers({ 'Content-Type': 'application/json', ...extra })
  const token = getToken()
  if (token) headers.set('Authorization', `Bearer ${token}`)
  return headers
}

// ── Helper: parsear respuesta de error del backend ────────────────────────

async function parseError(response: Response): Promise<ApiError> {
  try {
    const body = await response.json()
    // Errores de validación: { campos: { field: msg } }
    if (body.campos) {
      return new ApiError(response.status, body.error ?? 'Validación fallida', body.campos)
    }
    return new ApiError(response.status, body.mensaje ?? body.error ?? `Error ${response.status}`)
  } catch {
    return new ApiError(response.status, `Error ${response.status}: ${response.statusText}`)
  }
}

// ── Método base ───────────────────────────────────────────────────────────

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const url = `${BASE_URL}${path}`
  const headers = buildHeaders()

  const response = await fetch(url, { ...options, headers })

  // 204 No Content (DELETE exitoso)
  if (response.status === 204) return undefined as unknown as T

  // 401/403 — token expirado o inválido: limpiar sesión y redirigir al login
  if (response.status === 401 || response.status === 403) {
    const isLoginPage = window.location.pathname === '/login'
    if (!isLoginPage) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem('vet_user')
      window.location.href = '/login'
    }
    throw new ApiError(response.status, 'Sesión expirada. Por favor inicia sesión nuevamente.')
  }

  if (!response.ok) {
    throw await parseError(response)
  }

  return response.json() as Promise<T>
}

// ── API pública ───────────────────────────────────────────────────────────

export const api = {
  /**
   * GET sin paginación (para catálogos pequeños: especies, razas, tipos de cita, etc.)
   */
  get<T>(path: string): Promise<T> {
    return request<T>(path, { method: 'GET' })
  },

  /**
   * GET paginado — devuelve SpringPage<T>
   * Acepta page/size/sort/dir y cualquier parámetro extra de filtro (fecha, estado, etc.)
   * Ejemplo: api.getPaged('/api/citas/fecha', { fecha: '2026-07-23', page: 0, size: 20 })
   */
  getPaged<T>(path: string, params: PageParams = {}): Promise<SpringPage<T>> {
    const { page, size, sort, dir, ...extras } = params
    const query = buildQuery({
      page: page ?? 0,
      size: size ?? 20,
      sort,
      dir,
      ...extras,
    })
    return request<SpringPage<T>>(`${path}${query}`, { method: 'GET' })
  },

  /**
   * POST — crea un recurso, devuelve el recurso creado
   */
  post<T>(path: string, body: unknown): Promise<T> {
    return request<T>(path, {
      method: 'POST',
      body: JSON.stringify(body),
    })
  },

  /**
   * PUT — actualiza un recurso completo, devuelve el recurso actualizado
   */
  put<T>(path: string, body: unknown): Promise<T> {
    return request<T>(path, {
      method: 'PUT',
      body: JSON.stringify(body),
    })
  },

  /**
   * PATCH — actualiza parcialmente un recurso
   */
  patch<T>(path: string, body: unknown): Promise<T> {
    return request<T>(path, {
      method: 'PATCH',
      body: JSON.stringify(body),
    })
  },

  /**
   * DELETE — borra o desactiva un recurso (devuelve void en 204)
   */
  delete(path: string): Promise<void> {
    return request<void>(path, { method: 'DELETE' })
  },

  /**
   * POST para login — sin token en el header (ruta pública)
   */
  postPublic<T>(path: string, body: unknown): Promise<T> {
    return fetch(`${BASE_URL}${path}`, {
      method: 'POST',
      headers: new Headers({ 'Content-Type': 'application/json' }),
      body: JSON.stringify(body),
    }).then(async (res) => {
      if (!res.ok) throw await parseError(res)
      return res.json() as Promise<T>
    })
  },
}