import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

type Theme = 'light' | 'dark'

const STORAGE_KEY = 'vg-theme'

export const useThemeStore = defineStore('theme', () => {
  // Leer preferencia guardada, o usar la preferencia del sistema como fallback
  function getInitialTheme(): Theme {
    const stored = localStorage.getItem(STORAGE_KEY) as Theme | null
    if (stored === 'light' || stored === 'dark') return stored
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
  }

  const theme = ref<Theme>(getInitialTheme())

  function applyTheme(t: Theme) {
    document.documentElement.setAttribute('data-theme', t)
  }

  function toggle() {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
  }

  function setTheme(t: Theme) {
    theme.value = t
  }

  // Aplicar en cada cambio y persistir
  watch(
    theme,
    (t) => {
      applyTheme(t)
      localStorage.setItem(STORAGE_KEY, t)
    },
    { immediate: true },
  )

  const isDark = () => theme.value === 'dark'

  return { theme, toggle, setTheme, isDark }
})
