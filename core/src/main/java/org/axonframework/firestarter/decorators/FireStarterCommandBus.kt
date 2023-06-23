package org.axonframework.firestarter.decorators

import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.common.Registration
import org.axonframework.firestarter.FireStarterSettingsHolder
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.MessageHandler
import org.axonframework.messaging.MessageHandlerInterceptor

class FireStarterCommandBus(private val delegate: CommandBus) : CommandBus {
    override fun registerHandlerInterceptor(handlerInterceptor: MessageHandlerInterceptor<in CommandMessage<*>>): Registration? {
        return delegate.registerHandlerInterceptor(handlerInterceptor)
    }

    override fun registerDispatchInterceptor(dispatchInterceptor: MessageDispatchInterceptor<in CommandMessage<*>>): Registration? {
        return delegate.registerDispatchInterceptor(dispatchInterceptor)
    }

    override fun <C : Any?> dispatch(command: CommandMessage<C>) {
        return delegate.dispatch(command)
    }

    override fun <C : Any?, R : Any?> dispatch(command: CommandMessage<C>, callback: CommandCallback<in C, in R>) {
        FireStarterSettingsHolder.getSettings().command?.dispatch?.applyTaints()
        return delegate.dispatch(command, callback)
    }

    override fun subscribe(commandName: String, handler: MessageHandler<in CommandMessage<*>>): Registration? {
        return delegate.subscribe(commandName, handler)
    }
}
