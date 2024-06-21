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

import org.axonframework.common.ReflectionUtils
import org.axonframework.common.lock.LockFactory
import org.axonframework.config.AggregateConfiguration
import org.axonframework.config.Component
import org.axonframework.config.ConfigurerModule
import org.axonframework.firestarter.decorators.FireStarterLockFactory
import org.axonframework.firestarter.decorators.FireStarterRepository
import org.axonframework.modelling.command.Repository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FireStarterConfiguration {
    @Bean
    fun fireStarterSettingsHolder(
        @Value("\${spring.application.name:}") nameProperty: String?,
        applicationContext: ApplicationContext,
    ): FireStarterSettingsHolder {
        // Determine the name. First, name property, then spring application name, then context name
        // This should always result in a sane value
        val applicationName = (nameProperty?.trim()?.ifEmpty { null })
            ?: (applicationContext.applicationName.trim().ifEmpty { null })
            ?: (applicationContext.id?.removeSuffix("-1"))
            ?: "Unknown application"
        return FireStarterSettingsHolder(applicationName)
    }

    @Bean
    fun fireStarterBeanPostProcessor(settingsHolder: FireStarterSettingsHolder): FireStarterBeanPostProcessor {
        return FireStarterBeanPostProcessor(settingsHolder)
    }

    @Bean
    fun fireStarterController(settingsHolder: FireStarterSettingsHolder): FireStarterController {
        return FireStarterController(settingsHolder)
    }

    @Bean
    fun fireStarterHandlerEnhancerDefinition(settingsHolder: FireStarterSettingsHolder): FireStarterHandlerEnhancerDefinition {
        return FireStarterHandlerEnhancerDefinition(settingsHolder)
    }

    @Bean
    fun fireStarterConfigurerModule(settingsHolder: FireStarterSettingsHolder): ConfigurerModule {
        return ConfigurerModule { configurer ->
            configurer.onInitialize { config ->
                config.findModules(AggregateConfiguration::class.java).forEach { ac ->
                    val field = ac::class.java.declaredFields.first { it.name === "repository" }
                    val current = ReflectionUtils.getFieldValue<Component<Repository<*>>>(field, ac)

                    ReflectionUtils.setFieldValue(
                        field,
                        ac,
                        Component<Repository<*>>(
                            { config },
                            "Repository",
                            { c ->
                                val currentRepo = current.get()
                                current::class.java.declaredFields.firstOrNull { it.name === "lockFactory" }?.let {
                                    val delegate = ReflectionUtils.getFieldValue<LockFactory>(it, currentRepo)
                                    ReflectionUtils.setFieldValue(
                                        it,
                                        currentRepo,
                                        FireStarterLockFactory(delegate, settingsHolder)
                                    )
                                }
                                FireStarterRepository(currentRepo, settingsHolder)
                            }),
                    )
                }

            }
        }
    }
}
