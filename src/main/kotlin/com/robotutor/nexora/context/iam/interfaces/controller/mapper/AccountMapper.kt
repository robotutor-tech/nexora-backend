package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.AuthenticateAccountCommand
import com.robotutor.nexora.context.iam.application.command.RegisterAccountCommand
import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.context.iam.interfaces.controller.view.AccountResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthenticateAccountRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.RegisterAccountRequest

object AccountMapper {
    fun toRegisterAccountCommand(request: RegisterAccountRequest): RegisterAccountCommand {
        return RegisterAccountCommand(
            credentialId = CredentialId(request.credentialId),
            secret = CredentialSecret(request.secret),
            kind = request.kind,
            type = request.type
        )
    }

    fun toAccountResponse(account: AccountAggregate): AccountResponse {
        return AccountResponse(
            accountId = account.accountId.value,
            type = account.type,
            status = account.status,
            createdAt = account.createdAt,
            updatedAt = account.updatedAt
        )
    }

    fun toAuthenticateAccountCommand(authenticateAccountRequest: AuthenticateAccountRequest): AuthenticateAccountCommand {
        return AuthenticateAccountCommand(
            credentialId = CredentialId(authenticateAccountRequest.credentialId),
            secret = CredentialSecret(authenticateAccountRequest.secret),
            kind = authenticateAccountRequest.kind
        )
    }
}