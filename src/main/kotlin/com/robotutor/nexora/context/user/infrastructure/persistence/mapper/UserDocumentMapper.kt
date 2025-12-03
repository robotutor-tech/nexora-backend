package com.robotutor.nexora.context.user.infrastructure.persistence.mapper

import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.context.user.infrastructure.persistence.document.UserDocument
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object UserDocumentMapper : DocumentMapper<UserAggregate, UserDocument> {
    override fun toMongoDocument(domain: UserAggregate): UserDocument {
        return UserDocument(
            userId = domain.userId.value,
            name = domain.name.value,
            email = domain.email.value,
            mobile = domain.mobile.value,
            isEmailVerified = domain.email.isVerified,
            isMobileVerified = domain.mobile.isVerified,
            registeredAt = domain.registeredAt,
            version = domain.version
        )
    }

    override fun toDomainModel(document: UserDocument): UserAggregate {
        return UserAggregate(
            userId = UserId(document.userId),
            name = Name(document.name),
            email = Email(document.email, document.isEmailVerified),
            mobile = Mobile(document.mobile, document.isMobileVerified),
            registeredAt = document.registeredAt,
            version = document.version
        )
    }
}