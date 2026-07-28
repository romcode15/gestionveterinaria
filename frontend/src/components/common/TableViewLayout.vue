<script setup lang="ts">
/**
 * TableViewLayout — contenedor de dos bloques para vistas de listado.
 *
 * Estructura:
 *   ┌─────────────────────────────┐  ← shrink-0  (toolbar: filtros, botón nuevo)
 *   │  slot #toolbar              │
 *   ├─────────────────────────────┤  ← flex-1 overflow-hidden (tabla + paginación)
 *   │  slot #content              │
 *   └─────────────────────────────┘
 *
 * Uso:
 *   <TableViewLayout>
 *     <template #toolbar>
 *       <SearchToolbar ... />
 *     </template>
 *     <template #content>
 *       <AppCard fill-height padding="none">
 *         ...tabla...
 *         <template #footer><AppPagination .../></template>
 *       </AppCard>
 *     </template>
 *   </TableViewLayout>
 */
</script>

<template>
  <div class="flex flex-col h-full gap-4 p-6 min-h-0">
    <!-- Toolbar: siempre visible, no hace scroll -->
    <div class="shrink-0">
      <slot name="toolbar" />
    </div>

    <!-- Contenido: ocupa el resto y puede hacer scroll -->
    <div class="flex-1 flex flex-col min-h-0 overflow-hidden">
      <slot name="content" />
    </div>
  </div>
</template>
