import { ref, watch, onUnmounted } from 'vue'

interface UseFiltrosOptions {
  /** Función que se llama cuando algún filtro cambia — recibe page=0 siempre */
  onCargar: (page: number) => void
  /** Tiempo de espera para el debounce del campo de texto (ms). Default: 400 */
  debounceMs?: number
}

/**
 * Composable que centraliza el patrón de filtros reactivos con debounce.
 *
 * Uso:
 *   const { busqueda, filtros, setFiltro } = useFiltros({
 *     onCargar: (page) => store.cargar({ page }),
 *   })
 *
 * - busqueda: ref<string> — campo de texto principal (debounceado)
 * - filtros: Record<string, string> — filtros adicionales (select, toggle, etc.)
 * - setFiltro(key, value): actualiza un filtro adicional e invoca onCargar inmediatamente
 */
export function useFiltros(options: UseFiltrosOptions) {
  const { onCargar, debounceMs = 400 } = options

  const busqueda = ref('')
  const filtros  = ref<Record<string, string>>({})

  let timer: ReturnType<typeof setTimeout> | null = null

  // Campo de texto — debounceado, solo dispara si el usuario escribe (no en mount)
  watch(busqueda, () => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => onCargar(0), debounceMs)
  })

  // Filtros adicionales (selects, toggles) — respuesta inmediata
  function setFiltro(key: string, value: string) {
    filtros.value[key] = value
    onCargar(0)
  }

  function resetFiltros() {
    busqueda.value = ''
    filtros.value  = {}
    onCargar(0)
  }

  onUnmounted(() => {
    if (timer) clearTimeout(timer)
  })

  return { busqueda, filtros, setFiltro, resetFiltros }
}
