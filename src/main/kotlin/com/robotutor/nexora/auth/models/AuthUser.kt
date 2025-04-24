package com.robotutor.nexora.auth.models

import com.robotutor.nexora.auth.controllers.views.AuthUserRequest
import com.robotutor.nexora.security.models.UserId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val AUTH_USER_COLLECTION = "authUsers"

@TypeAlias("AuthUser")
@Document(AUTH_USER_COLLECTION)
data class AuthUser(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val userId: UserId,
    @Indexed(unique = true)
    val email: String,
    val password: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(authUserRequest: AuthUserRequest, password: String): AuthUser {
            return AuthUser(
                userId = authUserRequest.userId,
                email = authUserRequest.email,
                password = password,
            )
        }
    }
}
