<template>
  <div class="flex flex-col items-center justify-center py-12 gap-3">
    <div class="w-8 h-8 border-4 border-t-transparent rounded-full animate-spin" style="border-color: var(--color-primary); border-top-color: transparent;"></div>
    <p class="text-sm" style="color: var(--text-muted)">
      <slot>Cargando...</slot>
    </p>
  </div>
</template>