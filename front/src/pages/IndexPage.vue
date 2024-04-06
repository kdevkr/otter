<template>
  <q-page class="flex flex-center">
    <img
      alt="Quasar logo"
      src="~assets/quasar-logo-vertical.svg"
      style="width: 200px; height: 200px"
    >
    <div class="flex-col">
      <q-btn @click="onToggleTheme">
        Change to {{$q.dark.isActive ? 'White' : 'Dark'}}
      </q-btn>
    </div>
  </q-page>
</template>

<script setup>
import {Notify, useQuasar} from "quasar";

defineOptions({
  name: 'IndexPage'
});

const $q = useQuasar()
const onToggleTheme = () => {
  $q.dark.toggle()
  Notify.create(`Theme: ${$q.dark.isActive ? 'Dark' : 'White'}`)
}
</script>
