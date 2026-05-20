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
  success: 'bg-primary-100 text-primary-700',
  warning: 'bg-accent-100 text-accent-700',
  danger: 'bg-danger-100 text-danger-700',
  info: 'bg-blue-100 text-blue-700',
  neutral: 'bg-slate-100 text-slate-600',
  primary: 'bg-primary-600 text-white',
}

const dotClasses: Record<BadgeVariant, string> = {
  success: 'bg-primary-500',
  warning: 'bg-accent-500',
  danger: 'bg-danger-500',
  info: 'bg-blue-500',
  neutral: 'bg-slate-400',
  primary: 'bg-white',
}

const sizeClasses = {
  sm: 'px-2 py-0.5 text-xs',
  md: 'px-2.5 py-1 text-xs',
}
</script>

<template>
  <span
    :class="[
      'inline-flex items-center gap-1.5 font-medium rounded-full',
      variantClasses[props.variant],
      sizeClasses[props.size],
    ]"
  >
    <span
      v-if="props.dot"
      :class="['w-1.5 h-1.5 rounded-full shrink-0', dotClasses[props.variant]]"
      aria-hidden="true"
    />
    <slot />
  </span>
</template>
