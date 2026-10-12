import Vue from 'vue'
import App from './App.vue'
import './styles/reset.css'
import router from './router'
import store from './store'
import '@/utils/element-ui'

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
