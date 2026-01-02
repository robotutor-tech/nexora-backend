package com.robotutor.nexora.module.user.infrastructure.persistence.document

import com.robotutor.nexora.module.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.module.user.domain.aggregate.UserState
import com.robotutor.nexora.common.persistence.document.MongoDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val USER_COLLECTION = "users"

@TypeAlias("User")
@Document(USER_COLLECTION)
data class UserDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val userId: String,
    val name: String,
    val email: String,
    val mobile: String,
    val state: UserState,
    val isEmailVerified: Boolean,
    val isMobileVerified: Boolean,
    val registeredAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<UserAggregate>