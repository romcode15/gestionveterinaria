<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'

interface Props {
  modelValue: boolean
  title?: string
  size?: 'sm' | 'md' | 'lg' | 'xl'
  closable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  size: 'md',
  closable: true,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  close: []
}>()

function close() {
  if (props.closable) {
    emit('update:modelValue', false)
    emit('close')
  }
}

function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Escape') close()
}

onMounted(() => document.addEventListener('keydown', handleKeydown))
onUnmounted(() => document.removeEventListener('keydown', handleKeydown))

const sizeClasses = {
  sm: 'max-w-sm',
  md: 'max-w-lg',
  lg: 'max-w-2xl',
  xl: 'max-w-4xl',
}
</script>

<template>
  <Teleport to="body">
  <Transition name="fade">
      <div
        v-if="props.modelValue"
        class="fixed inset-0 z-50 flex items-center justify-center p-4"
        role="dialog"
        aria-modal="true"
        :aria-labelledby="props.title ? 'modal-title' : undefined"
      >
        <!-- Backdrop — pointer-events-none durante fade-out -->
        <div
          class="absolute inset-0 bg-black/60 backdrop-blur-sm"
          @click="close"
          aria-hidden="true"
        />

        <!-- Panel -->
        <Transition name="slide-up">
          <div
            v-if="props.modelValue"
            :class="[
              'relative w-full vg-modal-panel rounded-2xl shadow-2xl flex flex-col max-h-[90vh]',
              sizeClasses[props.size],
            ]"
          >
            <!-- Header -->
            <div
              v-if="props.title || $slots.header"
              class="flex items-center justify-between px-6 py-4"
              style="border-bottom: 1px solid var(--border-default)"
            >
              <slot name="header">
                <h2 id="modal-title" class="text-lg font-semibold" style="color: var(--text-primary)">
                  {{ props.title }}
                </h2>
              </slot>
              <button
                v-if="props.closable"
                @click="close"
                class="p-1.5 rounded-lg transition-colors"
                style="color: var(--text-muted)"
                aria-label="Cerrar modal"
                onmouseover="this.style.backgroundColor='var(--bg-surface-2)';this.style.color='var(--text-secondary)'"
                onmouseout="this.style.backgroundColor='';this.style.color='var(--text-muted)'"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <!-- Body -->
            <div class="flex-1 overflow-y-auto px-6 py-4">
              <slot />
            </div>

            <!-- Footer -->
            <div
              v-if="$slots.footer"
              class="px-6 py-4 flex items-center justify-end gap-3"
              style="border-top: 1px solid var(--border-default)"
            >
              <slot name="footer" />
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>
