<!-- components/common/EntityCard.vue -->
<script setup lang="ts">
import AppBadge from '@/components/ui/AppBadge.vue'

interface Status {
  label: string
  variant?: 'success' | 'warning' | 'danger' | 'info' | 'neutral' | 'primary'
  dot?: boolean
}

interface Props {
  title: string
  subtitle?: string
  status?: Status
  readonly?: boolean
}

withDefaults(defineProps<Props>(), {
  readonly: false,
})
</script>

<template>
  <div class="vg-card rounded-2xl p-4 sm:p-5 hover:shadow-md transition-shadow">
    <div class="flex items-start gap-3 sm:gap-4">
      <!-- Slot para avatar (imagen, iniciales, MascotaAvatar, etc.) -->
      <div v-if="$slots.avatar" class="shrink-0">
        <slot name="avatar" />
      </div>

      <div class="flex-1 min-w-0">
        <!-- Encabezado: título + badge -->
        <div class="flex flex-wrap items-start justify-between gap-x-2 gap-y-1">
          <div class="min-w-0">
            <h3 class="font-semibold truncate" style="color: var(--text-primary)">
              {{ title }}
            </h3>
            <p v-if="subtitle" class="text-xs mt-0.5 truncate" style="color: var(--text-muted)">
              {{ subtitle }}
            </p>
          </div>
          <AppBadge
            v-if="status"
            :variant="status.variant || 'neutral'"
            :dot="status.dot ?? false"
            class="shrink-0"
          >
            {{ status.label }}
          </AppBadge>
        </div>

        <!-- Slot para detalles (datos específicos de la entidad) -->
        <div class="mt-3">
          <slot name="details" />
        </div>

        <!-- Pie: acciones o información extra -->
        <div
          v-if="$slots.actions || !readonly"
          class="mt-3 pt-3 flex items-center justify-between gap-2"
          style="border-top: 1px solid var(--border-default)"
        >
          <slot name="actions">
            <!-- Si readonly, no mostrar nada; si no, mostrar botón editar por defecto -->
            <template v-if="!readonly">
              <div class="flex-1"></div>
              <button
                class="p-1.5 rounded-lg transition-colors vg-icon-btn"
                title="Editar"
                aria-label="Editar"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
              </button>
            </template>
          </slot>
        </div>
      </div>
    </div>
  </div>
</template>