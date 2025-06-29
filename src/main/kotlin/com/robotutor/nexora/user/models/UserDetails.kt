package com.robotutor.nexora.user.models

import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.user.conrollers.views.UserRequest
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val USER_COLLECTION = "users"

@TypeAlias("User")
@Document(USER_COLLECTION)
data class UserDetails(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val userId: UserId,
    val name: String,
    @Indexed(unique = true)
    val email: String,
    val registeredAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(userId: UserId, userRequest: UserRequest): UserDetails {
            return UserDetails(userId = userId, name = userRequest.name, email = userRequest.email)
        }
    }
}
