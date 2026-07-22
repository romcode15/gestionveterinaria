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
    await router.push('/dashboard')
  } catch {
    // El error queda en authStore.error
  }
}

const demoUsers = [
  { username: 'admin',      password: 'admin123', label: 'Administrador' },
  { username: 'dra.garcia', password: 'vet123',   label: 'Veterinaria' },
  { username: 'recepcion',  password: 'rec123',   label: 'Recepcionista' },
]

function fillDemo(username: string, password: string) {
  form.username = username
  form.password = password
  authStore.clearError()
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

      <!-- Usuarios de demostración -->
      <div class="mt-6 pt-6" style="border-top: 1px solid var(--border-default)">
        <p class="text-xs text-center mb-3" style="color: var(--text-muted)">Usuarios de demostración</p>
        <div class="grid grid-cols-3 gap-2">
          <button
            v-for="demo in demoUsers"
            :key="demo.username"
            type="button"
            class="vg-demo-btn text-xs px-2 py-2 rounded-lg transition-colors text-center"
            @click="fillDemo(demo.username, demo.password)"
          >
            {{ demo.label }}
          </button>
        </div>
      </div>
    </div>
  </AuthLayout>
</template>

<style>
.vg-demo-btn {
  border: 1px solid var(--border-default);
  color: var(--text-muted);
  background: transparent;
}
.vg-demo-btn:hover {
  border-color: #34d399;
  background-color: rgba(5, 150, 105, 0.08);
  color: #059669;
}
[data-theme="dark"] .vg-demo-btn:hover {
  border-color: #34d399;
  background-color: rgba(52, 211, 153, 0.12);
  color: #34d399;
}
</style>
