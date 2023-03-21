/*
 * Copyright (c) 2023-2023. AxonIQ
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.firestarter

data class FireStarterSettings(
    val events: EventStoreSettings? = EventStoreSettings(),
    val command: CommandSettings? = CommandSettings(),
    val query: QuerySettings? = QuerySettings(),
    val sagas: SagaSettings? = SagaSettings(),
    val tokens: TokenSettings? = TokenSettings(),
)

data class EventStoreSettings(
    val readAggregateStream: Taints = Taints(),
    val publishEvent: Taints = Taints(),
    val commitEvents: Taints = Taints(),
    val storeSnapshot: Taints = Taints(),
    val openStream: Taints = Taints(),
    val handlers: Taints = Taints(),
)

data class CommandSettings(
    val lockTime: Taints = Taints(),
    val dispatch: Taints = Taints(),
    val handlers: Taints = Taints(),
    val repositoryLoad: Taints = Taints(),
)

data class QuerySettings(
    val dispatch: Taints = Taints(),
    val handlers: Taints = Taints(),
)


data class SagaSettings(
    val create: Taints = Taints(),
    val update: Taints = Taints(),
    val delete: Taints = Taints(),
    val retrieve: Taints = Taints(),
)

data class TokenSettings(
    val fetch: Taints = Taints(),
    val store: Taints = Taints(),
)

data class Taints(
    val errorRate: ErrorRate? = null,
    val fixedDelay: FixedDelay? = null,
    val randomDelay: RandomDelay? = null,
) {
    fun applyTaints() {
        errorRate?.apply()
        fixedDelay?.apply()
        randomDelay?.apply()
    }
}

data class ErrorRate(val rate: Double, val runtimeException: Boolean = false) {
    fun apply() {
        if (Math.random() > rate) {
            return
        }
        if (runtimeException) {
            throw AxonFireStarterRuntimeException()
        }
        throw AxonFireStarterCheckedException()
    }
}

data class FixedDelay(val delay: Long) {
    fun apply() {
        Thread.sleep(delay)
    }
}

data class RandomDelay(val lowerBound: Long, val higherBound: Long) {
    fun apply() {
        val add = (higherBound - lowerBound) * Math.random()
        Thread.sleep(lowerBound + add.toLong())
    }
}

class AxonFireStarterRuntimeException : RuntimeException("Axon Fire Starter Runtime Exception!")
class AxonFireStarterCheckedException : Exception("Axon Fire Starter Checked Exception!")
