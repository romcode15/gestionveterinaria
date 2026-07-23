<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import { api } from '@/services/api'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import AppCard from '@/components/ui/AppCard.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppPasswordInput from '@/components/ui/AppPasswordInput.vue'
import AppAlert from '@/components/ui/AppAlert.vue'
import PageHeader from '@/components/common/PageHeader.vue'

const authStore = useAuthStore()

const form = reactive({
  passwordActual: '',
  passwordNueva: '',
  passwordConfirm: '',
})

const errors = reactive({ passwordActual: '', passwordNueva: '', passwordConfirm: '' })
const loading  = ref(false)
const success  = ref('')
const apiError = ref('')

function validate(): boolean {
  errors.passwordActual  = ''
  errors.passwordNueva   = ''
  errors.passwordConfirm = ''
  let ok = true

  if (!form.passwordActual)
    { errors.passwordActual = 'Ingresa tu contraseña actual'; ok = false }
  if (!form.passwordNueva || form.passwordNueva.length < 4)
    { errors.passwordNueva = 'La nueva contraseña debe tener al menos 4 caracteres'; ok = false }
  if (form.passwordNueva !== form.passwordConfirm)
    { errors.passwordConfirm = 'Las contraseñas no coinciden'; ok = false }

  return ok
}

async function handleSubmit() {
  if (!validate()) return
  loading.value = true
  success.value = ''
  apiError.value = ''
  try {
    await api.patch<void>('/api/auth/cambiar-password', {
      passwordActual: form.passwordActual,
      passwordNueva:  form.passwordNueva,
    })
    success.value = 'Contraseña actualizada correctamente'
    form.passwordActual  = ''
    form.passwordNueva   = ''
    form.passwordConfirm = ''
  } catch (e) {
    apiError.value = e instanceof Error ? e.message : 'Error al cambiar la contraseña'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <DashboardLayout>
    <template #header>
      <PageHeader title="Mi perfil" subtitle="Configuración de tu cuenta" />
    </template>

    <div class="max-w-lg space-y-6">
      <!-- Datos del usuario -->
      <AppCard>
        <div class="flex items-center gap-4">
          <div class="w-14 h-14 rounded-full flex items-center justify-center text-xl font-bold text-white"
            style="background: var(--color-primary)">
            {{ authStore.usuario?.nombre?.[0] }}{{ authStore.usuario?.apellido?.[0] }}
          </div>
          <div>
            <p class="text-lg font-semibold" style="color: var(--text-primary)">
              {{ authStore.nombreCompleto }}
            </p>
            <p class="text-sm" style="color: var(--text-muted)">{{ authStore.usuario?.email }}</p>
            <p class="text-xs mt-0.5" style="color: var(--text-muted)">
              Usuario: <span class="font-mono">{{ authStore.usuario?.username }}</span>
            </p>
          </div>
        </div>
      </AppCard>

      <!-- Cambio de contraseña -->
      <AppCard>
        <h2 class="text-base font-semibold mb-4" style="color: var(--text-primary)">
          Cambiar contraseña
        </h2>

        <Transition name="fade">
          <AppAlert v-if="success" type="success" dismissible class="mb-4" @dismiss="success = ''">
            {{ success }}
          </AppAlert>
        </Transition>
        <Transition name="fade">
          <AppAlert v-if="apiError" type="error" dismissible class="mb-4" @dismiss="apiError = ''">
            {{ apiError }}
          </AppAlert>
        </Transition>

        <form class="space-y-4" @submit.prevent="handleSubmit">
          <AppPasswordInput
            v-model="form.passwordActual"
            label="Contraseña actual"
            placeholder="Tu contraseña actual"
            :error="errors.passwordActual"
            required
            autocomplete="current-password"
          />
          <AppPasswordInput
            v-model="form.passwordNueva"
            label="Nueva contraseña"
            placeholder="Mínimo 4 caracteres"
            :error="errors.passwordNueva"
            required
            autocomplete="new-password"
          />
          <AppPasswordInput
            v-model="form.passwordConfirm"
            label="Confirmar nueva contraseña"
            placeholder="Repite la nueva contraseña"
            :error="errors.passwordConfirm"
            required
            autocomplete="new-password"
          />
          <div class="flex justify-end pt-1">
            <AppButton type="submit" :loading="loading">
              Actualizar contraseña
            </AppButton>
          </div>
        </form>
      </AppCard>
    </div>
  </DashboardLayout>
</template>
