<script setup lang="ts">
import { reactive, watch, computed } from 'vue'
import type { Cita, CitaFormData, TipoCita, Medico, Mascota } from '@/types'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppButton from '@/components/ui/AppButton.vue'

interface Props {
  cita?: Cita | null
  tiposCita: TipoCita[]
  medicos: Medico[]
  mascotas: Mascota[]
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  cita: null,
  loading: false,
})

const emit = defineEmits<{
  submit: [data: CitaFormData]
  cancel: []
}>()

const form = reactive<CitaFormData>({
  fecha: new Date().toISOString().split('T')[0]!,
  horaInicio: '09:00',
  tipoCitaId: 0,
  medicoId: 0,
  mascotaId: 0,
  motivo: '',
  observaciones: '',
})

const errors = reactive<Partial<Record<keyof CitaFormData, string>>>({})

watch(
  () => props.cita,
  (cita) => {
    if (cita) {
      form.fecha = cita.fecha
      form.horaInicio = cita.horaInicio
      form.tipoCitaId = cita.tipoCitaId
      form.medicoId = cita.medicoId
      form.mascotaId = cita.mascotaId
      form.motivo = cita.motivo
      form.observaciones = cita.observaciones ?? ''
    }
  },
  { immediate: true },
)

const tipoCitaOptions = computed(() =>
  props.tiposCita.map((t) => ({ value: t.id, label: `${t.nombre} (${t.duracionMinutos} min)` })),
)

const medicoOptions = computed(() =>
  props.medicos
    .filter((m) => m.disponible)
    .map((m) => ({ value: m.id, label: `${m.nombre} ${m.apellido}` })),
)

const mascotaOptions = computed(() =>
  props.mascotas.map((m) => ({ value: m.id, label: `${m.nombre} (${m.clienteNombre})` })),
)

const horasDisponibles = computed(() => {
  const horas = []
  for (let h = 7; h <= 19; h++) {
    for (const m of [0, 30]) {
      const hora = `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`
      horas.push({ value: hora, label: hora })
    }
  }
  return horas
})

function validate(): boolean {
  Object.keys(errors).forEach((k) => delete errors[k as keyof CitaFormData])
  let valid = true
  if (!form.fecha) { errors.fecha = 'Requerido'; valid = false }
  if (!form.horaInicio) { errors.horaInicio = 'Requerido'; valid = false }
  if (!form.tipoCitaId) { errors.tipoCitaId = 'Requerido'; valid = false }
  if (!form.medicoId) { errors.medicoId = 'Requerido'; valid = false }
  if (!form.mascotaId) { errors.mascotaId = 'Requerido'; valid = false }
  if (!form.motivo.trim()) { errors.motivo = 'Requerido'; valid = false }
  return valid
}

function handleSubmit() {
  if (!validate()) return
  emit('submit', {
    ...form,
    tipoCitaId: Number(form.tipoCitaId),
    medicoId: Number(form.medicoId),
    mascotaId: Number(form.mascotaId),
  })
}
</script>

<template>
  <form @submit.prevent="handleSubmit" novalidate class="space-y-4">
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppInput
        id="fecha"
        v-model="form.fecha"
        label="Fecha"
        type="date"
        :error="errors.fecha"
        required
      />
      <AppSelect
        id="hora"
        v-model="form.horaInicio"
        label="Hora de inicio"
        :options="horasDisponibles"
        :error="errors.horaInicio"
        required
      />
    </div>

    <AppSelect
      id="tipoCita"
      v-model="form.tipoCitaId"
      label="Tipo de cita"
      :options="tipoCitaOptions"
      placeholder="Seleccionar tipo..."
      :error="errors.tipoCitaId"
      required
    />

    <AppSelect
      id="medico"
      v-model="form.medicoId"
      label="Médico"
      :options="medicoOptions"
      placeholder="Seleccionar médico..."
      :error="errors.medicoId"
      required
    />

    <AppSelect
      id="mascota"
      v-model="form.mascotaId"
      label="Mascota"
      :options="mascotaOptions"
      placeholder="Seleccionar mascota..."
      :error="errors.mascotaId"
      required
    />

    <AppInput
      id="motivo"
      v-model="form.motivo"
      label="Motivo de la consulta"
      placeholder="Describe el motivo de la cita..."
      :error="errors.motivo"
      required
    />

    <AppTextarea
      id="observaciones"
      v-model="form.observaciones"
      label="Observaciones adicionales"
      placeholder="Información adicional relevante..."
      :rows="2"
    />

    <div class="flex justify-end gap-3 pt-2">
      <AppButton type="button" variant="ghost" @click="emit('cancel')" :disabled="props.loading">
        Cancelar
      </AppButton>
      <AppButton type="submit" :loading="props.loading">
        {{ props.cita ? 'Guardar cambios' : 'Agendar cita' }}
      </AppButton>
    </div>
  </form>
</template>
