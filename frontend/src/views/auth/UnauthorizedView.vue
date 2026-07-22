<script setup lang="ts">
import { useRouter } from 'vue-router'
import AppButton from '@/components/ui/AppButton.vue'
import FullPageState from '@/components/common/FullPageState.vue'

const router = useRouter()
</script>

<template>
  <FullPageState
    title="Acceso no autorizado"
    message="No tienes permisos para acceder a esta sección. Contacta al administrador si crees que esto es un error."
    icon-bg="rgba(244,63,94,0.12)"
  >
    <template #icon>
      <svg
        class="w-10 h-10 text-danger-500"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
        aria-hidden="true"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="1.5"
          d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
        />
      </svg>
    </template>

    <template #actions>
      <AppButton variant="ghost" @click="router.back()">Volver</AppButton>
      <AppButton @click="router.push('/dashboard')">Ir al inicio</AppButton>
    </template>
  </FullPageState>
</template>
