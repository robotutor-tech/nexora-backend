package com.robotutor.nexora.modules.auth.adapters.persistence.mapper

import com.robotutor.nexora.modules.auth.adapters.persistence.model.AuthUserDocument
import com.robotutor.nexora.modules.auth.domain.model.AuthUser
import com.robotutor.nexora.modules.auth.domain.model.HashedPassword
import com.robotutor.nexora.shared.adapters.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.UserId
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

