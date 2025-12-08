package com.robotutor.nexora.context.user.infrastructure.persistence.mapper

import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.context.user.infrastructure.persistence.document.UserDocument
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object UserDocumentMapper : DocumentMapper<UserAggregate, UserDocument> {
    override fun toMongoDocument(domain: UserAggregate): UserDocument {
        return UserDocument(
            userId = domain.userId.value,
            accountId = domain.accountId?.value,
            name = domain.name.value,
            email = domain.email.value,
            mobile = domain.mobile.value,
            isEmailVerified = domain.email.isVerified,
            isMobileVerified = domain.mobile.isVerified,
            registeredAt = domain.registeredAt,
            updatedAt = domain.updatedAt,
            state = domain.state,
            version = domain.version
        )
    }

    override fun toDomainModel(document: UserDocument): UserAggregate {
        return UserAggregate(
            userId = UserId(document.userId),
            accountId = document.accountId?.let { AccountId(it) },
            name = Name(document.name),
            email = Email(document.email, document.isEmailVerified),
            mobile = Mobile(document.mobile, document.isMobileVerified),
            registeredAt = document.registeredAt,
            updatedAt = document.updatedAt,
            state = document.state,
            version = document.version
        )
    }
}