package com.robotutor.nexora.modules.user.infrastructure.persistence.model

import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.shared.infrastructure.persistence.model.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@TypeAlias("User")
@Document("users")
data class UserDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true) val userId: String,
    val name: String,
    @Indexed(unique = true) val email: String,
    val mobile: String,
    val isEmailVerified: Boolean,
    val isMobileVerified: Boolean,
    val registeredAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<User>