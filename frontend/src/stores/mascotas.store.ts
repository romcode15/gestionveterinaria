import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Mascota, MascotaFormData, Especie, Raza } from '@/types'
import type { PageParams } from '@/services/api'
import { mascotasService } from '@/services/mascotas.service'
import { api } from '@/services/api'

export const useMascotasStore = defineStore('mascotas', () => {
  // ── Estado ─────────────────────────────────────────────────────────────

  const mascotas      = ref<Mascota[]>([])
  const especiesData  = ref<Especie[]>([])
  const razasData     = ref<Raza[]>([])
  const loading       = ref(false)
  const error         = ref<string | null>(null)

  // Paginación
  const page          = ref(0)
  const pageSize      = ref(10)
  const totalElements = ref(0)
  const totalPages    = ref(0)

  // Filtros
  const searchQuery      = ref('')
  const filtroEspecieId  = ref<number | null>(null)

  // Si está activo, el store usa el portal médico en lugar del endpoint general
  const medicoId         = ref<number | null>(null)

  // ── Computed ────────────────────────────────────────────────────────────

  const especies = computed(() => especiesData.value)
  const razas    = computed(() => razasData.value)

  const razasPorEspecie = computed(() => (especieId: number) =>
    razasData.value.filter((r) => r.especieId === especieId),
  )

  const hayMasPaginas     = computed(() => page.value < totalPages.value - 1)
  const hayPaginaAnterior = computed(() => page.value > 0)

  // ── Acciones ────────────────────────────────────────────────────────────

  async function cargarCatalogos(): Promise<void> {
    const [especies, razas] = await Promise.all([
      mascotasService.getAllEspecies(),
      mascotasService.getAllRazas(),
    ])
    especiesData.value = especies
    razasData.value    = razas
  }

  async function cargar(params: PageParams = {}): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const p: PageParams = {
        page: params.page ?? page.value,
        size: params.size ?? pageSize.value,
        sort: params.sort ?? 'nombre',
        dir:  params.dir  ?? 'asc',
      }

      let res
      if (medicoId.value) {
        // Veterinario: solo sus mascotas via portal médico
        const portalParams: PageParams = { ...p }
        if (searchQuery.value.trim()) portalParams.busqueda = searchQuery.value.trim()
        try {
          res = await api.getPaged<Mascota>('/api/portal/medico/mascotas', portalParams)
        } catch {
          mascotas.value = []; totalElements.value = 0; totalPages.value = 0; loading.value = false; return
        }
      } else {
        res = searchQuery.value.trim()
          ? await mascotasService.buscar(searchQuery.value.trim(), p)
          : await mascotasService.getAll(p)
      }

      mascotas.value      = res.content
      page.value          = res.number
      pageSize.value      = res.size
      totalElements.value = res.totalElements
      totalPages.value    = res.totalPages
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar mascotas'
    } finally {
      loading.value = false
    }
  }

  async function cargarPorCliente(clienteId: number, params: PageParams = {}): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const res = await mascotasService.getByClienteId(clienteId, params)
      mascotas.value      = res.content
      page.value          = res.number
      pageSize.value      = res.size
      totalElements.value = res.totalElements
      totalPages.value    = res.totalPages
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al cargar mascotas del cliente'
    } finally {
      loading.value = false
    }
  }

  async function irAPagina(numeroPagina: number): Promise<void> {
    await cargar({ page: numeroPagina })
  }

  async function siguientePagina(): Promise<void> {
    if (hayMasPaginas.value) await cargar({ page: page.value + 1 })
  }

  async function paginaAnterior(): Promise<void> {
    if (hayPaginaAnterior.value) await cargar({ page: page.value - 1 })
  }

  function getById(id: number): Mascota | undefined {
    return mascotas.value.find((m) => m.id === id)
  }

  // Filtrar mascotas en memoria por clienteId (para portal cliente)
  function mascotasPorCliente(clienteId: number): Mascota[] {
    return mascotas.value.filter((m) => m.clienteId === clienteId)
  }

  async function crear(data: MascotaFormData): Promise<Mascota> {
    loading.value = true
    error.value   = null
    try {
      const nueva = await mascotasService.create(data)
      await cargar()
      return nueva
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al crear mascota'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function actualizar(id: number, data: Partial<MascotaFormData>): Promise<void> {
    loading.value = true
    error.value   = null
    try {
      const actualizada = await mascotasService.update(id, data)
      const idx = mascotas.value.findIndex((m) => m.id === id)
      if (idx !== -1) mascotas.value[idx] = actualizada
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error al actualizar mascota'
      throw e
    } finally {
      loading.value = false
    }
  }

  async function eliminar(id: number): Promise<void> {
    await mascotasService.delete(id)
    await cargar()
  }

  function limpiarError(): void {
    error.value = null
  }

  return {
    mascotas,
    loading,
    error,
    page,
    pageSize,
    totalElements,
    totalPages,
    searchQuery,
    filtroEspecieId,
    medicoId,
    especies,
    razas,
    razasPorEspecie,
    hayMasPaginas,
    hayPaginaAnterior,
    cargar,
    cargarCatalogos,
    cargarPorCliente,
    irAPagina,
    siguientePagina,
    paginaAnterior,
    getById,
    mascotasPorCliente,
    crear,
    actualizar,
    eliminar,
    limpiarError,
  }
})
