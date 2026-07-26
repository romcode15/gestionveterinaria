<script setup lang="ts">
interface Props {
  title?: string
  message?: string
}

withDefaults(defineProps<Props>(), {
  title: 'No hay datos',
  message: 'No se encontraron elementos para mostrar.',
})
</script>

<template>
  <div class="flex flex-col items-center justify-center py-12 gap-3 text-center">
    <!-- Icono: preferir slot SVG, fallback al ícono genérico -->
    <div class="w-12 h-12 flex items-center justify-center rounded-full"
         style="background: var(--bg-surface-2);">
      <slot name="icon">
        <!-- Ícono genérico de bandeja vacía -->
        <svg class="w-6 h-6" style="color: var(--text-muted)" fill="none"
             stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
        </svg>
      </slot>
    </div>
    <h3 class="text-lg font-semibold" style="color: var(--text-primary)">{{ title }}</h3>
    <p class="text-sm max-w-sm" style="color: var(--text-muted)">{{ message }}</p>
    <slot name="actions" />
  </div>
</template>
