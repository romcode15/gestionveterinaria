export type TipoDocumento = 'CC' | 'CE' | 'NIT' | 'PP'
export type TipoSangre = 'A+' | 'A-' | 'B+' | 'B-' | 'AB+' | 'AB-' | 'O+' | 'O-'
export type EstadoPersona = 'activo' | 'inactivo'

export interface Persona {
  id: number
  tipoDocumento: TipoDocumento
  numeroDocumento: string
  nombre: string
  apellido: string
  email: string
  telefono: string
  direccion?: string
  ciudad?: string
  fechaNacimiento?: string
  estado: EstadoPersona
  createdAt: string
}

export interface Cliente extends Persona {
  numeroMascotas: number
  observaciones?: string
  usuarioId?: number
  username?: string
}

export interface Especialidad {
  id: number
  nombre: string
  descripcion?: string
}

export interface Medico extends Persona {
  numeroLicencia: string
  especialidades: Especialidad[]
  disponible: boolean
  foto?: string
  usuarioId?: number
  username?: string
}

export interface Empleado extends Persona {
  cargo: string
  departamento: string
  fechaIngreso: string
}

export interface ClienteFormData {
  tipoDocumento: TipoDocumento
  numeroDocumento: string
  nombre: string
  apellido: string
  email: string
  telefono: string
  direccion?: string
  ciudad?: string
  fechaNacimiento?: string
  observaciones?: string
}

export interface MedicoFormData {
  tipoDocumento: TipoDocumento
  numeroDocumento: string
  nombre: string
  apellido: string
  email: string
  telefono: string
  numeroLicencia: string
  especialidadesIds: number[]
  disponible: boolean
}

export interface Recepcionista {
  id: number
  nombre: string
  apellido: string
  email: string
  telefono?: string
  estado: EstadoPersona
  createdAt: string
  usuarioId?: number
  username?: string
}

export interface RecepcionistaFormData {
  nombre: string
  apellido: string
  email: string
  telefono?: string
}
