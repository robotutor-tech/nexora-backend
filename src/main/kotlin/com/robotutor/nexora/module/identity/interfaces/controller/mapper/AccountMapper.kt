package com.robotutor.nexora.module.identity.interfaces.controller.mapper

import com.robotutor.nexora.module.identity.application.command.AuthenticateAccountCommand
import com.robotutor.nexora.module.identity.application.command.RegisterUserAccountCommand
import com.robotutor.nexora.module.identity.application.command.RotateCredentialCommand
import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.vo.Email
import com.robotutor.nexora.module.identity.domain.vo.RawPassword
import com.robotutor.nexora.module.identity.interfaces.controller.view.AccountResponse
import com.robotutor.nexora.module.identity.interfaces.controller.view.AuthenticateAccountRequest
import com.robotutor.nexora.module.identity.interfaces.controller.view.RegisterUserAccountRequest
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.DeviceId
import com.robotutor.nexora.shared.domain.vo.UserId

object AccountMapper {
    fun toRegisterUserAccountCommand(request: RegisterUserAccountRequest): RegisterUserAccountCommand {
        return RegisterUserAccountCommand(
            email = Email(request.email),
            password = RawPassword(request.password),
            userId = UserId(request.userId)
        )
    }

    fun toAccountResponse(account: Account): AccountResponse {
        return AccountResponse(
            accountId = account.accountId.value,
            type = account.accountType,
            principalId = account.subjectId.value,
            status = account.getStatus(),
            createdAt = account.createdAt,
            updatedAt = account.getUpdatedAt()
        )
    }

    fun toAuthenticateAccountCommand(authenticateAccountRequest: AuthenticateAccountRequest): AuthenticateAccountCommand {
        return AuthenticateAccountCommand(
            credentialId = Email(authenticateAccountRequest.credentialId),
            secret = RawPassword(authenticateAccountRequest.secret),
        )
    }

    fun toRotateCredentialsCommand(principalIdValue: String, actorData: ActorData): RotateCredentialCommand {
        return RotateCredentialCommand(
            subjectId = DeviceId(principalIdValue),
            actorData = actorData
        )
    }
}
