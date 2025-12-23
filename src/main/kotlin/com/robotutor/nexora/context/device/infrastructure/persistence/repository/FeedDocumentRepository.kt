package com.robotutor.nexora.context.device.infrastructure.persistence.repository

import com.robotutor.nexora.context.device.infrastructure.persistence.document.FeedDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedDocumentRepository : ReactiveCrudRepository<FeedDocument, String> {
}