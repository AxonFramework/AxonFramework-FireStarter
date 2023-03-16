package org.axonframework.firestarter.decorators

import org.axonframework.firestarter.FireStarterSettingsHolder
import org.axonframework.modelling.saga.AssociationValue
import org.axonframework.modelling.saga.Saga
import org.axonframework.modelling.saga.SagaRepository
import java.util.function.Supplier

class FireStarterSagaRepository<T>(private val delegate: SagaRepository<T>) : SagaRepository<T> {
    override fun find(p0: AssociationValue?): MutableSet<String>? {
        FireStarterSettingsHolder.getSettings().sagas?.associationLookup?.applyTaints()
        return delegate.find(p0)
    }

    override fun load(p0: String?): Saga<T>? {
        FireStarterSettingsHolder.getSettings().sagas?.load?.applyTaints()
        return delegate.load(p0)
    }

    override fun createInstance(p0: String?, p1: Supplier<T>?): Saga<T>? {
        return delegate.createInstance(p0, p1)
    }
}
