package com.robotutor.nexora.modules.auth.models

import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthUserRequest
import com.robotutor.nexora.common.security.models.UserId
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
data class AuthUser(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val userId: UserId,
    @Indexed(unique = true)
    val email: String,
    val password: String,
    val createdAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
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
