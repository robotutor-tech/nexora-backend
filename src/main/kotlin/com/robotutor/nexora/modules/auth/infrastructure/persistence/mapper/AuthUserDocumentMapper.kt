package com.robotutor.nexora.modules.auth.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.auth.domain.model.AuthUser
import com.robotutor.nexora.modules.auth.domain.model.HashedPassword
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.AuthUserDocument
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import org.springframework.stereotype.Component

@Component
class AuthUserDocumentMapper : DocumentMapper<AuthUser, AuthUserDocument> {
    override fun toMongoDocument(domain: AuthUser): AuthUserDocument = AuthUserDocument(
        id = null,
        userId = domain.userId.value,
        email = domain.email.value,
        password = domain.password.value,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt,
        version = domain.version
    )

    override fun toDomainModel(document: AuthUserDocument): AuthUser = AuthUser(
        userId = UserId(document.userId),
        email = Email(document.email),
        password = HashedPassword(document.password),
        createdAt = document.createdAt,
        updatedAt = document.updatedAt,
        version = document.version
    )
}
