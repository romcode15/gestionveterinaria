<script setup lang="ts">
import { reactive, watch } from 'vue'
import type { Medico, MedicoFormData, Especialidad } from '@/types'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppToggle from '@/components/ui/AppToggle.vue'
import EspecialidadSelect from './EspecialidadSelect.vue'
import FormActions from '@/components/forms/FormActions.vue'

interface Props {
  medico?: Medico | null
  especialidades: Especialidad[]
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  medico: null,
  loading: false,
})

const emit = defineEmits<{
  submit: [data: MedicoFormData]
  cancel: []
}>()

import { TIPO_DOCUMENTO_OPTIONS } from '@/constants/documentTypes'
import { isValidEmail } from '@/utils/validators'

const form = reactive<MedicoFormData>({
  tipoDocumento: 'CC',
  numeroDocumento: '',
  nombre: '',
  apellido: '',
  email: '',
  telefono: '',
  numeroLicencia: '',
  especialidadesIds: [],
  disponible: true,
})

const errors = reactive<Partial<Record<keyof MedicoFormData, string>>>({})

watch(
  () => props.medico,
  (medico) => {
    if (medico) {
      form.tipoDocumento = medico.tipoDocumento
      form.numeroDocumento = medico.numeroDocumento
      form.nombre = medico.nombre
      form.apellido = medico.apellido
      form.email = medico.email
      form.telefono = medico.telefono
      form.numeroLicencia = medico.numeroLicencia
      form.especialidadesIds = medico.especialidades.map((e) => e.id)
      form.disponible = medico.disponible
    }
  },
  { immediate: true },
)

function validate(): boolean {
  Object.keys(errors).forEach((k) => delete errors[k as keyof MedicoFormData])
  let valid = true
  if (!form.numeroDocumento.trim()) { errors.numeroDocumento = 'Requerido'; valid = false }
  if (!form.nombre.trim()) { errors.nombre = 'Requerido'; valid = false }
  if (!form.apellido.trim()) { errors.apellido = 'Requerido'; valid = false }
  if (!form.email.trim()) {
    errors.email = 'Requerido'; valid = false
  } else if (!isValidEmail(form.email)) {
    errors.email = 'Email inválido'; valid = false
  }
  if (!form.telefono.trim()) { errors.telefono = 'Requerido'; valid = false }
  if (!form.numeroLicencia.trim()) { errors.numeroLicencia = 'Requerido'; valid = false }
  if (form.especialidadesIds.length === 0) { errors.especialidadesIds = 'Selecciona al menos una especialidad'; valid = false }
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
        id="tipoDoc"
        v-model="form.tipoDocumento"
        label="Tipo de documento"
        :options="TIPO_DOCUMENTO_OPTIONS"
        required
      />
      <AppInput
        id="numDoc"
        v-model="form.numeroDocumento"
        label="Número de documento"
        placeholder="Ej: 44556677"
        :error="errors.numeroDocumento"
        required
      />
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppInput
        id="nombre"
        v-model="form.nombre"
        label="Nombre"
        placeholder="Nombre del médico"
        :error="errors.nombre"
        required
      />
      <AppInput
        id="apellido"
        v-model="form.apellido"
        label="Apellido"
        placeholder="Apellido del médico"
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
        placeholder="medico@clinica.com"
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

    <AppInput
      id="licencia"
      v-model="form.numeroLicencia"
      label="Número de licencia"
      placeholder="Ej: VET-2024-001"
      :error="errors.numeroLicencia"
      required
    />

    <EspecialidadSelect
      v-model="form.especialidadesIds"
      :especialidades="props.especialidades"
      :error="errors.especialidadesIds"
    />

    <AppToggle
      v-model="form.disponible"
      label="Disponible para citas"
      aria-label="Disponible"
    />

    <FormActions
      :loading="props.loading"
      :submit-label="props.medico ? 'Guardar cambios' : 'Registrar médico'"
      @cancel="emit('cancel')"
    />
  </form>
</template>
