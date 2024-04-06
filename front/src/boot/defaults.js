import {Dark, LoadingBar, Notify} from 'quasar'

Notify.setDefaults({
  position: 'bottom',
  timeout: 2500,
  color: 'primary',
  textColor: 'white',
  actions: [{ icon: 'close', color: 'white' }]
})

LoadingBar.setDefaults({
  color: 'primary',
  size: '10px',
  position: 'bottom'
})

Dark.set("auto")
