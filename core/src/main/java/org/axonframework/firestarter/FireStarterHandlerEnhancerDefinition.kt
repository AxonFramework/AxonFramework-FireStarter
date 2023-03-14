package org.axonframework.firestarter

import org.axonframework.commandhandling.CommandMessage
import org.axonframework.deadline.DeadlineMessage
import org.axonframework.eventhandling.EventMessage
import org.axonframework.messaging.Message
import org.axonframework.messaging.annotation.HandlerEnhancerDefinition
import org.axonframework.messaging.annotation.MessageHandlingMember
import org.axonframework.messaging.annotation.WrappedMessageHandlingMember
import org.axonframework.queryhandling.QueryMessage

class FireStarterHandlerEnhancerDefinition : HandlerEnhancerDefinition {
    override fun <T : Any?> wrapHandler(p0: MessageHandlingMember<T>): MessageHandlingMember<T> {
        if (p0.canHandleMessageType(QueryMessage::class.java)) {
            return object : WrappedMessageHandlingMember<T>(p0) {
                override fun handle(message: Message<*>, target: T?): Any? {
                    FireStarterSettingsHolder.getSettings().query?.handlers?.applyTaints()
                    return super.handle(message, target)
                }
            }
        }
        if (p0.canHandleMessageType(EventMessage::class.java)) {
            if (p0.attribute<Any>("EventSourcingHandler.payloadType").isPresent) {
                // Skip event sourcing handlers
                return p0;
            }
            return object : WrappedMessageHandlingMember<T>(p0) {
                override fun handle(message: Message<*>, target: T?): Any? {
                    FireStarterSettingsHolder.getSettings().events?.handlers?.applyTaints()
                    return super.handle(message, target)
                }
            }
        }
        if (p0.canHandleMessageType(CommandMessage::class.java) || p0.canHandleMessageType(DeadlineMessage::class.java)) {
            return object : WrappedMessageHandlingMember<T>(p0) {
                override fun handle(message: Message<*>, target: T?): Any? {
                    FireStarterSettingsHolder.getSettings().command?.handlers?.applyTaints()
                    return super.handle(message, target)
                }
            }
        }

        return p0
    }
}
