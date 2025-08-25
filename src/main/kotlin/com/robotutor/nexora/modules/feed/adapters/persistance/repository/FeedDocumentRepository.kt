package com.robotutor.nexora.modules.feed.adapters.persistance.repository

import com.robotutor.nexora.modules.feed.adapters.persistance.model.FeedDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface FeedDocumentRepository : ReactiveCrudRepository<FeedDocument, String> {
    fun findAllByPremisesIdAndFeedIdIn(premisesId: String, feedIds: List<String>): Flux<FeedDocument>
}