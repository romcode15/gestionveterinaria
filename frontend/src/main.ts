import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'

import App from './App.vue'
import router from './router'

// Componentes globales
import AppCard from '@/components/ui/AppCard.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppSearchInput from '@/components/ui/AppSearchInput.vue'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.component('AppCard', AppCard)
app.component('AppButton', AppButton)
app.component('AppSearchInput', AppSearchInput)

app.mount('#app')
