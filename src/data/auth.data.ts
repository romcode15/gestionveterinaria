import type { Usuario, Rol, Permiso } from '@/types'

export const mockPermisos: Permiso[] = [
  { id: 1, nombre: 'clientes.ver', descripcion: 'Ver clientes', modulo: 'personas' },
  { id: 2, nombre: 'clientes.crear', descripcion: 'Crear clientes', modulo: 'personas' },
  { id: 3, nombre: 'clientes.editar', descripcion: 'Editar clientes', modulo: 'personas' },
  { id: 4, nombre: 'medicos.ver', descripcion: 'Ver médicos', modulo: 'personas' },
  { id: 5, nombre: 'medicos.crear', descripcion: 'Crear médicos', modulo: 'personas' },
  { id: 6, nombre: 'mascotas.ver', descripcion: 'Ver mascotas', modulo: 'mascotas' },
  { id: 7, nombre: 'mascotas.crear', descripcion: 'Crear mascotas', modulo: 'mascotas' },
  { id: 8, nombre: 'mascotas.editar', descripcion: 'Editar mascotas', modulo: 'mascotas' },
  { id: 9, nombre: 'citas.ver', descripcion: 'Ver citas', modulo: 'citas' },
  { id: 10, nombre: 'citas.crear', descripcion: 'Crear citas', modulo: 'citas' },
  { id: 11, nombre: 'citas.editar', descripcion: 'Editar citas', modulo: 'citas' },
  { id: 12, nombre: 'citas.cancelar', descripcion: 'Cancelar citas', modulo: 'citas' },
  { id: 13, nombre: 'admin.usuarios', descripcion: 'Administrar usuarios', modulo: 'admin' },
]

export const mockRoles: Rol[] = [
  {
    id: 1,
    nombre: 'admin',
    descripcion: 'Administrador del sistema',
    permisos: mockPermisos,
  },
  {
    id: 2,
    nombre: 'veterinario',
    descripcion: 'Médico veterinario',
    permisos: mockPermisos.filter((p) =>
      ['clientes.ver', 'mascotas.ver', 'mascotas.editar', 'citas.ver', 'citas.editar'].includes(
        p.nombre,
      ),
    ),
  },
  {
    id: 3,
    nombre: 'recepcionista',
    descripcion: 'Recepcionista',
    permisos: mockPermisos.filter((p) =>
      [
        'clientes.ver',
        'clientes.crear',
        'clientes.editar',
        'mascotas.ver',
        'mascotas.crear',
        'citas.ver',
        'citas.crear',
        'citas.cancelar',
      ].includes(p.nombre),
    ),
  },
]

export const mockUsuarios: Usuario[] = [
  {
    id: 1,
    username: 'admin',
    email: 'admin@vetclinic.com',
    nombre: 'Carlos',
    apellido: 'Administrador',
    activo: true,
    roles: [mockRoles[0]!],
    ultimoAcceso: '2026-05-07T08:30:00',
  },
  {
    id: 2,
    username: 'dra.garcia',
    email: 'garcia@vetclinic.com',
    nombre: 'Laura',
    apellido: 'García',
    activo: true,
    roles: [mockRoles[1]!],
    ultimoAcceso: '2026-05-07T09:00:00',
  },
  {
    id: 3,
    username: 'recepcion',
    email: 'recepcion@vetclinic.com',
    nombre: 'María',
    apellido: 'López',
    activo: true,
    roles: [mockRoles[2]!],
    ultimoAcceso: '2026-05-06T17:00:00',
  },
]

// Credenciales de prueba: username -> password
export const mockCredentials: Record<string, string> = {
  admin: 'admin123',
  'dra.garcia': 'vet123',
  recepcion: 'rec123',
}
