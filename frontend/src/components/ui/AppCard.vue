<script setup lang="ts">
interface Props {
  padding?:    'none' | 'sm' | 'md' | 'lg'
  shadow?:     boolean
  hover?:      boolean
  /**
   * fillHeight: la card ocupa todo el espacio vertical disponible (flex-1)
   * y distribuye su interior en flex-col, permitiendo que el slot #table
   * crezca con scroll y el slot #footer (paginación) quede fijo abajo.
   * No afecta el comportamiento por defecto (sin esta prop).
   */
  fillHeight?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  padding:    'md',
  shadow:     true,
  hover:      false,
  fillHeight: false,
})

const paddingClasses: Record<string, string> = {
  none: '',
  sm:   'p-4',
  md:   'p-6',
  lg:   'p-8',
}
</script>

<template>
  <!-- Modo normal — comportamiento sin cambios -->
  <div
    v-if="!fillHeight"
    :class="[
      'vg-card rounded-2xl',
      props.hover ? 'hover:shadow-md hover:-translate-y-0.5 transition-all duration-200 cursor-pointer' : '',
      paddingClasses[props.padding],
    ]"
  >
    <slot />
  </div>

  <!-- Modo fillHeight — ocupa el espacio vertical y gestiona scroll interno -->
  <div
    v-else
    :class="[
      'vg-card rounded-2xl flex flex-col min-h-0',
      props.hover ? 'hover:shadow-md transition-all duration-200 cursor-pointer' : '',
    ]"
    style="flex: 1 1 0; overflow: hidden;"
  >
    <!-- Cabecera opcional (título, contador) — no hace scroll -->
    <slot name="header" />

    <!-- Área de tabla — crece y hace scroll -->
    <div class="flex-1 overflow-y-auto min-h-0">
      <slot />
    </div>

    <!-- Paginación u otro footer — siempre visible -->
    <div v-if="$slots.footer" class="shrink-0">
      <slot name="footer" />
    </div>
  </div>
</template>
