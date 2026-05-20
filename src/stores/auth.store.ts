import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Usuario, LoginCredentials, RoleName } from '@/types'
import { authService } from '@/services/auth.service'

export const useAuthStore = defineStore('auth', () => {
  const usuario = ref<Usuario | null>(null)
  const token = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value && !!usuario.value)

  const nombreCompleto = computed(() =>
    usuario.value ? `${usuario.value.nombre} ${usuario.value.apellido}` : '',
  )

  const roles = computed<RoleName[]>(() => usuario.value?.roles.map((r) => r.nombre) ?? [])

  const permisos = computed<string[]>(() => {
    if (!usuario.value) return []
    const set = new Set<string>()
    for (const rol of usuario.value.roles) {
      for (const permiso of rol.permisos) {
        set.add(permiso.nombre)
      }
    }
    return Array.from(set)
  })

  function hasRole(roleName: RoleName): boolean {
    return roles.value.includes(roleName)
  }

  function hasPermiso(permisoNombre: string): boolean {
    return permisos.value.includes(permisoNombre)
  }

  function isAdmin(): boolean {
    return hasRole('admin')
  }

  async function login(credentials: LoginCredentials): Promise<void> {
    loading.value = true
    error.value = null
    try {
      const result = await authService.login(credentials)
      token.value = result.token
      usuario.value = result.usuario
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Error al iniciar sesión'
      throw err
    } finally {
      loading.value = false
    }
  }

  function logout(): void {
    authService.logout()
    token.value = null
    usuario.value = null
    error.value = null
  }

  function initFromStorage(): void {
    const storedToken = authService.getStoredToken()
    const storedUser = authService.getStoredUser()
    if (storedToken && storedUser && authService.isTokenValid(storedToken)) {
      token.value = storedToken
      usuario.value = storedUser
    }
  }

  function clearError(): void {
    error.value = null
  }

  return {
    usuario,
    token,
    loading,
    error,
    isAuthenticated,
    nombreCompleto,
    roles,
    permisos,
    hasRole,
    hasPermiso,
    isAdmin,
    login,
    logout,
    initFromStorage,
    clearError,
  }
})
