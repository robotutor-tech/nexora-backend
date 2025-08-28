package com.robotutor.nexora.modules.auth.adapters.persistence.repository

import com.robotutor.nexora.modules.auth.adapters.persistence.mapper.AuthUserDocumentMapper
import com.robotutor.nexora.modules.auth.adapters.persistence.model.AuthUserDocument
import com.robotutor.nexora.modules.auth.domain.model.AuthUser
import com.robotutor.nexora.modules.auth.domain.repository.AuthUserRepository
import com.robotutor.nexora.shared.adapters.persistence.repository.MongoRepository
import com.robotutor.nexora.shared.domain.model.Email
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoAuthUserRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<AuthUser, AuthUserDocument>(mongoTemplate, AuthUserDocument::class.java, AuthUserDocumentMapper()),
    AuthUserRepository {
    override fun save(authUser: AuthUser): Mono<AuthUser> {
        val query = Query(Criteria.where("userId").`is`(authUser.userId.value))
        return this.findAndReplace(query, authUser)
    }

    override fun findByEmail(email: Email): Mono<AuthUser> {
        val query = Query(Criteria.where("email").`is`(email.value))
        return this.findOne(query)
    }
}
