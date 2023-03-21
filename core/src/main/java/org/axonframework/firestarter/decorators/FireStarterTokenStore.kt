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

import org.axonframework.eventhandling.Segment
import org.axonframework.eventhandling.TrackingToken
import org.axonframework.eventhandling.tokenstore.TokenStore
import org.axonframework.eventhandling.tokenstore.UnableToClaimTokenException
import org.axonframework.eventhandling.tokenstore.UnableToInitializeTokenException
import org.axonframework.eventhandling.tokenstore.UnableToRetrieveIdentifierException
import org.axonframework.firestarter.FireStarterSettingsHolder
import java.util.*

class FireStarterTokenStore(private val delegate: TokenStore) : TokenStore {

    @Throws(UnableToClaimTokenException::class)
    override fun initializeTokenSegments(processorName: String, segmentCount: Int) {
        FireStarterSettingsHolder.getSettings().tokens?.store?.applyTaints()
        delegate.initializeTokenSegments(processorName, segmentCount)
    }

    @Throws(UnableToClaimTokenException::class)
    override fun initializeTokenSegments(
        processorName: String,
        segmentCount: Int,
        initialToken: TrackingToken?
    ) {
        FireStarterSettingsHolder.getSettings().tokens?.store?.applyTaints()
        delegate.initializeTokenSegments(processorName, segmentCount, initialToken)
    }

    override fun storeToken(token: TrackingToken?, processorName: String, segment: Int) {
        FireStarterSettingsHolder.getSettings().tokens?.store?.applyTaints()
        delegate.storeToken(token, processorName, segment)
    }

    override fun fetchToken(processorName: String, segement: Int): TrackingToken {
        FireStarterSettingsHolder.getSettings().tokens?.fetch?.applyTaints()
        return delegate.fetchToken(processorName, segement)
    }

    @Throws(UnableToClaimTokenException::class)
    override fun fetchToken(
        processorName: String, segment: Segment
    ): TrackingToken? {
        FireStarterSettingsHolder.getSettings().tokens?.fetch?.applyTaints()
        return delegate.fetchToken(processorName, segment.segmentId)
    }

    @Throws(UnableToClaimTokenException::class)
    override fun extendClaim(processorName: String, segment: Int) {
        FireStarterSettingsHolder.getSettings().tokens?.store?.applyTaints()
        delegate.extendClaim(processorName, segment)
    }

    override fun releaseClaim(processorName: String, segment: Int) {
        FireStarterSettingsHolder.getSettings().tokens?.store?.applyTaints()
        delegate.releaseClaim(processorName, segment)
    }

    @Throws(UnableToInitializeTokenException::class)
    override fun initializeSegment(token: TrackingToken?, processorName: String, segment: Int) {
        FireStarterSettingsHolder.getSettings().tokens?.store?.applyTaints()
        delegate.initializeSegment(token, processorName, segment)
    }

    @Throws(UnableToClaimTokenException::class)
    override fun deleteToken(processorName: String, segment: Int) {
        delegate.deleteToken(processorName, segment)
    }

    override fun requiresExplicitSegmentInitialization(): Boolean {
        return delegate.requiresExplicitSegmentInitialization()
    }

    override fun fetchSegments(processorName: String): IntArray {
        FireStarterSettingsHolder.getSettings().tokens?.fetch?.applyTaints()
        return delegate.fetchSegments(processorName)
    }

    override fun fetchAvailableSegments(processorName: String): List<Segment?>? {
        FireStarterSettingsHolder.getSettings().tokens?.fetch?.applyTaints()
        return delegate.fetchAvailableSegments(processorName)
    }

    @Throws(UnableToRetrieveIdentifierException::class)
    override fun retrieveStorageIdentifier(): Optional<String> {
        return delegate.retrieveStorageIdentifier()
    }
}
