package com.robotutor.nexora.module.master.infrastructure.persistence.repository

import com.robotutor.nexora.module.master.infrastructure.persistence.document.AddressDocument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AddressDocumentRepository : ReactiveMongoRepository<AddressDocument, String> {
    fun findByPinCode(pinCode: String): Mono<AddressDocument>
}
