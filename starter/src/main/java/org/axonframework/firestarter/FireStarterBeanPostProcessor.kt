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
import org.axonframework.common.lock.LockFactory
import org.axonframework.eventhandling.tokenstore.TokenStore
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.firestarter.decorators.*
import org.axonframework.modelling.command.Repository
import org.axonframework.modelling.saga.SagaRepository
import org.axonframework.queryhandling.QueryBus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor

class FireStarterBeanPostProcessor : BeanPostProcessor {
    private val logger = LoggerFactory.getLogger(
        this::class.java
    )

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean is EventStore) {
            logger.info("Decorating EventStore with Axon Framework Firestarter")
            return FireStarterEventStore(bean)
        }
        if (bean is QueryBus) {
            logger.info("Decorating QueryBus with Axon Framework Firestarter")
            return FireStarterQueryBus(bean)
        }
        if (bean is CommandBus) {
            logger.info("Decorating CommandBus with Axon Framework Firestarter")
            return FireStarterCommandBus(bean)
        }
        if (bean is LockFactory) {
            logger.info("Decorating LockFactory with Axon Framework Firestarter")
            return FireStarterLockFactory(bean)
        }
        if (bean is SagaRepository<*>) {
            logger.info("Decorating SagaRepository with Axon Framework Firestarter")
            return FireStarterSagaRepository(bean)
        }
        if (bean is TokenStore) {
            logger.info("Decorating TokenStore with Axon Framework Firestarter")
            return FireStarterTokenStore(bean)
        }
        if (bean is Repository<*>) {
            logger.info("Decorating Repository with Axon Framework Firestarter")
            return FireStarterRepository(bean)
        }

        return super.postProcessBeforeInitialization(bean, beanName)
    }
}
