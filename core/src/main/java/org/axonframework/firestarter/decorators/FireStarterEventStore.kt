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
import org.axonframework.common.stream.BlockingStream
import org.axonframework.eventhandling.DomainEventMessage
import org.axonframework.eventhandling.EventMessage
import org.axonframework.eventhandling.TrackedEventMessage
import org.axonframework.eventhandling.TrackingToken
import org.axonframework.eventsourcing.eventstore.DomainEventStream
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.firestarter.FireStarterSettingsHolder
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork
import org.axonframework.tracing.SpanFactory
import java.time.Duration
import java.time.Instant
import java.util.function.Consumer

class FireStarterEventStore(private val delegate: EventStore, private val spanFactory: SpanFactory) : EventStore {

    override fun subscribe(messageProcessor: Consumer<MutableList<out EventMessage<*>>>): Registration? {
        return delegate.subscribe(messageProcessor)
    }

    override fun registerDispatchInterceptor(dispatchInterceptor: MessageDispatchInterceptor<in EventMessage<*>>): Registration? {
        return delegate.registerDispatchInterceptor(dispatchInterceptor)
    }

    override fun publish(events: MutableList<out EventMessage<*>>) {
        return spanFactory.createInternalSpan { "FireStarterEventStore.publish" }.runSupplier {
            FireStarterSettingsHolder.getSettings().events?.publishEvent?.applyTaints()
            CurrentUnitOfWork.map { uow ->
                uow.resources().computeIfAbsent("__firestarter_commit_taint") {
                    uow.onCommit {
                        FireStarterSettingsHolder.getSettings().events?.commitEvents?.applyTaints()
                    }
                }
            }
            delegate.publish(events)
        }
    }

    override fun openStream(trackingToken: TrackingToken?): BlockingStream<TrackedEventMessage<*>>? {
        return spanFactory.createInternalSpan { "FireStarterEventStore.openStream" }.runSupplier {
            FireStarterSettingsHolder.getSettings().events?.openStream?.applyTaints()
            delegate.openStream(trackingToken)
        }
    }

    override fun readEvents(aggregateIdentifier: String): DomainEventStream? {
        return spanFactory.createInternalSpan { "FireStarterEventStore.readEvents" }.runSupplier {
            FireStarterSettingsHolder.getSettings().events?.readAggregateStream?.applyTaints()
            delegate.readEvents(aggregateIdentifier)
        }
    }

    override fun storeSnapshot(snapshot: DomainEventMessage<*>) {
        return spanFactory.createInternalSpan { "FireStarterEventStore.storeSnapshot" }.runSupplier {
            FireStarterSettingsHolder.getSettings().events?.storeSnapshot?.applyTaints()
            delegate.storeSnapshot(snapshot)
        }
    }

    override fun createTailToken(): TrackingToken? {
        return delegate.createTailToken()
    }

    override fun createHeadToken(): TrackingToken? {
        return delegate.createHeadToken()
    }

    override fun createTokenAt(dateTime: Instant): TrackingToken? {
        return delegate.createTokenAt(dateTime)
    }

    override fun createTokenSince(duration: Duration?): TrackingToken? {
        return delegate.createTokenAt(Instant.now() - duration)
    }
}
