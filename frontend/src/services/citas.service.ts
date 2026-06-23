import type { Cita, CitaFormData, EstadoCita, TipoCita } from '@/types'
import citasJson from '@/data/json/citas.json'
import tiposCitaJson from '@/data/json/tipos-cita.json'
import medicosJson from '@/data/json/medicos.json'
import mascotasJson from '@/data/json/mascotas.json'
import clientesJson from '@/data/json/clientes.json'

// ── resolución de relaciones ───────────────────────────────────────────────

function resolveCita(raw: (typeof citasJson)[number]): Cita {
  const tipoCita = tiposCitaJson.find((t) => t.id === raw.tipoCitaId) as TipoCita
  const medico = medicosJson.find((m) => m.id === raw.medicoId)
  const mascota = mascotasJson.find((m) => m.id === raw.mascotaId)
  const cliente = clientesJson.find((c) => c.id === raw.clienteId)

  return {
    ...raw,
    estado: raw.estado as EstadoCita,
    tipoCita,
    medicoNombre: medico ? `${medico.nombre} ${medico.apellido}` : '',
    mascotaNombre: mascota?.nombre ?? '',
    clienteNombre: cliente ? `${cliente.nombre} ${cliente.apellido}` : '',
    observaciones: raw.observaciones ?? '',
  }
}

function calcularHoraFin(horaInicio: string, duracionMinutos: number): string {
  const [h, m] = horaInicio.split(':').map(Number)
  const total = (h ?? 0) * 60 + (m ?? 0) + duracionMinutos
  return `${String(Math.floor(total / 60)).padStart(2, '0')}:${String(total % 60).padStart(2, '0')}`
}

// ── estado en memoria ──────────────────────────────────────────────────────

type CitaRaw = (typeof citasJson)[number]
let db: CitaRaw[] = citasJson.map((c) => ({ ...c }))

function nextId(): number {
  return db.length > 0 ? Math.max(...db.map((c) => c.id)) + 1 : 1
}

// ── API del servicio ───────────────────────────────────────────────────────

export const citasService = {
  async getAll(): Promise<Cita[]> {
    await new Promise((r) => setTimeout(r, 300))
    return db.map(resolveCita)
  },

  async getById(id: number): Promise<Cita | undefined> {
    await new Promise((r) => setTimeout(r, 150))
    const raw = db.find((c) => c.id === id)
    return raw ? resolveCita(raw) : undefined
  },

  async getByClienteId(clienteId: number): Promise<Cita[]> {
    await new Promise((r) => setTimeout(r, 200))
    return db.filter((c) => c.clienteId === clienteId).map(resolveCita)
  },

  async getAllTiposCita(): Promise<TipoCita[]> {
    await new Promise((r) => setTimeout(r, 150))
    return [...tiposCitaJson] as TipoCita[]
  },

  async create(data: CitaFormData): Promise<Cita> {
    await new Promise((r) => setTimeout(r, 500))
    const tipoCita = tiposCitaJson.find((t) => t.id === data.tipoCitaId)!
    const raw: CitaRaw = {
      id: nextId(),
      fecha: data.fecha,
      horaInicio: data.horaInicio,
      horaFin: calcularHoraFin(data.horaInicio, tipoCita.duracionMinutos),
      estado: 'pendiente',
      tipoCitaId: data.tipoCitaId,
      medicoId: data.medicoId,
      mascotaId: data.mascotaId,
      clienteId: mascotasJson.find((m) => m.id === data.mascotaId)?.clienteId ?? 0,
      motivo: data.motivo,
      observaciones: data.observaciones ?? '',
      createdAt: new Date().toISOString(),
    }
    db.push(raw)
    return resolveCita(raw)
  },

  async update(id: number, data: Partial<CitaFormData>): Promise<Cita> {
    await new Promise((r) => setTimeout(r, 500))
    const idx = db.findIndex((c) => c.id === id)
    if (idx === -1) throw new Error(`Cita ${id} no encontrada`)
    db[idx] = { ...db[idx]!, ...data }
    return resolveCita(db[idx]!)
  },

  async changeStatus(id: number, estado: EstadoCita): Promise<Cita> {
    await new Promise((r) => setTimeout(r, 200))
    const raw = db.find((c) => c.id === id)
    if (!raw) throw new Error(`Cita ${id} no encontrada`)
    raw.estado = estado
    return resolveCita(raw)
  },
}
