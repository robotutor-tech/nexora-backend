package com.robotutor.nexora.modules.user.adapters.persistance.model

import com.robotutor.nexora.modules.user.domain.model.Email
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.shared.domain.model.UserId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@TypeAlias("user")
@Document("users")
data class UserDocument(
    @Indexed(unique = true)
    val userId: String,
    val name: String,
    @Indexed(unique = true)
    val email: String,
    val registeredAt: Instant = Instant.now(),
    val version: Long? = null
) {
    fun toDomainModel(): User {
        return User(
            userId = UserId(userId),
            name = name,
            email = Email(email),
            registeredAt = registeredAt,
            version = version
        )
    }

    companion object {
        fun from(user: User): UserDocument {
            return UserDocument(
                userId = user.userId.value,
                name = user.name,
                email = user.email.value,
                registeredAt = user.registeredAt,
                version = user.version
            )
        }
    }
}

