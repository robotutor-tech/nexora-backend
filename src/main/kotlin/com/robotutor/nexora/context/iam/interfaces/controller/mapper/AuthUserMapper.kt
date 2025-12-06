package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.application.command.ActorLoginCommand
import com.robotutor.nexora.context.iam.application.command.LoginCommand
import com.robotutor.nexora.context.iam.domain.entity.Password
import com.robotutor.nexora.context.iam.interfaces.controller.view.ActorLoginRequest
import com.robotutor.nexora.context.iam.interfaces.controller.view.AuthLoginRequest
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.UserData

object AuthUserMapper {
//    fun toAuthUserResponseDto(authUserResponse: AuthUserResponse): AccountResponse {
////        return AccountResponse(
////            userId = authUserResponse.userId.value,
////            email = authUserResponse.email.value,
////        )
//    }

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