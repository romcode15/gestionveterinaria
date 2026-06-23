<script setup lang="ts">
import { reactive, watch, computed } from 'vue'
import type { Mascota, MascotaFormData, Especie, Raza, Cliente } from '@/types'
import AppInput from '@/components/ui/AppInput.vue'
import AppSelect from '@/components/ui/AppSelect.vue'
import AppTextarea from '@/components/ui/AppTextarea.vue'
import AppButton from '@/components/ui/AppButton.vue'

interface Props {
  mascota?: Mascota | null
  especies: Especie[]
  razas: Raza[]
  clientes: Cliente[]
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  mascota: null,
  loading: false,
})

const emit = defineEmits<{
  submit: [data: MascotaFormData]
  cancel: []
}>()

const form = reactive<MascotaFormData>({
  nombre: '',
  especieId: 0,
  razaId: 0,
  sexo: 'macho',
  fechaNacimiento: '',
  color: '',
  peso: undefined,
  microchip: '',
  esterilizado: false,
  clienteId: 0,
  observaciones: '',
})

const errors = reactive<Partial<Record<keyof MascotaFormData, string>>>({})

watch(
  () => props.mascota,
  (mascota) => {
    if (mascota) {
      form.nombre = mascota.nombre
      form.especieId = mascota.especieId
      form.razaId = mascota.razaId
      form.sexo = mascota.sexo
      form.fechaNacimiento = mascota.fechaNacimiento ?? ''
      form.color = mascota.color ?? ''
      form.peso = mascota.peso
      form.microchip = mascota.microchip ?? ''
      form.esterilizado = mascota.esterilizado
      form.clienteId = mascota.clienteId
      form.observaciones = mascota.observaciones ?? ''
    }
  },
  { immediate: true },
)

const razasFiltradas = computed(() =>
  form.especieId ? props.razas.filter((r) => r.especieId === Number(form.especieId)) : [],
)

watch(
  () => form.especieId,
  () => { form.razaId = 0 },
)

const especieOptions = computed(() =>
  props.especies.map((e) => ({ value: e.id, label: e.nombre })),
)

const razaOptions = computed(() =>
  razasFiltradas.value.map((r) => ({ value: r.id, label: r.nombre })),
)

const clienteOptions = computed(() =>
  props.clientes
    .filter((c) => c.estado === 'activo')
    .map((c) => ({ value: c.id, label: `${c.nombre} ${c.apellido}` })),
)

const sexoOptions = [
  { value: 'macho', label: '♂ Macho' },
  { value: 'hembra', label: '♀ Hembra' },
]

function validate(): boolean {
  Object.keys(errors).forEach((k) => delete errors[k as keyof MascotaFormData])
  let valid = true
  if (!form.nombre.trim()) { errors.nombre = 'Requerido'; valid = false }
  if (!form.especieId) { errors.especieId = 'Requerido'; valid = false }
  if (!form.razaId) { errors.razaId = 'Requerido'; valid = false }
  if (!form.clienteId) { errors.clienteId = 'Requerido'; valid = false }
  return valid
}

function handleSubmit() {
  if (!validate()) return
  emit('submit', {
    ...form,
    especieId: Number(form.especieId),
    razaId: Number(form.razaId),
    clienteId: Number(form.clienteId),
    peso: form.peso ? Number(form.peso) : undefined,
  })
}
</script>

<template>
  <form @submit.prevent="handleSubmit" novalidate class="space-y-4">
    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppInput
        id="nombre"
        v-model="form.nombre"
        label="Nombre de la mascota"
        placeholder="Ej: Max"
        :error="errors.nombre"
        required
      />
      <AppSelect
        id="cliente"
        v-model="form.clienteId"
        label="Propietario"
        :options="clienteOptions"
        placeholder="Seleccionar propietario..."
        :error="errors.clienteId"
        required
      />
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppSelect
        id="especie"
        v-model="form.especieId"
        label="Especie"
        :options="especieOptions"
        placeholder="Seleccionar especie..."
        :error="errors.especieId"
        required
      />
      <AppSelect
        id="raza"
        v-model="form.razaId"
        label="Raza"
        :options="razaOptions"
        placeholder="Seleccionar raza..."
        :error="errors.razaId"
        :disabled="!form.especieId"
        required
      />
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <AppSelect
        id="sexo"
        v-model="form.sexo"
        label="Sexo"
        :options="sexoOptions"
        required
      />
      <AppInput
        id="fechaNacimiento"
        v-model="form.fechaNacimiento"
        label="Fecha de nacimiento"
        type="date"
      />
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <AppInput
        id="color"
        v-model="form.color"
        label="Color / Pelaje"
        placeholder="Ej: Dorado"
      />
      <AppInput
        id="peso"
        v-model="form.peso"
        label="Peso (kg)"
        type="number"
        placeholder="Ej: 12.5"
      />
      <AppInput
        id="microchip"
        v-model="form.microchip"
        label="Microchip"
        placeholder="Número de microchip"
      />
    </div>

    <div class="flex items-center gap-3">
      <button
        type="button"
        @click="form.esterilizado = !form.esterilizado"
        :class="[
          'relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-1',
          form.esterilizado ? 'bg-primary-600' : 'bg-slate-300',
        ]"
        :aria-checked="form.esterilizado"
        role="switch"
        aria-label="Esterilizado"
      >
        <span
          :class="[
            'inline-block h-4 w-4 transform rounded-full bg-white shadow transition-transform',
            form.esterilizado ? 'translate-x-6' : 'translate-x-1',
          ]"
        />
      </button>
      <span class="text-sm text-slate-700">Esterilizado / Castrado</span>
    </div>

    <AppTextarea
      id="observaciones"
      v-model="form.observaciones"
      label="Observaciones"
      placeholder="Alergias, condiciones especiales, notas..."
      :rows="2"
    />

    <div class="flex justify-end gap-3 pt-2">
      <AppButton type="button" variant="ghost" @click="emit('cancel')" :disabled="props.loading">
        Cancelar
      </AppButton>
      <AppButton type="submit" :loading="props.loading">
        {{ props.mascota ? 'Guardar cambios' : 'Registrar mascota' }}
      </AppButton>
    </div>
  </form>
</template>
