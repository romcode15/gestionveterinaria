<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import AuthLayout from '@/layouts/AuthLayout.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppPasswordInput from '@/components/ui/AppPasswordInput.vue'
import AppAlert from '@/components/ui/AppAlert.vue'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({ username: '', password: '' })
const errors = reactive({ username: '', password: '' })

function validate(): boolean {
  errors.username = ''
  errors.password = ''
  let valid = true
  if (!form.username.trim()) { errors.username = 'El usuario es requerido'; valid = false }
  if (!form.password)        { errors.password = 'La contraseña es requerida'; valid = false }
  return valid
}

async function handleSubmit() {
  if (!validate()) return
  authStore.clearError()
  try {
    await authStore.login({ username: form.username, password: form.password })

    // Redirigir al portal correcto según el rol
    if (authStore.isCliente) {
      await router.push('/mi-portal')
    } else if (authStore.isMedico) {
      await router.push('/mi-agenda')
    } else {
      await router.push('/dashboard')
    }
  } catch {
    // El error queda en authStore.error
  }
}

</script>

<template>
  <AuthLayout>
    <div class="p-8">
      <div class="mb-6">
        <h2 class="text-xl font-bold" style="color: var(--text-primary)">Iniciar sesión</h2>
        <p class="text-sm mt-1" style="color: var(--text-muted)">Ingresa tus credenciales para continuar</p>
      </div>

      <AppAlert
        v-if="authStore.error"
        type="error"
        dismissible
        class="mb-4"
        @dismiss="authStore.clearError()"
      >
        {{ authStore.error }}
      </AppAlert>

      <form @submit.prevent="handleSubmit" novalidate class="space-y-4">
        <AppInput
          id="username"
          v-model="form.username"
          label="Usuario"
          placeholder="Ingresa tu usuario"
          :error="errors.username"
          required
          autocomplete="username"
        />

        <AppPasswordInput
          id="password"
          v-model="form.password"
          label="Contraseña"
          placeholder="Ingresa tu contraseña"
          :error="errors.password"
          required
          autocomplete="current-password"
        />

        <AppButton type="submit" :loading="authStore.loading" full-width size="lg" class="mt-2">
          Ingresar al sistema
        </AppButton>
      </form>

    </div>
  </AuthLayout>
</template>

