package com.robotutor.nexora.module.identity.interfaces.controller.mapper

import com.robotutor.nexora.module.identity.application.command.AuthenticateAccountCommand
import com.robotutor.nexora.module.identity.application.command.RegisterAccountCommand
import com.robotutor.nexora.module.identity.application.command.RotateCredentialCommand
import com.robotutor.nexora.module.identity.domain.aggregate.AccountAggregate
import com.robotutor.nexora.module.identity.domain.vo.CredentialId
import com.robotutor.nexora.module.identity.domain.vo.CredentialKind
import com.robotutor.nexora.module.identity.domain.vo.CredentialSecret
import com.robotutor.nexora.shared.domain.vo.principal.SubjectId
import com.robotutor.nexora.module.identity.interfaces.controller.view.AccountResponse
import com.robotutor.nexora.module.identity.interfaces.controller.view.AuthenticateAccountRequest
import com.robotutor.nexora.module.identity.interfaces.controller.view.RegisterAccountRequest
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

object AccountMapper {
    fun toRegisterAccountCommand(request: RegisterAccountRequest, actorData: ActorData?): RegisterAccountCommand {
        return RegisterAccountCommand(
            credentialId = CredentialId(request.credentialId),
            secret = CredentialSecret(request.secret),
            kind = request.kind,
            type = request.type,
            subjectId = SubjectId(request.principalId),
            createdBy = actorData?.actorId
        )
    }

    fun toAccountResponse(account: AccountAggregate): AccountResponse {
        return AccountResponse(
            accountId = account.accountId.value,
            type = account.type,
            principalId = account.subjectId.value,
            status = account.getStatus(),
            createdAt = account.createdAt,
            updatedAt = account.getUpdatedAt()
        )
    }

    fun toAuthenticateAccountCommand(authenticateAccountRequest: AuthenticateAccountRequest): AuthenticateAccountCommand {
        return AuthenticateAccountCommand(
            credentialId = CredentialId(authenticateAccountRequest.credentialId),
            secret = CredentialSecret(authenticateAccountRequest.secret),
        )
    }

    fun toRotateCredentialsCommand(principalIdValue: String, actorData: ActorData): RotateCredentialCommand {
        return RotateCredentialCommand(
            subjectId = SubjectId(principalIdValue),
            kind = CredentialKind.API_SECRET,
            actorData = actorData
        )
    }
}
