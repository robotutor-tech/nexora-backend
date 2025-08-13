package com.robotutor.nexora.modules.user.interfaces.controller.mapper

import com.robotutor.nexora.modules.user.application.command.RegisterUserCommand
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserRequest
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserResponse


object UserMapper {
    fun toRegisterUserCommand(userRequest: UserRequest): RegisterUserCommand {
        return RegisterUserCommand(
            name = userRequest.name,
            email = userRequest.email,
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
}