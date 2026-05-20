<script setup lang="ts">
</script>

<template>
  <div class="min-h-screen bg-linear-to-br from-primary-800 via-primary-700 to-secondary-700 flex items-center justify-center p-4">
    <!-- Decoración de fondo -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none" aria-hidden="true">
      <div class="absolute -top-40 -right-40 w-96 h-96 bg-white/5 rounded-full blur-3xl" />
      <div class="absolute -bottom-40 -left-40 w-96 h-96 bg-white/5 rounded-full blur-3xl" />
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-150 h-150 bg-white/3 rounded-full blur-3xl" />
    </div>

    <div class="relative w-full max-w-md">
      <!-- Logo / Branding -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-white/20 backdrop-blur-sm rounded-2xl mb-4 shadow-lg">
          <svg class="w-9 h-9 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
              d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
          </svg>
        </div>
        <h1 class="text-2xl font-bold text-white">Gestión Veterinaria</h1>
        <p class="text-primary-200 text-sm mt-1">Sistema de administración clínica</p>
      </div>

      <!-- Contenido del formulario -->
      <div class="bg-white rounded-2xl shadow-2xl overflow-hidden">
        <slot />
      </div>

      <p class="text-center text-primary-300 text-xs mt-6">
        © 2026 GestionVeterinaria · Todos los derechos reservados
      </p>
    </div>
  </div>
</template>
