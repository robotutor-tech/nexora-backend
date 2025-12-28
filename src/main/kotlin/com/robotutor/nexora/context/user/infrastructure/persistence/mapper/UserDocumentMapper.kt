package com.robotutor.nexora.context.user.infrastructure.persistence.mapper

import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.context.user.infrastructure.persistence.document.UserDocument
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.common.persistence.mongo.mapper.DocumentMapper

object UserDocumentMapper : DocumentMapper<UserAggregate, UserDocument> {
    override fun toMongoDocument(domain: UserAggregate): UserDocument {
        return UserDocument(
            id = domain.getObjectId(),
            userId = domain.userId.value,
            name = domain.name.value,
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

    override fun toDomainModel(document: UserDocument): UserAggregate {
        return UserAggregate
            .create(
                userId = UserId(document.userId),
                name = Name(document.name),
                email = Email(document.email, document.isEmailVerified),
                mobile = Mobile(document.mobile, document.isMobileVerified),
                registeredAt = document.registeredAt,
                updatedAt = document.updatedAt,
                state = document.state,
            )
            .setObjectIdAndVersion(document.id, document.version)
    }
}