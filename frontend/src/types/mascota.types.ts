export type SexoMascota = 'macho' | 'hembra'
export type EstadoMascota = 'activo' | 'fallecido' | 'transferido'

export interface Especie {
  id: number
  nombre: string
  descripcion?: string
}

export interface Raza {
  id: number
  nombre: string
  especieId: number
  descripcion?: string
}

export interface Mascota {
  id: number
  nombre: string
  especieId: number
  especie: Especie
  razaId: number
  raza: Raza
  sexo: SexoMascota
  fechaNacimiento?: string
  color?: string
  peso?: number
  microchip?: string
  esterilizado: boolean
  estado: EstadoMascota
  clienteId: number
  clienteNombre: string
  foto?: string
  observaciones?: string
  createdAt: string
}

export interface MascotaFormData {
  nombre: string
  especieId: number
  razaId: number
  sexo: SexoMascota
  fechaNacimiento?: string
  color?: string
  peso?: number
  microchip?: string
  esterilizado: boolean
  clienteId: number
  observaciones?: string
}
