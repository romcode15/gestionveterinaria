<script setup lang="ts">
type AlertType = 'success' | 'warning' | 'error' | 'info'

interface Props {
  type?: AlertType
  title?: string
  dismissible?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'info',
  dismissible: false,
})

const emit = defineEmits<{ dismiss: [] }>()

const config: Record<AlertType, { bg: string; border: string; icon: string; iconColor: string; textColor: string }> = {
  success: {
    bg: 'bg-primary-50 dark:bg-primary-900/20',
    border: 'border-primary-200',
    icon: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
    iconColor: 'text-primary-500',
    textColor: 'text-primary-800',
  },
  warning: {
    bg: 'bg-accent-50',
    border: 'border-accent-200',
    icon: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z',
    iconColor: 'text-accent-500',
    textColor: 'text-accent-800',
  },
  error: {
    bg: 'bg-danger-50',
    border: 'border-danger-100',
    icon: 'M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z',
    iconColor: 'text-danger-500',
    textColor: 'text-danger-700',
  },
  info: {
    bg: 'bg-blue-50',
    border: 'border-blue-200',
    icon: 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    iconColor: 'text-blue-500',
    textColor: 'text-blue-800',
  },
}
</script>

<template>
  <div
    :class="[
      'flex gap-3 p-4 rounded-lg border text-sm',
      config[props.type].bg,
      config[props.type].border,
    ]"
    role="alert"
  >
    <svg
      class="w-5 h-5 shrink-0 mt-0.5"
      :class="config[props.type].iconColor"
      fill="none"
      stroke="currentColor"
      viewBox="0 0 24 24"
      aria-hidden="true"
    >
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" :d="config[props.type].icon" />
    </svg>
    <div class="flex-1" :class="config[props.type].textColor">
      <p v-if="props.title" class="font-semibold mb-0.5">{{ props.title }}</p>
      <slot />
    </div>
    <button
      v-if="props.dismissible"
      @click="emit('dismiss')"
      class="shrink-0 p-0.5 rounded opacity-60 hover:opacity-100 transition-opacity"
      :class="config[props.type].textColor"
      aria-label="Cerrar alerta"
    >
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
      </svg>
    </button>
  </div>
</template>
