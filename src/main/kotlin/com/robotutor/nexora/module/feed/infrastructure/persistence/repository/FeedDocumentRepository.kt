package com.robotutor.nexora.module.feed.infrastructure.persistence.repository

import com.robotutor.nexora.module.feed.infrastructure.persistence.document.FeedDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedDocumentRepository : ReactiveCrudRepository<FeedDocument, String>

