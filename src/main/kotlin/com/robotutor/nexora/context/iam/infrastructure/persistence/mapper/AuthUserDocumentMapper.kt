package com.robotutor.nexora.context.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.context.iam.domain.entity.AuthUser
import com.robotutor.nexora.context.iam.domain.entity.HashedPassword
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.AuthUserDocument
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object AuthUserDocumentMapper : DocumentMapper<AuthUser, AuthUserDocument> {
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
