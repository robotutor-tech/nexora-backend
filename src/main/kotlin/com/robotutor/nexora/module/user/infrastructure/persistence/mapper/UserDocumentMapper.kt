package com.robotutor.nexora.module.user.infrastructure.persistence.mapper

import com.robotutor.nexora.module.user.domain.aggregate.User
import com.robotutor.nexora.module.user.domain.vo.Email
import com.robotutor.nexora.module.user.domain.vo.Mobile
import com.robotutor.nexora.shared.domain.vo.FullName
import com.robotutor.nexora.module.user.infrastructure.persistence.document.UserDocument
import com.robotutor.nexora.shared.domain.vo.UserId
import com.robotutor.nexora.shared.persistence.mapper.DocumentMapper

object UserDocumentMapper : DocumentMapper<User, UserDocument> {
    override fun toMongoDocument(domain: User): UserDocument {
        return UserDocument(
            id = domain.getObjectId(),
            userId = domain.userId.value,
            name = domain.fullName.value,
            email = domain.email.value,
            mobile = domain.mobile.value,
            isEmailVerified = domain.email.isVerified,
            isMobileVerified = domain.mobile.isVerified,
            registeredAt = domain.registeredAt,
            updatedAt = domain.updatedAt(),
            state = domain.state(),
            version = domain.getVersion()
        )
    }

    override fun toDomainModel(document: UserDocument): User {
        return User.create(
            userId = UserId(document.userId),
            fullName = FullName.of(document.name),
            email = Email(document.email, document.isEmailVerified),
            mobile = Mobile(document.mobile, document.isMobileVerified),
            registeredAt = document.registeredAt,
            updatedAt = document.updatedAt,
            state = document.state,
        )
            .setObjectIdAndVersion(document.id, document.version)
    }
}
