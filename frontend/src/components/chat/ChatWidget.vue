<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import { api } from '@/services/api'

interface Mensaje {
  rol: 'usuario' | 'asistente'
  texto: string
  error?: boolean
}

const abierto    = ref(false)
const pregunta   = ref('')
const mensajes   = ref<Mensaje[]>([])
const cargando   = ref(false)
const contenedor = ref<HTMLElement | null>(null)
const inputRef   = ref<HTMLInputElement | null>(null)

const sugerencias = [
  '¿Cuántos clientes activos hay?',
  '¿Cuántas citas hay esta semana?',
  '¿Qué mascotas están registradas?',
  '¿Cuántos médicos están disponibles?',
]

function toggleChat() {
  abierto.value = !abierto.value
  if (abierto.value && mensajes.value.length === 0) {
    mensajes.value.push({
      rol: 'asistente',
      texto: '¡Hola! Soy tu asistente de datos. Puedes preguntarme cualquier cosa sobre clientes, mascotas, citas o médicos.',
    })
    nextTick(() => inputRef.value?.focus())
  }
}

async function enviar(texto?: string) {
  const q = (texto ?? pregunta.value).trim()
  if (!q || cargando.value) return

  mensajes.value.push({ rol: 'usuario', texto: q })
  pregunta.value = ''
  cargando.value = true
  scrollAbajo()

  try {
    const res = await api.post<{ respuesta: string; exitoso: boolean }>(
      '/api/chat', { pregunta: q }
    )
    mensajes.value.push({
      rol: 'asistente',
      texto: res.respuesta,
      error: !res.exitoso,
    })
  } catch {
    mensajes.value.push({
      rol: 'asistente',
      texto: 'No pude conectarme con el asistente.',
      error: true,
    })
  } finally {
    cargando.value = false
    scrollAbajo()
    nextTick(() => inputRef.value?.focus())
  }
}

function scrollAbajo() {
  nextTick(() => {
    if (contenedor.value) {
      contenedor.value.scrollTop = contenedor.value.scrollHeight
    }
  })
}

watch(mensajes, scrollAbajo, { deep: true })

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    enviar()
  }
}

function limpiar() {
  mensajes.value = [{
    rol: 'asistente',
    texto: '¡Hola! Soy tu asistente de datos. Puedes preguntarme cualquier cosa sobre clientes, mascotas, citas o médicos.',
  }]
}
</script>

<template>
  <!-- Botón flotante -->
  <div class="fixed bottom-6 right-6 z-50 flex flex-col items-end gap-3">

    <!-- Ventana del chat -->
    <Transition name="chat-slide">
      <div
        v-if="abierto"
        class="vg-card rounded-2xl shadow-2xl flex flex-col overflow-hidden"
        style="width: 360px; height: 520px; border: 1px solid var(--border-default);"
        role="dialog"
        aria-label="Asistente de datos"
      >
        <!-- Header -->
        <div class="flex items-center justify-between px-4 py-3 shrink-0 bg-primary-600 text-white rounded-t-2xl">
          <div class="flex items-center gap-2">
            <svg class="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
            </svg>
            <div>
              <p class="font-semibold text-sm leading-tight">Asistente IA</p>
            </div>
          </div>
          <div class="flex items-center gap-1">
            <button
              class="p-1.5 rounded-lg hover:bg-white/20 transition-colors"
              title="Limpiar conversación"
              aria-label="Limpiar conversación"
              @click="limpiar"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
            <button
              class="p-1.5 rounded-lg hover:bg-white/20 transition-colors"
              title="Cerrar"
              aria-label="Cerrar chat"
              @click="toggleChat"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <!-- Mensajes -->
        <div
          ref="contenedor"
          class="flex-1 overflow-y-auto p-4 space-y-3"
          style="background: var(--bg-surface)"
        >
          <!-- Sugerencias iniciales -->
          <div v-if="mensajes.length <= 1" class="flex flex-wrap gap-2 mt-1">
            <button
              v-for="s in sugerencias"
              :key="s"
              class="text-xs px-3 py-1.5 rounded-full border border-primary-600 text-primary-600 transition-colors hover:bg-primary-50"
              @click="enviar(s)"
            >
              {{ s }}
            </button>
          </div>

          <div
            v-for="(msg, i) in mensajes"
            :key="i"
            class="flex"
            :class="msg.rol === 'usuario' ? 'justify-end' : 'justify-start'"
          >
            <div
              class="max-w-[85%] rounded-2xl px-3 py-2 text-sm"
              :class="msg.rol === 'usuario'
                ? 'rounded-br-sm bg-primary-600 text-white'
                : msg.error
                  ? 'rounded-bl-sm border border-red-300 bg-red-50 text-red-800 dark:bg-red-950 dark:text-red-200 dark:border-red-800'
                  : 'rounded-bl-sm bg-gray-100 text-gray-900 dark:bg-gray-700 dark:text-gray-100'"
            >
              <p style="white-space: pre-wrap; word-break: break-word;">{{ msg.texto }}</p>
            </div>
          </div>

          <!-- Indicador de carga -->
          <div v-if="cargando" class="flex justify-start">
            <div
              class="rounded-2xl rounded-bl-sm px-4 py-3"
              style="background: var(--bg-surface-2);"
            >
              <div class="flex gap-1 items-center">
                <span
                  v-for="n in 3" :key="n"
                  class="w-1.5 h-1.5 rounded-full animate-bounce bg-primary-600"
                  :style="{ animationDelay: `${(n - 1) * 0.15}s` }"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- Input -->
        <div
          class="px-3 py-3 shrink-0 flex gap-2"
          style="border-top: 1px solid var(--border-default); background: var(--bg-surface);"
        >
          <input
            ref="inputRef"
            v-model="pregunta"
            type="text"
            placeholder="Escribe tu pregunta..."
            class="flex-1 text-sm rounded-xl px-3 py-2 outline-none"
            style="background: var(--bg-surface-2); color: var(--text-primary);
                   border: 1px solid var(--border-default);"
            :disabled="cargando"
            maxlength="500"
            @keydown="onKeydown"
          />
          <button
            class="rounded-xl px-3 py-2 transition-opacity disabled:opacity-40 bg-primary-600 hover:bg-primary-700 text-white"
            :disabled="!pregunta.trim() || cargando"
            aria-label="Enviar"
            @click="enviar()"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
            </svg>
          </button>
        </div>
      </div>
    </Transition>

    <!-- Botón toggle -->
    <button
      class="w-14 h-14 rounded-full shadow-lg flex items-center justify-center transition-transform hover:scale-110 active:scale-95 bg-primary-600 hover:bg-primary-700 text-white"
      :title="abierto ? 'Cerrar asistente' : 'Abrir asistente IA'"
      :aria-label="abierto ? 'Cerrar asistente' : 'Abrir asistente IA'"
      @click="toggleChat"
    >
      <Transition name="fade" mode="out-in">
        <svg v-if="!abierto" key="open" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
        </svg>
        <svg v-else key="close" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </Transition>
    </button>
  </div>
</template>

<style scoped>
.chat-slide-enter-active,
.chat-slide-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.chat-slide-enter-from,
.chat-slide-leave-to {
  opacity: 0;
  transform: translateY(12px) scale(0.97);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
