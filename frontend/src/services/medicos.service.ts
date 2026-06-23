import type { Medico, MedicoFormData, Especialidad } from '@/types'
import medicosJson from '@/data/json/medicos.json'
import especialidadesJson from '@/data/json/especialidades.json'

// ── resolución de relaciones (JOIN especialidades) ─────────────────────────

function resolveEspecialidades(ids: number[]): Especialidad[] {
  return especialidadesJson.filter((e) => ids.includes(e.id)) as Especialidad[]
}

function resolveMediaco(raw: (typeof medicosJson)[number]): Medico {
  return {
    ...raw,
    estado: raw.estado as Medico['estado'],
    tipoDocumento: raw.tipoDocumento as Medico['tipoDocumento'],
    especialidades: resolveEspecialidades(raw.especialidadesIds),
  }
}

// ── estado en memoria ──────────────────────────────────────────────────────

type MedicoRaw = (typeof medicosJson)[number]
let db: MedicoRaw[] = medicosJson.map((m) => ({ ...m }))

function nextId(): number {
  return db.length > 0 ? Math.max(...db.map((m) => m.id)) + 1 : 1
}

// ── API del servicio ───────────────────────────────────────────────────────

export const medicosService = {
  async getAll(): Promise<Medico[]> {
    await new Promise((r) => setTimeout(r, 300))
    return db.map(resolveMediaco)
  },

  async getById(id: number): Promise<Medico | undefined> {
    await new Promise((r) => setTimeout(r, 150))
    const raw = db.find((m) => m.id === id)
    return raw ? resolveMediaco(raw) : undefined
  },

  async getAllEspecialidades(): Promise<Especialidad[]> {
    await new Promise((r) => setTimeout(r, 150))
    return [...especialidadesJson] as Especialidad[]
  },

  async create(data: MedicoFormData): Promise<Medico> {
    await new Promise((r) => setTimeout(r, 500))
    const raw: MedicoRaw = {
      id: nextId(),
      tipoDocumento: data.tipoDocumento,
      numeroDocumento: data.numeroDocumento,
      nombre: data.nombre,
      apellido: data.apellido,
      email: data.email,
      telefono: data.telefono,
      numeroLicencia: data.numeroLicencia,
      especialidadesIds: data.especialidadesIds,
      disponible: data.disponible,
      estado: 'activo',
      createdAt: new Date().toISOString(),
    }
    db.push(raw)
    return resolveMediaco(raw)
  },

  async update(id: number, data: Partial<MedicoFormData>): Promise<Medico> {
    await new Promise((r) => setTimeout(r, 500))
    const idx = db.findIndex((m) => m.id === id)
    if (idx === -1) throw new Error(`Médico ${id} no encontrado`)
    db[idx] = {
      ...db[idx]!,
      ...data,
      especialidadesIds: data.especialidadesIds ?? db[idx]!.especialidadesIds,
    }
    return resolveMediaco(db[idx]!)
  },
}
