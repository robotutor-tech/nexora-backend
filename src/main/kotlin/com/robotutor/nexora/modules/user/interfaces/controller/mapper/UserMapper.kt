package com.robotutor.nexora.modules.user.interfaces.controller.mapper

import com.robotutor.nexora.modules.user.application.command.RegisterUserCommand
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserRequest
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserResponse
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.UserData


object UserMapper {
    fun toRegisterUserCommand(userRequest: UserRequest): RegisterUserCommand {
        return RegisterUserCommand(
            name = userRequest.name,
            email = Email(userRequest.email),
            password = userRequest.password
        )
    }

    fun toUserResponse(user: User): UserResponse {
        return UserResponse(
            userId = user.userId.value,
            name = user.name,
            email = user.email.value,
            registeredAt = user.registeredAt,
        )
    }

    fun toUserResponse(user: UserData): UserResponse {
        return UserResponse(
            userId = user.userId.value,
            name = user.name,
            email = user.email.value,
            registeredAt = user.registeredAt,
        )
    }
}