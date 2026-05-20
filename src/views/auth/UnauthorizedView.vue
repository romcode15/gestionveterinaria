<script setup lang="ts">
import { useRouter } from 'vue-router'
import AppButton from '@/components/ui/AppButton.vue'

const router = useRouter()
</script>

<template>
  <div class="min-h-screen bg-slate-50 flex items-center justify-center p-4">
    <div class="text-center max-w-md">
      <div class="w-20 h-20 bg-danger-100 rounded-full flex items-center justify-center mx-auto mb-6">
        <svg class="w-10 h-10 text-danger-500" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-slate-800 mb-2">Acceso no autorizado</h1>
      <p class="text-slate-500 mb-8">
        No tienes permisos para acceder a esta sección. Contacta al administrador si crees que esto es un error.
      </p>
      <div class="flex gap-3 justify-center">
        <AppButton variant="ghost" @click="router.back()">Volver</AppButton>
        <AppButton @click="router.push('/dashboard')">Ir al inicio</AppButton>
      </div>
    </div>
  </div>
</template>
