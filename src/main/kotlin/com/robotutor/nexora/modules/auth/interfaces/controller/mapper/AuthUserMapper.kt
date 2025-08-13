package com.robotutor.nexora.modules.auth.interfaces.controller.mapper

import com.robotutor.nexora.modules.auth.application.command.LoginCommand
import com.robotutor.nexora.modules.auth.application.command.RegisterAuthUserCommand
import com.robotutor.nexora.modules.auth.application.dto.AuthUserResponse
import com.robotutor.nexora.modules.auth.domain.model.Email
import com.robotutor.nexora.modules.auth.domain.model.Password
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthLoginRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthUserRequest
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthUserResponseDto
import com.robotutor.nexora.shared.domain.model.UserId

object AuthUserMapper {
    fun toRegisterAuthUserCommand(authUserRequest: AuthUserRequest): RegisterAuthUserCommand {
        return RegisterAuthUserCommand(
            userId = UserId(authUserRequest.userId),
            email = Email(authUserRequest.email),
            password = Password(authUserRequest.password)
        )
    }

    fun toAuthUserResponseDto(authUserResponse: AuthUserResponse): AuthUserResponseDto {
        return AuthUserResponseDto(
            userId = authUserResponse.userId.value,
            email = authUserResponse.email.value,
        )
    }

    fun toLoginCommand(authLoginRequest: AuthLoginRequest): LoginCommand {
        return LoginCommand(
            email = Email(authLoginRequest.email),
            password = Password(authLoginRequest.password)
        )
    }
}