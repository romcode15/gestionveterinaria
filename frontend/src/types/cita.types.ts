export type EstadoCita = 'pendiente' | 'confirmada' | 'en_curso' | 'completada' | 'cancelada' | 'no_asistio'

export interface TipoCita {
  id: number
  nombre: string
  duracionMinutos: number
  color: string
  descripcion?: string
}

export interface Cita {
  id: number
  fecha: string
  horaInicio: string
  horaFin: string
  estado: EstadoCita
  tipoCitaId: number
  tipoCita: TipoCita
  medicoId: number
  medicoNombre: string
  mascotaId: number
  mascotaNombre: string
  clienteId: number
  clienteNombre: string
  motivo: string
  observaciones?: string
  createdAt: string
}

export interface CitaFormData {
  fecha: string
  horaInicio: string
  tipoCitaId: number
  medicoId: number
  mascotaId: number
  motivo: string
  observaciones?: string
}
