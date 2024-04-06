<template>
  <router-view />
</template>

<script setup>
import {LoadingBar} from 'quasar'

defineOptions({
  name: 'App',
  preFetch() {
    LoadingBar.start(100)

    return new Promise(resolve => {
      setTimeout(() => resolve(), 2000)
    }).then(() => {
      LoadingBar.stop()
    })
  }
});


</script>
