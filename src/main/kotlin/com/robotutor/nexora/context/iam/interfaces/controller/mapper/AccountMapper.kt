package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.AuthenticateAccountCommand
import com.robotutor.nexora.context.iam.application.command.RegisterAccountCommand
import com.robotutor.nexora.context.iam.application.command.RotateCredentialCommand
import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId
import com.robotutor.nexora.context.iam.interfaces.controller.view.AccountResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthenticateAccountRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.RegisterAccountRequest
import com.robotutor.nexora.shared.domain.vo.principal.ActorData

object AccountMapper {
    fun toRegisterAccountCommand(request: RegisterAccountRequest, actorData: ActorData?): RegisterAccountCommand {
        return RegisterAccountCommand(
            credentialId = CredentialId(request.credentialId),
            secret = CredentialSecret(request.secret),
            kind = request.kind,
            type = request.type,
            principalId = PrincipalId(request.principalId),
            createdBy = actorData?.actorId
        )
    }

    fun toAccountResponse(account: AccountAggregate): AccountResponse {
        return AccountResponse(
            accountId = account.accountId.value,
            type = account.type,
            principalId = account.principalId.value,
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
            principalId = PrincipalId(principalIdValue),
            kind = CredentialKind.API_SECRET,
            actorData = actorData
        )
    }
}