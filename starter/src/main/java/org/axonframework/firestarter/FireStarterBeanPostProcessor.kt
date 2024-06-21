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

import org.axonframework.commandhandling.CommandBus
import org.axonframework.eventhandling.tokenstore.TokenStore
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.firestarter.decorators.*
import org.axonframework.modelling.saga.repository.SagaStore
import org.axonframework.queryhandling.QueryBus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor

class FireStarterBeanPostProcessor(
    private val settingsHolder: FireStarterSettingsHolder,
) : BeanPostProcessor {
    private val logger = LoggerFactory.getLogger(
        this::class.java
    )

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean is EventStore) {
            logger.info("Decorating EventStore with Axon Framework Firestarter")
            return FireStarterEventStore(bean, settingsHolder)
        }
        if (bean is QueryBus) {
            logger.info("Decorating QueryBus with Axon Framework Firestarter")
            return FireStarterQueryBus(bean, settingsHolder)
        }
        if (bean is CommandBus) {
            logger.info("Decorating CommandBus with Axon Framework Firestarter")
            return FireStarterCommandBus(bean, settingsHolder)
        }
        if (bean is SagaStore<*>) {
            logger.info("Decorating SagaRepository with Axon Framework Firestarter")
            return FireStarterSagaStore(bean, settingsHolder)
        }
        if (bean is TokenStore) {
            logger.info("Decorating TokenStore with Axon Framework Firestarter")
            return FireStarterTokenStore(bean, settingsHolder)
        }
        return super.postProcessBeforeInitialization(bean, beanName)
    }
}
