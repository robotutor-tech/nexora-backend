package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.ActorLoginCommand
import com.robotutor.nexora.context.iam.application.command.LoginCommand
import com.robotutor.nexora.context.iam.application.command.RegisterAccountCommand
import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.entity.Password
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.context.iam.interfaces.controller.view.AccountResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.ActorLoginRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthLoginRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.RegisterAccountRequest
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.UserData

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
        return AccountResponse(accountId = account.accountId.value)
    }

    fun toLoginCommand(authLoginRequest: AuthLoginRequest): LoginCommand {
        return LoginCommand(
            email = Email(authLoginRequest.email),
            password = Password(authLoginRequest.password)
        )
    }

    fun toActorLoginCommand(
        actorLoginRequest: ActorLoginRequest,
        userData: UserData,
        token: String
    ): ActorLoginCommand {
        return ActorLoginCommand(
            actorId = ActorId(actorLoginRequest.actorId),
            roleId = RoleId(actorLoginRequest.roleId),
            userData = userData,
            token = token.removePrefix("Bearer ")
        )
    }
}