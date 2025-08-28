package com.robotutor.nexora.modules.user.adapters.persistence.model

import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.shared.adapters.persistence.model.MongoDocument
import java.time.Instant
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Version

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
