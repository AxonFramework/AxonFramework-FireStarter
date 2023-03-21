package org.axonframework.firestarter.decorators

import org.axonframework.firestarter.FireStarterSettingsHolder
import org.axonframework.modelling.saga.AssociationValue
import org.axonframework.modelling.saga.AssociationValues
import org.axonframework.modelling.saga.repository.SagaStore

class FireStarterSagaStore<T>(private val delegate: SagaStore<T>) : SagaStore<T> {
    override fun findSagas(sagaType: Class<out T>?, associationValue: AssociationValue?): MutableSet<String>? {
        FireStarterSettingsHolder.getSettings().sagas?.retrieve?.applyTaints()
        return delegate.findSagas(sagaType, associationValue)
    }

    override fun <S : T> loadSaga(sagaType: Class<S>?, sagaIdentifier: String?): SagaStore.Entry<S>? {
        FireStarterSettingsHolder.getSettings().sagas?.retrieve?.applyTaints()
        return delegate.loadSaga(sagaType, sagaIdentifier)
    }

    override fun deleteSaga(
        sagaType: Class<out T>?,
        sagaIdentifier: String?,
        associationValues: MutableSet<AssociationValue>?
    ) {
        FireStarterSettingsHolder.getSettings().sagas?.delete?.applyTaints()
        delegate.deleteSaga(sagaType, sagaIdentifier, associationValues)
    }

    override fun insertSaga(
        sagaType: Class<out T>?,
        sagaIdentifier: String?,
        saga: T,
        associationValues: MutableSet<AssociationValue>?
    ) {
        FireStarterSettingsHolder.getSettings().sagas?.create?.applyTaints()
        delegate.insertSaga(sagaType, sagaIdentifier, saga, associationValues)
    }

    override fun updateSaga(
        sagaType: Class<out T>?,
        sagaIdentifier: String?,
        saga: T,
        associationValues: AssociationValues?
    ) {
        FireStarterSettingsHolder.getSettings().sagas?.update?.applyTaints()
        delegate.updateSaga(sagaType, sagaIdentifier, saga, associationValues)
    }

}
