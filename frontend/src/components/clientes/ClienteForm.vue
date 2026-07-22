<script setup lang="ts">
import { reactive, watch } from 'vue'
import type { Cliente, ClienteFormData } from '@/types'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import FormActions from '@/components/forms/FormActions.vue'
import { TIPO_DOCUMENTO_OPTIONS } from '@/constants/documentTypes'
import { isValidEmail } from '@/utils/validators'

interface Props {
  cliente?: Cliente | null
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  cliente: null,
  loading: false,
})

const emit = defineEmits<{
  submit: [data: ClienteFormData]
  cancel: []
}>()

const tipoDocumentoOptions = TIPO_DOCUMENTO_OPTIONS

const form = reactive<ClienteFormData>({
  tipoDocumento: 'CC',
  numeroDocumento: '',
  nombre: '',
  apellido: '',
  email: '',
  telefono: '',
  direccion: '',
  ciudad: '',
  fechaNacimiento: '',
  observaciones: '',
})

const errors = reactive<Partial<Record<keyof ClienteFormData, string>>>({})

// Cargar datos si es edición
watch(
  () => props.cliente,
  (cliente) => {
    if (cliente) {
      form.tipoDocumento = cliente.tipoDocumento
      form.numeroDocumento = cliente.numeroDocumento
      form.nombre = cliente.nombre
      form.apellido = cliente.apellido
      form.email = cliente.email
      form.telefono = cliente.telefono
      form.direccion = cliente.direccion ?? ''
      form.ciudad = cliente.ciudad ?? ''
      form.fechaNacimiento = cliente.fechaNacimiento ?? ''
      form.observaciones = cliente.observaciones ?? ''
    }
  },
  { immediate: true },
)

function validate(): boolean {
  Object.keys(errors).forEach((k) => delete errors[k as keyof ClienteFormData])
  let valid = true

  if (!form.tipoDocumento) { errors.tipoDocumento = 'Requerido'; valid = false }
  if (!form.numeroDocumento.trim()) { errors.numeroDocumento = 'Requerido'; valid = false }
  if (!form.nombre.trim()) { errors.nombre = 'Requerido'; valid = false }
  if (!form.apellido.trim()) { errors.apellido = 'Requerido'; valid = false }
  if (!form.email.trim()) {
    errors.email = 'Requerido'; valid = false
  } else if (!isValidEmail(form.email)) {
    errors.email = 'Email inválido'; valid = false
  }
  if (!form.telefono.trim()) { errors.telefono = 'Requerido'; valid = false }

  return valid
}

function handleSubmit() {
  if (!validate()) return
  emit('submit', { ...form })
}
</script>

<template>
  <form @submit.prevent="handleSubmit" novalidate class="space-y-4">
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppSelect
        id="tipoDocumento"
        v-model="form.tipoDocumento"
        label="Tipo de documento"
        :options="tipoDocumentoOptions"
        :error="errors.tipoDocumento"
        required
      />
      <AppInput
        id="numeroDocumento"
        v-model="form.numeroDocumento"
        label="Número de documento"
        placeholder="Ej: 12345678"
        :error="errors.numeroDocumento"
        required
      />
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppInput
        id="nombre"
        v-model="form.nombre"
        label="Nombre"
        placeholder="Nombre del cliente"
        :error="errors.nombre"
        required
      />
      <AppInput
        id="apellido"
        v-model="form.apellido"
        label="Apellido"
        placeholder="Apellido del cliente"
        :error="errors.apellido"
        required
      />
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppInput
        id="email"
        v-model="form.email"
        label="Correo electrónico"
        type="email"
        placeholder="correo@ejemplo.com"
        :error="errors.email"
        required
      />
      <AppInput
        id="telefono"
        v-model="form.telefono"
        label="Teléfono"
        placeholder="Ej: 3001234567"
        :error="errors.telefono"
        required
      />
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppInput
        id="ciudad"
        v-model="form.ciudad"
        label="Ciudad"
        placeholder="Ciudad de residencia"
      />
      <AppInput
        id="fechaNacimiento"
        v-model="form.fechaNacimiento"
        label="Fecha de nacimiento"
        type="date"
      />
    </div>

    <AppInput
      id="direccion"
      v-model="form.direccion"
      label="Dirección"
      placeholder="Dirección completa"
    />

    <AppTextarea
      id="observaciones"
      v-model="form.observaciones"
      label="Observaciones"
      placeholder="Notas adicionales sobre el cliente..."
      :rows="2"
    />

    <FormActions
      :loading="props.loading"
      :submit-label="props.cliente ? 'Guardar cambios' : 'Registrar cliente'"
      @cancel="emit('cancel')"
    />
  </form>
</template>
