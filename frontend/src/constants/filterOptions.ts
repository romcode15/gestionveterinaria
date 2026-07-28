/**
 * Opciones de select reutilizables para los filtros de cada módulo.
 * Fuente única de verdad — evita duplicar el mismo array en múltiples vistas.
 */

export const ESTADO_PERSONA_OPTIONS = [
  { value: 'todos',    label: 'Todos los estados' },
  { value: 'activo',   label: 'Activos' },
  { value: 'inactivo', label: 'Inactivos' },
] as const

export const ESTADO_MASCOTA_OPTIONS = [
  { value: 'todos',       label: 'Todos los estados' },
  { value: 'activo',      label: 'Activos' },
  { value: 'fallecido',   label: 'Fallecidos' },
  { value: 'transferido', label: 'Transferidos' },
] as const

export const ESTADO_VACUNA_OPTIONS = [
  { value: 'todos',   label: 'Todos los estados' },
  { value: 'vigente', label: 'Vigentes' },
  { value: 'vencida', label: 'Vencidas' },
] as const

export const ESTADO_CITA_OPTIONS = [
  { value: 'todos',       label: 'Todos los estados' },
  { value: 'pendiente',   label: 'Pendientes' },
  { value: 'confirmada',  label: 'Confirmadas' },
  { value: 'en_curso',    label: 'En curso' },
  { value: 'completada',  label: 'Completadas' },
  { value: 'cancelada',   label: 'Canceladas' },
] as const

export const ESTADO_CITA_CAMBIO_OPTIONS = [
  { value: 'pendiente',  label: 'Pendiente' },
  { value: 'confirmada', label: 'Confirmar' },
  { value: 'en_curso',   label: 'En curso' },
  { value: 'completada', label: 'Completar' },
  { value: 'cancelada',  label: 'Cancelar' },
] as const

export const ROL_OPTIONS = [
  { value: 'todos',         label: 'Todos los roles' },
  { value: 'admin',         label: 'Administrador' },
  { value: 'veterinario',   label: 'Veterinario' },
  { value: 'recepcionista', label: 'Recepcionista' },
  { value: 'cliente',       label: 'Cliente' },
] as const

export const ACCION_AUDITORIA_OPTIONS = [
  { value: '',        label: 'Todas las acciones' },
  { value: 'CREATE',  label: 'Crear' },
  { value: 'UPDATE',  label: 'Actualizar' },
  { value: 'DELETE',  label: 'Eliminar' },
  { value: 'LOGIN',   label: 'Login' },
  { value: 'LOGOUT',  label: 'Logout' },
] as const
