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
package org.axonframework.firestarter.decorators

import org.axonframework.common.Registration
import org.axonframework.firestarter.FireStarterSettingsHolder
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.MessageHandler
import org.axonframework.messaging.MessageHandlerInterceptor
import org.axonframework.queryhandling.*
import org.reactivestreams.Publisher
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

class FireStarterQueryBus(private val delegate: QueryBus) : QueryBus {
    override fun registerHandlerInterceptor(handlerInterceptor: MessageHandlerInterceptor<in QueryMessage<*, *>>): Registration? {
        return delegate.registerHandlerInterceptor(handlerInterceptor)
    }

    override fun registerDispatchInterceptor(dispatchInterceptor: MessageDispatchInterceptor<in QueryMessage<*, *>>): Registration? {
        return delegate.registerDispatchInterceptor(dispatchInterceptor)
    }

    override fun <R : Any?> subscribe(
        queryName: String,
        responseType: Type,
        handler: MessageHandler<in QueryMessage<*, R>>
    ): Registration? {
        return delegate.subscribe(queryName, responseType, handler)
    }

    override fun <Q : Any?, R : Any?> query(query: QueryMessage<Q, R>): CompletableFuture<QueryResponseMessage<R>> {
        return CompletableFuture.runAsync { FireStarterSettingsHolder.getSettings().query?.dispatch?.applyTaints() }
            .thenCompose { delegate.query(query) }
    }

    override fun <Q : Any?, R : Any?> scatterGather(
        query: QueryMessage<Q, R>,
        timeout: Long,
        unit: TimeUnit
    ): Stream<QueryResponseMessage<R>>? {
        FireStarterSettingsHolder.getSettings().query?.dispatch?.applyTaints()
        return delegate.scatterGather(query, timeout, unit)
    }

    override fun <Q : Any?, I : Any?, U : Any?> subscriptionQuery(query: SubscriptionQueryMessage<Q, I, U>): SubscriptionQueryResult<QueryResponseMessage<I>, SubscriptionQueryUpdateMessage<U>>? {
        return delegate.subscriptionQuery(query)
    }

    override fun <Q : Any?, I : Any?, U : Any?> subscriptionQuery(
        query: SubscriptionQueryMessage<Q, I, U>,
        backpressure: SubscriptionQueryBackpressure?,
        updateBufferSize: Int
    ): SubscriptionQueryResult<QueryResponseMessage<I>, SubscriptionQueryUpdateMessage<U>>? {
        return delegate.subscriptionQuery(query, backpressure, updateBufferSize)
    }

    override fun <Q : Any?, I : Any?, U : Any?> subscriptionQuery(
        query: SubscriptionQueryMessage<Q, I, U>,
        updateBufferSize: Int
    ): SubscriptionQueryResult<QueryResponseMessage<I>, SubscriptionQueryUpdateMessage<U>>? {
        return delegate.subscriptionQuery(query, updateBufferSize)
    }

    override fun queryUpdateEmitter(): QueryUpdateEmitter? {
        return delegate.queryUpdateEmitter()
    }

    override fun <Q : Any?, R : Any?> streamingQuery(query: StreamingQueryMessage<Q, R>?): Publisher<QueryResponseMessage<R>>? {
        return delegate.streamingQuery(query)
    }
}
