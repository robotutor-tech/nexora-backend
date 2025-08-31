package com.robotutor.nexora.modules.auth.infrastructure.persistence.model

import com.robotutor.nexora.modules.auth.domain.model.AuthUser
import com.robotutor.nexora.shared.infrastructure.persistence.model.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant


const val AUTH_USER_COLLECTION = "authUsers"

@TypeAlias("AuthUser")
@Document(AUTH_USER_COLLECTION)
data class AuthUserDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val userId: String,
    @Indexed(unique = true)
    val email: String,
    val password: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long?
) : MongoDocument<AuthUser>