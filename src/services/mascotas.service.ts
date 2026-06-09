import type { Mascota, MascotaFormData, Especie, Raza } from '@/types'
import mascotasJson from '@/data/json/mascotas.json'
import especiesJson from '@/data/json/especies.json'
import razasJson from '@/data/json/razas.json'
import clientesJson from '@/data/json/clientes.json'

// ── resolución de relaciones ───────────────────────────────────────────────

function resolveMascota(raw: (typeof mascotasJson)[number]): Mascota {
  const especie = especiesJson.find((e) => e.id === raw.especieId) as Especie
  const raza = razasJson.find((r) => r.id === raw.razaId) as Raza
  const cliente = clientesJson.find((c) => c.id === raw.clienteId)
  const clienteNombre = cliente ? `${cliente.nombre} ${cliente.apellido}` : ''

  return {
    ...raw,
    sexo: raw.sexo as Mascota['sexo'],
    estado: raw.estado as Mascota['estado'],
    especie,
    raza,
    clienteNombre,
    observaciones: raw.observaciones ?? '',
  }
}

// ── estado en memoria ──────────────────────────────────────────────────────

type MascotaRaw = (typeof mascotasJson)[number]
let db: MascotaRaw[] = mascotasJson.map((m) => ({ ...m }))

function nextId(): number {
  return db.length > 0 ? Math.max(...db.map((m) => m.id)) + 1 : 1
}

// ── API del servicio ───────────────────────────────────────────────────────

export const mascotasService = {
  async getAll(): Promise<Mascota[]> {
    await new Promise((r) => setTimeout(r, 300))
    return db.map(resolveMascota)
  },

  async getById(id: number): Promise<Mascota | undefined> {
    await new Promise((r) => setTimeout(r, 150))
    const raw = db.find((m) => m.id === id)
    return raw ? resolveMascota(raw) : undefined
  },

  async getByClienteId(clienteId: number): Promise<Mascota[]> {
    await new Promise((r) => setTimeout(r, 200))
    return db.filter((m) => m.clienteId === clienteId && m.estado === 'activo').map(resolveMascota)
  },

  async getAllEspecies(): Promise<Especie[]> {
    await new Promise((r) => setTimeout(r, 150))
    return [...especiesJson] as Especie[]
  },

  async getAllRazas(): Promise<Raza[]> {
    await new Promise((r) => setTimeout(r, 150))
    return [...razasJson] as Raza[]
  },

  async create(data: MascotaFormData): Promise<Mascota> {
    await new Promise((r) => setTimeout(r, 500))
    const raw: MascotaRaw = {
      id: nextId(),
      nombre: data.nombre,
      especieId: data.especieId,
      razaId: data.razaId,
      sexo: data.sexo,
      fechaNacimiento: data.fechaNacimiento ?? '',
      color: data.color ?? '',
      peso: data.peso ?? 0,
      microchip: data.microchip ?? '',
      esterilizado: data.esterilizado,
      estado: 'activo',
      clienteId: data.clienteId,
      observaciones: data.observaciones ?? '',
      createdAt: new Date().toISOString(),
    }
    db.push(raw)
    return resolveMascota(raw)
  },

  async update(id: number, data: Partial<MascotaFormData>): Promise<Mascota> {
    await new Promise((r) => setTimeout(r, 500))
    const idx = db.findIndex((m) => m.id === id)
    if (idx === -1) throw new Error(`Mascota ${id} no encontrada`)
    db[idx] = { ...db[idx]!, ...data }
    return resolveMascota(db[idx]!)
  },
}
