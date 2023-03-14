<script setup lang="ts">
import type {Taints} from "@/types";
import {computed, ref} from "vue";

const props = defineProps<{ name: string, taints: Taints }>()
const emits = defineEmits<{ (event: 'update', taints: Taints): void }>()


const open = ref<boolean>(false)

const fixedDelayEnabled = ref<boolean>(false)
const fixedDelayAmount = ref<number>(10)
const randomDelayEnabled = ref<boolean>(false)
const randomDelayLowerAmount = ref<number>(10)
const randomDelayHigherAmount = ref<number>(20)
const errorRateEnabled = ref<boolean>(false)
const errorRateRate = ref<number>(25)
const errorRateRuntime = ref<boolean>(true)

const openSettings = () => {
  if (open.value) {
    return
  }
  fixedDelayEnabled.value = !!props.taints.fixedDelay
  fixedDelayAmount.value = props.taints.fixedDelay?.delay || 10
  randomDelayEnabled.value = !!props.taints.randomDelay
  randomDelayLowerAmount.value = props.taints.randomDelay?.lowerBound || 10
  randomDelayHigherAmount.value = props.taints.randomDelay?.higherBound || 20
  errorRateEnabled.value = !!props.taints.errorRate
  errorRateRate.value = (props.taints.errorRate?.rate || 0.25) * 100
  errorRateRuntime.value = props.taints.errorRate?.runtimeException === false
  open.value = true
}
const closeSettings = () => {
  open.value = false
}
const save = () => {
  const fixedDelayTaint = fixedDelayEnabled.value ? {delay: fixedDelayAmount.value} : undefined
  const randomDelayTaint = randomDelayEnabled.value ? {
    higherBound: randomDelayHigherAmount.value,
    lowerBound: randomDelayLowerAmount.value
  } : undefined
  const errorRateTaint = errorRateEnabled.value ? {
    rate: errorRateRate.value / 100,
    runtimeException: errorRateRuntime.value
  } : undefined
  emits('update', {fixedDelay: fixedDelayTaint, randomDelay: randomDelayTaint, errorRate: errorRateTaint})
  open.value = false
}

const taintString = computed(() => {
  const items = []
  if (props.taints.fixedDelay) {
    items.push("Delay: " + props.taints.fixedDelay.delay + 'ms')
  }
  if (props.taints.randomDelay) {
    items.push("Delay: " + props.taints.randomDelay.lowerBound + '-' + props.taints.randomDelay.higherBound + 'ms')
  }
  if(props.taints.errorRate) {
    items.push(`Errors: ${props.taints.errorRate.rate * 100}% ${!props.taints.errorRate.runtimeException ? 'Checked' : 'Runtime'}`)
  }
  if (items.length === 0) {
    return ''
  }
  return "(" + items.join(", ") + ")"
})
</script>

<template>
  <div :class="`taint-chooser mb-2 border rounded p-2 ${open ? 'taint-chooser-open' : 'taint-chooser-closed'}`"
       @click="openSettings">
    <div :class="`preview d-flex`" v-if="!open">
      <div class="flex-grow-1">
        {{name}}
      </div>
      <div>{{ taintString }}</div>
    </div>
    <div class="chooser" v-if="open">
      <form @submit.prevent="save">
        <h3 class="text-center" @click.prevent.stop="closeSettings">{{ name }}</h3>
        <table class="table table-borderless table-sm">
          <tbody>
          <tr>
            <th rowspan="2">Fixed Delay</th>
            <td>Enabled</td>
            <td><input class="d-inline mx-2" type="checkbox" id="flexCheckDefault" v-model="fixedDelayEnabled">
            </td>
          </tr>
          <tr>
            <td>Delay</td>
            <td>
              <div class="input-group" style="width: 150px">
                <input type="number" class="form-control" placeholder="50" :required="fixedDelayEnabled"
                       v-model="fixedDelayAmount">
                <span class="input-group-text" id="basic-addon2">ms</span>
              </div>
            </td>
          </tr>
          <tr>
            <th rowspan="3">Random Delay</th>
            <td>Enabled</td>
            <td><input class="d-inline mx-2" type="checkbox" value="" id="flexCheckDefault"
                       v-model="randomDelayEnabled"></td>
          </tr>
          <tr>
            <td>Min Delay</td>
            <td>
              <div class="input-group" style="width: 150px">
                <input type="number" class="form-control" placeholder="50" :required="randomDelayEnabled"
                       v-model="randomDelayLowerAmount">
                <span class="input-group-text" id="basic-addon2">ms</span>
              </div>
            </td>
          </tr>
          <tr>
            <td>Max Delay</td>
            <td>
              <div class="input-group" style="width: 150px">
                <input type="number" class="form-control" placeholder="50" :required="randomDelayEnabled"
                       v-model="randomDelayHigherAmount">
                <span class="input-group-text" id="basic-addon2">ms</span>
              </div>
            </td>
          </tr>
          <tr>
            <th rowspan="3">Error Rate</th>
            <td>Enabled</td>
            <td><input class="d-inline mx-2" type="checkbox" value="" id="flexCheckDefault" v-model="errorRateEnabled">
            </td>
          </tr>
          <tr>
            <td>Rate</td>
            <td>
              <div class="input-group" style="width: 150px">
                <input type="number" class="form-control" placeholder="50" :required="errorRateEnabled"
                       v-model="errorRateRate">
                <span class="input-group-text" id="basic-addon2">%</span>
              </div>
            </td>
          </tr>
          <tr>
            <td>Checked Exception</td>
            <td><input class="d-inline mx-2" type="checkbox" value="" id="flexCheckDefault" v-model="errorRateRuntime">
            </td>
          </tr>
          </tbody>
        </table>

        <div class="d-flex">
          <div class="flex-grow-1">
            <button type="button" class="btn btn-outline-dark" @click.prevent.stop="closeSettings">Cancel</button>
          </div>
          <button type="submit" class="btn btn-secondary">Save taints</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.taint-chooser {
  cursor: pointer
}

.taint-chooser-closed:hover {
  background-color: rgba(0, 0, 0, 0.2)
}
</style>
