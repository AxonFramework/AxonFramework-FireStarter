package org.axonframework.firestarter.decorators

import org.axonframework.firestarter.FireStarterSettingsHolder
import org.axonframework.messaging.Message
import org.axonframework.messaging.ScopeDescriptor
import org.axonframework.modelling.command.Aggregate
import org.axonframework.modelling.command.Repository
import java.util.concurrent.Callable
import java.util.function.Consumer

class FireStarterRepository<T>(private val delegate: Repository<T>) :
        Repository<T> {
    override fun send(message: Message<*>?, scopeDescription: ScopeDescriptor?) {
        return delegate.send(message, scopeDescription)
    }

    override fun canResolve(scopeDescription: ScopeDescriptor?): Boolean {
        return delegate.canResolve(scopeDescription)
    }

    override fun load(p0: String): Aggregate<T>? {
        FireStarterSettingsHolder.getSettings().command?.repositoryLoad?.applyTaints()
        return delegate.load(p0)
    }

    override fun load(p0: String, p1: Long?): Aggregate<T>? {
        FireStarterSettingsHolder.getSettings().command?.repositoryLoad?.applyTaints()
        return delegate.load(p0, p1)

    }

    override fun newInstance(p0: Callable<T>): Aggregate<T>? {
        return delegate.newInstance(p0)
    }

    override fun loadOrCreate(aggregateIdentifier: String, factoryMethod: Callable<T>): Aggregate<T>? {
        return delegate.loadOrCreate(aggregateIdentifier, factoryMethod)
    }

    override fun newInstance(factoryMethod: Callable<T>?, initMethod: Consumer<Aggregate<T>>?): Aggregate<T>? {
        return delegate.newInstance(factoryMethod, initMethod)
    }
}
