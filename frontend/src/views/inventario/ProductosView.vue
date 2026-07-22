<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppModal from '@/components/ui/AppModal.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppBadge from '@/components/ui/AppBadge.vue'
import AppPagination from '@/components/ui/AppPagination.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import SearchToolbar from '@/components/common/SearchToolbar.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { api } from '@/services/api'

// ── Tipos ──────────────────────────────────────────────────────────────────

interface Producto {
  id: number
  nombre: string
  codigo?: string
  categoria: string
  unidadMedida: string
  stockActual: number
  stockMinimo: number
  precio: number
  requiereReceta: boolean
  proveedorId?: number
  proveedorNombre?: string
  activo: boolean
}

interface Categoria {
  id: number
  nombre: string
}

interface ProveedorSimple {
  id: number
  nombre: string
}

interface Lote {
  id: number
  numeroLote: string
  fechaVencimiento: string
  cantidad: number
  cantidadActual: number
  precioCompra: number
  proveedorId: number
  proveedorNombre: string
  estado: 'activo' | 'agotado' | 'vencido'
}

interface MovimientoSalida {
  cantidad: number
  motivo: string
}

// ── Estado ─────────────────────────────────────────────────────────────────

const productos = ref<Producto[]>([])
const categorias = ref<Categoria[]>([])
const proveedores = ref<ProveedorSimple[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const successMsg = ref('')
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(20)

const searchQuery = ref('')
const filtroCategoria = ref<number | null>(null)

// Modal producto
const showModalProducto = ref(false)
const editandoProducto = ref<Producto | null>(null)
const guardandoProducto = ref(false)
const formErrorsProducto = ref<Record<string, string>>({})

const formProducto = ref({
  nombre: '',
  codigo: '',
  categoria: '',
  unidadMedida: '',
  stockMinimo: 5,
  precio: 0,
  requiereReceta: false,
  proveedorId: null as number | null,
})

// Modal lote (entrada)
const showModalLote = ref(false)
const productoSeleccionado = ref<Producto | null>(null)
const guardandoLote = ref(false)
const formErrorsLote = ref<Record<string, string>>({})

const formLote = ref({
  numeroLote: '',
  proveedorId: null as number | null,
  fechaVencimiento: '',
  cantidad: 0,
  precioCompra: 0,
})

// Modal salida
const showModalSalida = ref(false)
const loteSeleccionado = ref<Lote | null>(null)
const guardandoSalida = ref(false)
const formErrorsSalida = ref<Record<string, string>>({})

const formSalida = ref<MovimientoSalida>({
  cantidad: 0,
  motivo: '',
})

// Lotes del producto seleccionado
const lotes = ref<Lote[]>([])
const mostrarLotes = ref(false)

// ── Computed ─────────────────────────────────────────────────────────────────

const categoriaOptions = computed(() =>
  categorias.value.map(c => ({ value: c.id, label: c.nombre }))
)

const proveedorOptions = computed(() =>
  proveedores.value.map(p => ({ value: p.id, label: p.nombre }))
)

const unidadOptions = [
  { value: 'unidad', label: 'Unidad' },
  { value: 'kg', label: 'Kilogramo' },
  { value: 'g', label: 'Gramo' },
  { value: 'ml', label: 'Mililitro' },
  { value: 'l', label: 'Litro' },
]

const categoriaFiltroOptions = computed(() => [
  { value: '', label: 'Todas las categorías' },
  ...categoriaOptions.value,
])

// ── Carga de datos ─────────────────────────────────────────────────────────

async function cargarDatos() {
  await Promise.all([
    cargarProductos(),
    cargarCategorias(),
    cargarProveedores(),
  ])
}

async function cargarProductos(p = 0) {
  loading.value = true
  error.value = null
  try {
    const params: any = { page: p, size: pageSize.value, sort: 'nombre', dir: 'asc' }
    if (searchQuery.value) params.search = searchQuery.value
    if (filtroCategoria.value) params.categoriaId = filtroCategoria.value

    const res = await api.getPaged<Producto>('/api/inventario/productos', params)
    productos.value = res.content
    page.value = res.number
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar productos'
  } finally {
    loading.value = false
  }
}

async function cargarCategorias() {
  try {
    categorias.value = await api.get<Categoria[]>('/api/inventario/categorias')
  } catch (e) {
    console.error('Error cargando categorías:', e)
  }
}

async function cargarProveedores() {
  try {
    proveedores.value = await api.get<ProveedorSimple[]>('/api/inventario/proveedores/simple')
  } catch (e) {
    console.error('Error cargando proveedores:', e)
  }
}

async function cargarLotes(productoId: number) {
  try {
    lotes.value = await api.get<Lote[]>(`/api/inventario/lotes/producto/${productoId}`)
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Error al cargar lotes'
  }
}

onMounted(cargarDatos)

watch([searchQuery, filtroCategoria], () => {
  cargarProductos(0)
})

// ── CRUD Producto ──────────────────────────────────────────────────────────

function resetFormProducto() {
  formProducto.value = {
    nombre: '',
    codigo: '',
    categoria: '',
    unidadMedida: '',
    stockMinimo: 5,
    precio: 0,
    requiereReceta: false,
    proveedorId: null,
  }
  formErrorsProducto.value = {}
  editandoProducto.value = null
}

function abrirCrearProducto() {
  resetFormProducto()
  showModalProducto.value = true
}

function abrirEditarProducto(producto: Producto) {
  resetFormProducto()
  editandoProducto.value = producto
  formProducto.value = {
    nombre: producto.nombre,
    codigo: producto.codigo || '',
    categoria: producto.categoria,
    unidadMedida: producto.unidadMedida,
    stockMinimo: producto.stockMinimo,
    precio: producto.precio,
    requiereReceta: producto.requiereReceta,
    proveedorId: producto.proveedorId || null,
  }
  showModalProducto.value = true
}

async function guardarProducto() {
  formErrorsProducto.value = {}

  if (!formProducto.value.nombre.trim()) { formErrorsProducto.value.nombre = 'Requerido'; return }
  if (!formProducto.value.categoria) { formErrorsProducto.value.categoria = 'Requerido'; return }
  if (!formProducto.value.unidadMedida) { formErrorsProducto.value.unidadMedida = 'Requerido'; return }
  if (formProducto.value.precio <= 0) { formErrorsProducto.value.precio = 'Debe ser mayor a 0'; return }
  if (formProducto.value.stockMinimo < 0) { formErrorsProducto.value.stockMinimo = 'No puede ser negativo'; return }

  guardandoProducto.value = true
  try {
    const payload = {
      ...formProducto.value,
      proveedorId: formProducto.value.proveedorId || null,
    }

    if (editandoProducto.value) {
      await api.put(`/api/inventario/productos/${editandoProducto.value.id}`, payload)
      successMsg.value = 'Producto actualizado'
    } else {
      await api.post('/api/inventario/productos', payload)
      successMsg.value = 'Producto creado'
    }
    showModalProducto.value = false
    setTimeout(() => (successMsg.value = ''), 3000)
    await cargarProductos(page.value)
  } catch (e: unknown) {
    const err = e as { campos?: Record<string, string>; message?: string }
    if (err.campos) formErrorsProducto.value = err.campos
    else error.value = err.message ?? 'Error al guardar'
  } finally {
    guardandoProducto.value = false
  }
}

// ── Lotes ──────────────────────────────────────────────────────────────────

function verLotes(producto: Producto) {
  productoSeleccionado.value = producto
  mostrarLotes.value = true
  cargarLotes(producto.id)
}

function cerrarLotes() {
  mostrarLotes.value = false
  productoSeleccionado.value = null
  lotes.value = []
}

function abrirRegistrarLote(producto: Producto) {
  productoSeleccionado.value = producto
  formLote.value = {
    numeroLote: '',
    proveedorId: null,
    fechaVencimiento: '',
    cantidad: 0,
    precioCompra: 0,
  }
  formErrorsLote.value = {}
  showModalLote.value = true
}

async function guardarLote() {
  formErrorsLote.value = {}
  if (!formLote.value.numeroLote.trim()) { formErrorsLote.value.numeroLote = 'Requerido'; return }
  if (!formLote.value.proveedorId) { formErrorsLote.value.proveedorId = 'Requerido'; return }
  if (!formLote.value.fechaVencimiento) { formErrorsLote.value.fechaVencimiento = 'Requerido'; return }
  if (formLote.value.cantidad <= 0) { formErrorsLote.value.cantidad = 'Debe ser mayor a 0'; return }

  guardandoLote.value = true
  try {
    await api.post('/api/inventario/lotes', {
      productoId: productoSeleccionado.value!.id,
      numeroLote: formLote.value.numeroLote,
      proveedorId: formLote.value.proveedorId,
      fechaVencimiento: formLote.value.fechaVencimiento,
      cantidad: formLote.value.cantidad,
      precioCompra: formLote.value.precioCompra || 0,
    })
    showModalLote.value = false
    successMsg.value = 'Lote registrado correctamente'
    setTimeout(() => (successMsg.value = ''), 3000)
    await cargarLotes(productoSeleccionado.value!.id)
    await cargarProductos(page.value)
  } catch (e: unknown) {
    const err = e as { campos?: Record<string, string>; message?: string }
    if (err.campos) formErrorsLote.value = err.campos
    else error.value = err.message ?? 'Error al guardar lote'
  } finally {
    guardandoLote.value = false
  }
}

// ── Salida de inventario ──────────────────────────────────────────────────

function abrirRegistrarSalida(lote: Lote) {
  loteSeleccionado.value = lote
  formSalida.value = { cantidad: 0, motivo: '' }
  formErrorsSalida.value = {}
  showModalSalida.value = true
}

async function registrarSalida() {
  formErrorsSalida.value = {}
  if (formSalida.value.cantidad <= 0) { formErrorsSalida.value.cantidad = 'Debe ser mayor a 0'; return }
  if (formSalida.value.cantidad > (loteSeleccionado.value?.cantidadActual || 0)) {
    formErrorsSalida.value.cantidad = 'Cantidad insuficiente'
    return
  }
  if (!formSalida.value.motivo.trim()) { formErrorsSalida.value.motivo = 'Requerido'; return }

  guardandoSalida.value = true
  try {
    await api.post('/api/inventario/movimientos/salida', {
      loteId: loteSeleccionado.value!.id,
      cantidad: formSalida.value.cantidad,
      motivo: formSalida.value.motivo,
    })
    showModalSalida.value = false
    successMsg.value = 'Salida registrada correctamente'
    setTimeout(() => (successMsg.value = ''), 3000)
    await cargarLotes(productoSeleccionado.value!.id)
    await cargarProductos(page.value)
  } catch (e: unknown) {
    const err = e as { campos?: Record<string, string>; message?: string }
    if (err.campos) formErrorsSalida.value = err.campos
    else error.value = err.message ?? 'Error al registrar salida'
  } finally {
    guardandoSalida.value = false
  }
}

// ── Helpers ─────────────────────────────────────────────────────────────────

function formatMoneda(valor: number): string {
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(valor)
}

function formatFecha(f: string) {
  return new Date(f + 'T00:00:00').toLocaleDateString('es-CO')
}

function stockBajo(stock: number, minimo: number): boolean {
  return stock <= minimo
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Productos" subtitle="Gestión de inventario" />
    </template>

    <div class="space-y-4">
      <Transition name="fade">
        <AppAlert v-if="error" type="error" dismissible @dismiss="error = null">{{ error }}</AppAlert>
      </Transition>
      <Transition name="fade">
        <AppAlert v-if="successMsg" type="success" dismissible @dismiss="successMsg = ''">{{ successMsg }}</AppAlert>
      </Transition>

      <SearchToolbar
        v-model:search="searchQuery"
        search-placeholder="Buscar por nombre, código..."
        :show-new-button="true"
        new-button-label="Nuevo producto"
        @new="abrirCrearProducto"
      >
        <template #filters>
          <AppSelect
            v-model="filtroCategoria"
            :options="categoriaFiltroOptions"
            class="w-full sm:w-48"
          />
        </template>
      </SearchToolbar>

      <!-- Lista de productos -->
      <AppCard padding="none">
        <div class="px-6 py-4 border-b flex items-center justify-between" style="border-color: var(--border-color)">
          <h2 class="font-semibold" style="color: var(--text-primary)">
            {{ totalElements }} producto(s)
          </h2>
        </div>

        <div v-if="loading" class="p-4 space-y-3">
          <div v-for="i in 4" :key="i" class="h-16 vg-skeleton rounded-xl animate-pulse" />
        </div>

        <EmptyState
          v-else-if="productos.length === 0"
          icon="📦"
          title="Sin productos"
          message="No hay productos registrados en el inventario"
        />

        <div v-else class="divide-y" style="border-color: var(--border-color)">
          <div
            v-for="p in productos"
            :key="p.id"
            class="px-6 py-4 hover:bg-(--bg-surface-2) transition-colors"
          >
            <div class="flex flex-col sm:flex-row sm:items-center gap-3">
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2 flex-wrap">
                  <span class="font-semibold" style="color: var(--text-primary)">{{ p.nombre }}</span>
                  <AppBadge :variant="p.activo ? 'success' : 'neutral'" size="sm">
                    {{ p.activo ? 'Activo' : 'Inactivo' }}
                  </AppBadge>
                  <AppBadge v-if="p.requiereReceta" variant="warning" size="sm">
                    Receta médica
                  </AppBadge>
                </div>
                <p class="text-sm" style="color: var(--text-secondary)">
                  {{ p.categoria }} · {{ p.unidadMedida }}
                  <span v-if="p.codigo">· Código: {{ p.codigo }}</span>
                  <span v-if="p.proveedorNombre">· Prov: {{ p.proveedorNombre }}</span>
                </p>
                <div class="flex gap-4 mt-1 text-sm">
                  <span style="color: var(--text-primary)">{{ formatMoneda(p.precio) }}</span>
                  <span
                    :class="stockBajo(p.stockActual, p.stockMinimo) ? 'text-danger-500 font-semibold' : ''"
                    style="color: var(--text-muted)"
                  >
                    Stock: {{ p.stockActual }} / Min: {{ p.stockMinimo }}
                    <span v-if="stockBajo(p.stockActual, p.stockMinimo)">⚠️</span>
                  </span>
                </div>
              </div>
              <div class="flex gap-2 shrink-0 flex-wrap">
                <AppButton size="sm" variant="ghost" @click="verLotes(p)">
                  📦 Lotes
                </AppButton>
                <AppButton size="sm" variant="ghost" @click="abrirEditarProducto(p)">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                </AppButton>
              </div>
            </div>

            <!-- Lotes expandidos -->
            <div v-if="mostrarLotes && productoSeleccionado?.id === p.id" class="mt-4 pt-4 border-t" style="border-color: var(--border-color)">
              <div class="flex items-center justify-between mb-3">
                <h4 class="font-medium text-sm" style="color: var(--text-primary)">Lotes</h4>
                <AppButton size="sm" @click="abrirRegistrarLote(p)">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                  </svg>
                  Registrar entrada
                </AppButton>
              </div>
              <div v-if="lotes.length === 0" style="color: var(--text-muted)" class="text-sm">
                Sin lotes registrados
              </div>
              <div v-else class="space-y-2">
                <div
                  v-for="l in lotes"
                  :key="l.id"
                  class="flex flex-col sm:flex-row sm:items-center justify-between p-2 rounded-lg text-sm"
                  style="background-color: var(--bg-surface-2)"
                >
                  <div>
                    <span class="font-medium" style="color: var(--text-primary)">Lote {{ l.numeroLote }}</span>
                    <span class="ml-2" style="color: var(--text-muted)">{{ l.proveedorNombre }}</span>
                  </div>
                  <div class="flex items-center gap-3">
                    <span style="color: var(--text-secondary)">Stock: {{ l.cantidadActual }}</span>
                    <AppBadge :variant="l.estado === 'activo' ? 'success' : l.estado === 'agotado' ? 'neutral' : 'danger'" size="sm">
                      {{ l.estado }}
                    </AppBadge>
                    <span style="color: var(--text-muted)">Vence: {{ formatFecha(l.fechaVencimiento) }}</span>
                    <AppButton
                      v-if="l.estado === 'activo' && l.cantidadActual > 0"
                      size="sm"
                      variant="ghost"
                      @click="abrirRegistrarSalida(l)"
                    >
                      Salida
                    </AppButton>
                  </div>
                </div>
              </div>
              <AppButton size="sm" variant="ghost" class="mt-2" @click="cerrarLotes">
                Ocultar lotes
              </AppButton>
            </div>
          </div>
        </div>

        <div class="px-4 border-t" style="border-color: var(--border-color)">
          <AppPagination
            :page="page"
            :total-pages="totalPages"
            :total-elements="totalElements"
            :page-size="pageSize"
            :loading="loading"
            @change="cargarProductos"
          />
        </div>
      </AppCard>
    </div>

    <!-- Modal Producto -->
    <AppModal v-model="showModalProducto" :title="editandoProducto ? 'Editar producto' : 'Nuevo producto'" size="lg">
      <form @submit.prevent="guardarProducto" class="space-y-4">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="formProducto.nombre"
            label="Nombre"
            placeholder="Nombre del producto"
            required
            :error="formErrorsProducto.nombre"
          />
          <AppInput
            v-model="formProducto.codigo"
            label="Código (opcional)"
            placeholder="Código SKU"
          />
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="formProducto.categoria"
            label="Categoría"
            placeholder="Ej: Medicamentos"
            required
            :error="formErrorsProducto.categoria"
          />
          <AppSelect
            v-model="formProducto.unidadMedida"
            label="Unidad de medida"
            :options="unidadOptions"
            required
            :error="formErrorsProducto.unidadMedida"
          />
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="formProducto.precio"
            label="Precio"
            type="number"
            step="0.01"
            placeholder="0.00"
            required
            :error="formErrorsProducto.precio"
          />
          <AppInput
            v-model="formProducto.stockMinimo"
            label="Stock mínimo"
            type="number"
            placeholder="5"
            required
            :error="formErrorsProducto.stockMinimo"
          />
        </div>
        <AppSelect
          v-model="formProducto.proveedorId"
          label="Proveedor (opcional)"
          :options="proveedorOptions"
          placeholder="Seleccionar proveedor..."
        />
        <div class="flex items-center gap-3">
          <button
            type="button"
            @click="formProducto.requiereReceta = !formProducto.requiereReceta"
            :class="[
              'relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-1',
              formProducto.requiereReceta ? 'bg-primary-600' : 'bg-slate-300',
            ]"
            role="switch"
            :aria-checked="formProducto.requiereReceta"
          >
            <span
              :class="[
                'inline-block h-4 w-4 transform rounded-full bg-white shadow transition-transform',
                formProducto.requiereReceta ? 'translate-x-6' : 'translate-x-1',
              ]"
            />
          </button>
          <span class="text-sm" style="color: var(--text-secondary)">Requiere receta médica</span>
        </div>
        <div class="flex gap-3 justify-end pt-2">
          <AppButton type="button" variant="ghost" @click="showModalProducto = false">Cancelar</AppButton>
          <AppButton type="submit" :loading="guardandoProducto">Guardar</AppButton>
        </div>
      </form>
    </AppModal>

    <!-- Modal Registrar Lote -->
    <AppModal v-model="showModalLote" :title="`Registrar lote - ${productoSeleccionado?.nombre}`" size="md">
      <form @submit.prevent="guardarLote" class="space-y-4">
        <AppInput
          v-model="formLote.numeroLote"
          label="Número de lote"
          placeholder="Ej: L-2024-001"
          required
          :error="formErrorsLote.numeroLote"
        />
        <AppSelect
          v-model="formLote.proveedorId"
          label="Proveedor"
          :options="proveedorOptions"
          placeholder="Seleccionar proveedor..."
          required
          :error="formErrorsLote.proveedorId"
        />
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="formLote.fechaVencimiento"
            label="Fecha de vencimiento"
            type="date"
            required
            :error="formErrorsLote.fechaVencimiento"
          />
          <AppInput
            v-model="formLote.cantidad"
            label="Cantidad"
            type="number"
            placeholder="0"
            required
            :error="formErrorsLote.cantidad"
          />
        </div>
        <AppInput
          v-model="formLote.precioCompra"
          label="Precio de compra"
          type="number"
          step="0.01"
          placeholder="0.00"
        />
        <div class="flex gap-3 justify-end pt-2">
          <AppButton type="button" variant="ghost" @click="showModalLote = false">Cancelar</AppButton>
          <AppButton type="submit" :loading="guardandoLote">Registrar lote</AppButton>
        </div>
      </form>
    </AppModal>

    <!-- Modal Salida -->
    <AppModal v-model="showModalSalida" :title="`Registrar salida - Lote ${loteSeleccionado?.numeroLote}`" size="md">
      <form @submit.prevent="registrarSalida" class="space-y-4">
        <div class="flex gap-4 text-sm" style="color: var(--text-secondary)">
          <span>Stock actual: {{ loteSeleccionado?.cantidadActual }}</span>
          <span>Producto: {{ productoSeleccionado?.nombre }}</span>
        </div>
        <AppInput
          v-model="formSalida.cantidad"
          label="Cantidad a retirar"
          type="number"
          placeholder="0"
          required
          :error="formErrorsSalida.cantidad"
        />
        <AppTextarea
          v-model="formSalida.motivo"
          label="Motivo"
          placeholder="Ej: Venta, uso interno, merma..."
          required
          :error="formErrorsSalida.motivo"
          :rows="2"
        />
        <div class="flex gap-3 justify-end pt-2">
          <AppButton type="button" variant="ghost" @click="showModalSalida = false">Cancelar</AppButton>
          <AppButton type="submit" :loading="guardandoSalida">Registrar salida</AppButton>
        </div>
      </form>
    </AppModal>
  </DashboardLayout>
</template>