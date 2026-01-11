package com.robotutor.nexora.modules.user.builder

import com.robotutor.nexora.module.user.infrastructure.persistence.document.UserDocument
import java.time.Instant

data class UserDocumentBuilder(
    val userId: String = "userId",
    val name: String = "John",
    val email: String = "example@email.com",
    val mobile: String = "9012345678",
    val isEmailVerified: Boolean = false,
    val isMobileVerified: Boolean = false,
    val registeredAt: Instant = Instant.parse("2023-01-01T00:00:00Z"),
    val version: Long? = null
) {
    fun build(): UserDocument {
        return UserDocument(
            userId = userId,
            name = name,
            email = email,
            mobile = mobile,
            isEmailVerified = isEmailVerified,
            isMobileVerified = isMobileVerified,
            registeredAt = registeredAt,
            version = version
        )
    }
}
