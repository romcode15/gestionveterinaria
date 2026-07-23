<script setup lang="ts">
import { reactive, watch, ref, computed, onMounted } from 'vue'
import type { UsuarioFormData } from '@/types'
import type { UsuarioListItem } from '@/services/usuarios.service'
import AppInput from '@/components/ui/AppInput.vue'
import FormActions from '@/components/forms/FormActions.vue'
import { isValidEmail } from '@/utils/validators'
import { api } from '@/services/api'

interface Props {
  usuario?: UsuarioListItem | null
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  usuario: null,
  loading: false,
})

const emit = defineEmits<{
  submit: [data: UsuarioFormData]
  cancel: []
}>()

// ── Roles disponibles (cargados desde el backend) ─────────────────────

interface RolOption { id: number; nombre: string }
const rolesDisponibles = ref<RolOption[]>([])
const rolesLoading = ref(false)

onMounted(async () => {
  rolesLoading.value = true
  try {
    const data = await api.get<{ id: number; nombre: string }[]>('/api/catalogos/roles')
    rolesDisponibles.value = data
  } catch {
    // fallback estático con los IDs reales de la BD
    rolesDisponibles.value = [
      { id: 1, nombre: 'admin' },
      { id: 2, nombre: 'veterinario' },
      { id: 3, nombre: 'recepcionista' },
      { id: 4, nombre: 'cliente' },
    ]
  } finally {
    rolesLoading.value = false
  }
})

// ── Formulario ────────────────────────────────────────────────────────

const form = reactive<UsuarioFormData>({
  username: '',
  password: '',
  email: '',
  nombre: '',
  apellido: '',
  activo: true,
  rolesIds: [],
})

const errors = reactive<Partial<Record<keyof UsuarioFormData | 'rolesIds', string>>>({})

// Modo: 'nuevo' | 'editar'
const esEdicion = computed(() => !!props.usuario)

// Rellenar datos en modo edición
watch(
  () => props.usuario,
  (u) => {
    if (u) {
      form.username  = u.username
      form.password  = ''   // no se muestra la contraseña almacenada
      form.email     = u.email
      form.nombre    = u.nombre
      form.apellido  = u.apellido
      form.activo    = u.activo
      // Mapear nombres de rol a IDs una vez que tengamos los roles disponibles
      form.rolesIds  = []
    }
  },
  { immediate: true },
)

// Cuando carguen los roles y estemos en modo edición, mapear nombres → IDs
watch(
  [rolesDisponibles, () => props.usuario],
  ([roles, usuario]) => {
    if (usuario && roles.length) {
      form.rolesIds = roles
        .filter((r) => usuario.rolesNombres.includes(r.nombre))
        .map((r) => r.id)
    }
  },
)

// ── Toggle de rol ─────────────────────────────────────────────────────

function toggleRol(id: number) {
  const idx = form.rolesIds.indexOf(id)
  if (idx === -1) {
    form.rolesIds.push(id)
  } else {
    form.rolesIds.splice(idx, 1)
  }
}

function tieneRol(id: number): boolean {
  return form.rolesIds.includes(id)
}

// ── Visibilidad de contraseña ─────────────────────────────────────────

const mostrarPassword = ref(false)

// ── Validación ────────────────────────────────────────────────────────

function validate(): boolean {
  Object.keys(errors).forEach((k) => delete errors[k as keyof typeof errors])
  let valid = true

  if (!form.username.trim()) { errors.username = 'Requerido'; valid = false }
  if (!esEdicion.value && !form.password?.trim()) {
    errors.password = 'La contraseña es obligatoria para nuevos usuarios'; valid = false
  }
  if (!form.nombre.trim())   { errors.nombre   = 'Requerido'; valid = false }
  if (!form.apellido.trim()) { errors.apellido  = 'Requerido'; valid = false }
  if (!form.email.trim()) {
    errors.email = 'Requerido'; valid = false
  } else if (!isValidEmail(form.email)) {
    errors.email = 'Email inválido'; valid = false
  }
  if (form.rolesIds.length === 0) {
    errors.rolesIds = 'Selecciona al menos un rol'; valid = false
  }

  return valid
}

function handleSubmit() {
  if (!validate()) return
  const payload: UsuarioFormData = { ...form }
  // Si estamos editando y no se ingresó contraseña, no la enviamos
  if (esEdicion.value && !payload.password?.trim()) {
    delete payload.password
  }
  emit('submit', payload)
}

// Etiqueta amigable del rol
const rolLabels: Record<string, string> = {
  admin: 'Administrador',
  veterinario: 'Veterinario',
  recepcionista: 'Recepcionista',
  auxiliar: 'Auxiliar',
  cliente: 'Cliente',
}
</script>

<template>
  <form @submit.prevent="handleSubmit" novalidate class="space-y-4">

    <!-- Nombre y apellido -->
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppInput
        id="nombre"
        v-model="form.nombre"
        label="Nombre"
        placeholder="Nombre del usuario"
        :error="errors.nombre"
        required
      />
      <AppInput
        id="apellido"
        v-model="form.apellido"
        label="Apellido"
        placeholder="Apellido del usuario"
        :error="errors.apellido"
        required
      />
    </div>

    <!-- Username y email -->
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppInput
        id="username"
        v-model="form.username"
        label="Usuario"
        placeholder="Nombre de usuario"
        :error="errors.username"
        required
      />
      <AppInput
        id="email"
        v-model="form.email"
        label="Correo electrónico"
        type="email"
        placeholder="correo@ejemplo.com"
        :error="errors.email"
        required
      />
    </div>

    <!-- Contraseña -->
    <div class="relative">
      <AppInput
        id="password"
        v-model="form.password"
        :label="esEdicion ? 'Nueva contraseña (dejar en blanco para no cambiar)' : 'Contraseña'"
        :type="mostrarPassword ? 'text' : 'password'"
        :placeholder="esEdicion ? 'Dejar en blanco para no cambiar' : 'Mínimo 4 caracteres'"
        :error="errors.password"
        :required="!esEdicion"
      />
      <button
        type="button"
        class="absolute right-3 top-8 text-sm"
        style="color: var(--text-muted)"
        @click="mostrarPassword = !mostrarPassword"
        :aria-label="mostrarPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
      >
        <svg v-if="mostrarPassword" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 4.411m0 0L21 21" />
        </svg>
        <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
        </svg>
      </button>
    </div>

    <!-- Roles -->
    <div>
      <p class="text-sm font-medium mb-2" style="color: var(--text-primary)">
        Roles <span class="text-red-500">*</span>
      </p>
      <div v-if="rolesLoading" class="text-sm" style="color: var(--text-muted)">Cargando roles...</div>
      <div v-else class="flex flex-wrap gap-2">
        <button
          v-for="rol in rolesDisponibles"
          :key="rol.id"
          type="button"
          :class="[
            'px-3 py-1.5 rounded-lg text-sm font-medium border transition-all duration-150',
            tieneRol(rol.id)
              ? 'bg-primary-500 border-primary-500 text-white'
              : 'border-current opacity-60 hover:opacity-100',
          ]"
          style="color: var(--text-secondary)"
          @click="toggleRol(rol.id)"
        >
          {{ rolLabels[rol.nombre] ?? rol.nombre }}
        </button>
      </div>
      <p v-if="errors.rolesIds" class="mt-1 text-xs text-red-500">{{ errors.rolesIds }}</p>
    </div>

    <!-- Estado (solo en edición) -->
    <div v-if="esEdicion" class="flex items-center gap-3">
      <label class="text-sm font-medium" style="color: var(--text-primary)">Estado</label>
      <button
        type="button"
        role="switch"
        :aria-checked="form.activo"
        :class="[
          'theme-toggle',
          form.activo ? 'bg-emerald-500' : '',
        ]"
        @click="form.activo = !form.activo"
      >
        <span class="theme-toggle-thumb" />
      </button>
      <span class="text-sm" style="color: var(--text-secondary)">
        {{ form.activo ? 'Activo' : 'Inactivo' }}
      </span>
    </div>

    <FormActions
      :loading="props.loading"
      :submit-label="esEdicion ? 'Guardar cambios' : 'Crear usuario'"
      @cancel="emit('cancel')"
    />
  </form>
</template>
