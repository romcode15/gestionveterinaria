<script setup lang="ts">
type BadgeVariant = 'success' | 'warning' | 'danger' | 'info' | 'neutral' | 'primary'

interface Props {
  variant?: BadgeVariant
  size?: 'sm' | 'md'
  dot?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'neutral',
  size: 'md',
  dot: false,
})

const variantClasses: Record<BadgeVariant, string> = {
  success: 'badge-success',
  warning: 'badge-warning',
  danger:  'badge-danger',
  info:    'badge-info',
  neutral: 'badge-neutral',
  primary: 'badge-primary',
}

const sizeClasses = {
  sm: 'px-2 py-0.5 text-xs',
  md: 'px-2.5 py-1 text-xs',
}
</script>

<template>
  <span
    :class="[
      'inline-flex items-center gap-1.5 font-medium rounded-full transition-colors',
      variantClasses[props.variant],
      sizeClasses[props.size],
    ]"
  >
    <span
      v-if="props.dot"
      class="w-1.5 h-1.5 rounded-full shrink-0 badge-dot"
      aria-hidden="true"
    />
    <slot />
  </span>
</template>
