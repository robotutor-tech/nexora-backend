package com.robotutor.nexora.module.identity.infrastructure.persistence.mapper

import com.robotutor.nexora.module.identity.domain.aggregate.Session
import com.robotutor.nexora.module.identity.domain.vo.HashAccessToken
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.*
import com.robotutor.nexora.shared.domain.vo.*
import com.robotutor.nexora.shared.persistence.mapper.DocumentMapper

object SessionDocumentMapper : DocumentMapper<Session, SessionDocument> {
    override fun toMongoDocument(domain: Session): SessionDocument {
        return SessionDocument(
            id = domain.getObjectId(),
            sessionId = domain.sessionId.value,
            accountDataDocument = toAccountDataDocument(domain.accountData),
            token = domain.token.value,
            status = domain.getStatus(),
            issuedAt = domain.issuedAt,
            expiresAt = domain.expiresAt,
            version = domain.getVersion(),
        )
    }

    override fun toDomainModel(document: SessionDocument): Session {
        return Session.create(
            sessionId = SessionId(document.sessionId),
            token = HashAccessToken(document.token),
            issuedAt = document.issuedAt,
            accountData = toAccountData(document.accountDataDocument),
            expiredAt = document.expiresAt,
            status = document.status,
        ).setObjectIdAndVersion(document.id, document.version)
    }

    private fun toAccountDataDocument(accountData: AccountData): AccountDataDocument {
        return when (accountData) {
            is ActorData -> ActorDataDocument(
                actorId = accountData.actorId.value,
                premisesId = accountData.premisesId.value,
                accountId = accountData.accountId.value,
                subjectType = accountData.subjectType,
                subjectId = accountData.subjectId.value
            )

            is DeviceData -> DeviceDataDocument(
                deviceId = accountData.deviceId.value,
                accountId = accountData.accountId.value,
            )

            is UserData -> UserDataDocument(
                userId = accountData.userId.value,
                accountId = accountData.accountId.value,
            )
        }
    }


    private fun toAccountData(accountDataDocument: AccountDataDocument): AccountData {
        return when (accountDataDocument) {
            is UserDataDocument -> UserData(
                userId = UserId(accountDataDocument.userId),
                accountId = AccountId(accountDataDocument.accountId),
            )

            is DeviceDataDocument -> DeviceData(
                deviceId = DeviceId(accountDataDocument.deviceId),
                accountId = AccountId(accountDataDocument.accountId),
            )

            is ActorDataDocument -> ActorData(
                actorId = ActorId(accountDataDocument.actorId),
                premisesId = PremisesId(accountDataDocument.premisesId),
                accountId = AccountId(accountDataDocument.accountId),
                subjectId = SubjectId.from(accountDataDocument.subjectType, accountDataDocument.subjectId),
                subjectType = accountDataDocument.subjectType
            )

        }
    }
}
