package com.robotutor.nexora.modules.user.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.user.infrastructure.persistence.document.UserDocument
import com.robotutor.nexora.modules.user.domain.entity.User
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.Mobile
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import org.springframework.stereotype.Component

@Component
class UserDocumentMapper : DocumentMapper<User, UserDocument> {
    override fun toMongoDocument(domain: User): UserDocument {
        return UserDocument(
            userId = domain.userId.value,
            name = domain.name.value,
            email = domain.email.value,
            mobile = domain.mobile.value,
            isEmailVerified = domain.isEmailVerified,
            isMobileVerified = domain.isMobileVerified,
            registeredAt = domain.registeredAt,
            version = domain.version
        )
    }

    override fun toDomainModel(document: UserDocument): User {
        return User(
            userId = UserId(document.userId),
            name = Name(document.name),
            email = Email(document.email),
            mobile = Mobile(document.mobile),
            isEmailVerified = document.isEmailVerified,
            isMobileVerified = document.isMobileVerified,
            registeredAt = document.registeredAt,
            version = document.version
        )
    }
}