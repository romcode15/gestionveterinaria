<script setup lang="ts">
/**
 * Estado de página completa — para errores, acceso denegado, 404, etc.
 * Centra el contenido verticalmente en toda la pantalla.
 */
interface Props {
  iconBg?: string
  title: string
  message?: string
}

withDefaults(defineProps<Props>(), {
  iconBg: 'rgba(244,63,94,0.12)',
})
</script>

<template>
  <div
    class="min-h-screen flex items-center justify-center p-4"
    style="background-color: var(--bg-base)"
  >
    <div class="text-center max-w-md">
      <!-- Ícono con fondo circular -->
      <div
        v-if="$slots.icon"
        class="w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6"
        :style="{ backgroundColor: iconBg }"
      >
        <slot name="icon" />
      </div>

      <h1 class="text-2xl font-bold mb-2" style="color: var(--text-primary)">{{ title }}</h1>

      <p v-if="message" class="mb-8" style="color: var(--text-muted)">{{ message }}</p>

      <!-- Acciones opcionales (botones, etc.) -->
      <div v-if="$slots.actions" class="flex gap-3 justify-center">
        <slot name="actions" />
      </div>
    </div>
  </div>
</template>
