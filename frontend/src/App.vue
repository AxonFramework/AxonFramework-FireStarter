<script setup lang="ts">

import {ref} from "vue";
import TaintChooser from "@/components/TaintChooser.vue";

const loading = ref<boolean>(false)
const settings = ref<any>(null)
const getSettings = async () => {
  const r = await fetch('/fire-starter/settings', {method: 'GET'})
  if (r.ok) {
    settings.value = await r.json()
  } else {
    setTimeout(() => getSettings(), 1000)
  }
  loading.value = false
}
getSettings()

const save = async () => {
  const r = await fetch('/fire-starter/settings', {
    method: 'POST',
    body: JSON.stringify(settings.value),
    headers: {'Content-Type': 'application/json'}
  })
  if (r.ok) {
    await getSettings()
  }
}
</script>

<template>
  <div class="container-fluid">
    <nav class="navbar navbar-expand-lg navbar-light bg-white">
      <div class="container-fluid px-3">
        <a class="navbar-brand" href="/"><img src="./assets/logo.svg" alt="" width="30" height="24"> AxonFramework
          FireStarter</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
      </div>
    </nav>

    <div class="bg-white mt-4 rounded m-4 p-4">
      <div class="col-md-6 offset-3 text-center bg-gradient px-2 py-5" v-if="loading">
        <h4>Loading...</h4>
        <div class="spinner-border" role="status">
          <span class="visually-hidden"></span>
        </div>
      </div>


      <div v-else-if="!settings">
        <div class="col-md-6 offset-3 text-center bg-gradient px-2 py-5">
          <h4>Was unable to load current settings</h4>
          <p>We will retry every second.</p>
        </div>
      </div>

      <div v-else>
        <div class="row">
          <div class="col-md-4">
            <h2 class="text-secondary">Events</h2>

            <TaintChooser name="Read Aggregate Stream" :taints="settings.events.readAggregateStream" @update="(update) => {
              settings.events.readAggregateStream = update
              save()
            }"/>
            <TaintChooser name="Commit events" :taints="settings.events.commitEvents" @update="(update) => {
              settings.events.commitEvents = update
              save()
            }"/>
            <TaintChooser name="Store Snapshot" :taints="settings.events.storeSnapshot" @update="(update) => {
              settings.events.storeSnapshot = update
              save()
            }"/>
            <TaintChooser name="Open Stream" :taints="settings.events.openStream" @update="(update) => {
              settings.events.openStream = update
              save()
            }"/>
            <TaintChooser name="Event Handlers" :taints="settings.events.handlers" @update="(update) => {
              settings.events.handlers = update
              save()
            }"/>
          </div>
          <div class="col-md-4">
            <h2 class="text-secondary">Commands</h2>

            <TaintChooser name="Aggregate Lock Time" :taints="settings.command.lockTime" @update="(update) => {
              settings.command.lockTime = update
              save()
            }"/>
            <TaintChooser name="Command Dispatch" :taints="settings.command.dispatch" @update="(update) => {
              settings.command.dispatch = update
              save()
            }"/>
            <TaintChooser name="Command Handlers" :taints="settings.command.handlers" @update="(update) => {
              settings.command.handlers = update
              save()
            }"/>
            <TaintChooser name="Aggregate Repository Load" :taints="settings.command.repositoryLoad" @update="(update) => {
              settings.command.repositoryLoad = update
              save()
            }"/>

          </div>
          <div class="col-md-4">

            <h2 class="text-secondary">Queries</h2>

            <TaintChooser name="Dispatch" :taints="settings.query.dispatch" @update="(update) => {
              settings.query.dispatch = update
              save()
            }"/>
            <TaintChooser name="Query Handlers" :taints="settings.query.handlers" @update="(update) => {
              settings.query.handlers = update
              save()
            }"/>
          </div>
          <div class="col-md-4 mt-4">

            <h2 class="text-secondary">Sagas</h2>

            <TaintChooser name="Saga Create" :taints="settings.sagas.create" @update="(update) => {
              settings.sagas.create = update
              save()
            }"/>
            <TaintChooser name="Saga Update" :taints="settings.sagas.update" @update="(update) => {
              settings.sagas.update = update
              save()
            }"/>
            <TaintChooser name="Saga Delete" :taints="settings.sagas.delete" @update="(update) => {
              settings.sagas.delete = update
              save()
            }"/>
            <TaintChooser name="Saga Retrieve" :taints="settings.sagas.retrieve" @update="(update) => {
              settings.sagas.retrieve = update
              save()
            }"/>
          </div>
          <div class="col-md-4 mt-4">

            <h2 class="text-secondary">Token Store</h2>

            <TaintChooser name="Store Token" :taints="settings.tokens.store" @update="(update) => {
              settings.tokens.store = update
              save()
            }"/>
            <TaintChooser name="Fetch Token" :taints="settings.tokens.fetch" @update="(update) => {
              settings.tokens.fetch = update
              save()
            }"/>
          </div>
        </div>
      </div>
    </div>


  </div>
</template>
