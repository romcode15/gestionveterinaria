<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import AuthLayout from '@/layouts/AuthLayout.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppInput from '@/components/ui/AppInput.vue'
import AppAlert from '@/components/ui/AppAlert.vue'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({ username: '', password: '' })
const showPassword = ref(false)
const errors = reactive({ username: '', password: '' })

function validate(): boolean {
  errors.username = ''
  errors.password = ''
  let valid = true
  if (!form.username.trim()) { errors.username = 'El usuario es requerido'; valid = false }
  if (!form.password) { errors.password = 'La contraseña es requerida'; valid = false }
  return valid
}

async function handleSubmit() {
  if (!validate()) return
  authStore.clearError()
  try {
    await authStore.login({ username: form.username, password: form.password })
    await router.push('/dashboard')
  } catch {
    // El error ya está en authStore.error
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
        @dismiss="authStore.clearError()"
        class="mb-4"
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

        <!-- Password con toggle manual -->
        <div class="flex flex-col gap-1">
          <label for="password" class="text-sm font-medium" style="color: var(--text-secondary)">
            Contraseña <span class="text-danger-500" aria-hidden="true">*</span>
          </label>
          <div class="relative">
            <input
              id="password"
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="Ingresa tu contraseña"
              autocomplete="current-password"
              :aria-invalid="!!errors.password"
              :class="[
                'vg-input w-full px-3 py-2 pr-10 rounded-lg text-sm transition-colors',
                'focus:outline-none focus:ring-2 focus:ring-offset-0',
                errors.password
                  ? 'border-danger-400 focus:border-danger-400 focus:ring-danger-300'
                  : 'focus:border-primary-500 focus:ring-primary-200',
              ]"
            />
            <button
              type="button"
              @click="showPassword = !showPassword"
              class="absolute right-3 top-1/2 -translate-y-1/2 transition-colors"
              style="color: var(--text-muted)"
              :aria-label="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
            >
              <svg v-if="!showPassword" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
              </svg>
            </button>
          </div>
          <p v-if="errors.password" class="text-xs text-danger-500" role="alert">{{ errors.password }}</p>
        </div>

        <AppButton type="submit" :loading="authStore.loading" full-width size="lg" class="mt-2">
          Ingresar al sistema
        </AppButton>
      </form>

      <!-- Demo credentials -->
      <div class="mt-6 pt-6" style="border-top: 1px solid var(--border-default)">
        <p class="text-xs text-center mb-3" style="color: var(--text-muted)">Usuarios de demostración</p>
        <div class="grid grid-cols-3 gap-2">
          <button
            v-for="demo in demoUsers"
            :key="demo.username"
            @click="fillDemo(demo.username, demo.password)"
            class="text-xs px-2 py-2 rounded-lg transition-colors text-center vg-demo-btn"
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
