package com.robotutor.nexora.modules.user.infrastructure.persistence.repository

import com.robotutor.nexora.modules.user.infrastructure.persistence.mapper.UserDocumentMapper
import com.robotutor.nexora.modules.user.infrastructure.persistence.model.UserDocument
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.modules.user.domain.repository.UserRepository
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoUserRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<User, UserDocument>(mongoTemplate, UserDocument::class.java, UserDocumentMapper()),
    UserRepository {
    override fun save(user: User): Mono<User> {
        val query = Query(Criteria.where("userId").`is`(user.userId.value))
        return this.findAndReplace(query, user)
    }

    override fun deleteByUserId(userId: UserId): Mono<User> {
        val query = Query(Criteria.where("userId").`is`(userId.value))
        return this.deleteOne(query)
    }

    override fun findByUserId(userId: UserId): Mono<User> {
        val query = Query(Criteria.where("userId").`is`(userId.value))
        return this.findOne(query)
    }

    override fun findByEmail(email: Email): Mono<User> {
        val query = Query(Criteria.where("email").`is`(email.value))
        return this.findOne(query)
    }
}