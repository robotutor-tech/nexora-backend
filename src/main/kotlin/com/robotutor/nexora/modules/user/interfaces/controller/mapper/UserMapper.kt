package com.robotutor.nexora.modules.user.interfaces.controller.mapper

import com.robotutor.nexora.modules.auth.domain.entity.Password
import com.robotutor.nexora.modules.user.application.command.RegisterUserCommand
import com.robotutor.nexora.modules.user.domain.entity.User
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserRequest
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserResponse
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.Mobile
import com.robotutor.nexora.shared.domain.model.Name

object UserMapper {
    fun toRegisterUserCommand(userRequest: UserRequest): RegisterUserCommand {
        return RegisterUserCommand(
            name = Name(userRequest.name),
            email = Email(userRequest.email),
            password = Password(userRequest.password),
            mobile = Mobile(userRequest.mobile)
        )
    }

    fun toUserResponse(user: User): UserResponse {
        return UserResponse(
            userId = user.userId.value,
            name = user.name.value,
            email = user.email.value,
            registeredAt = user.registeredAt,
            mobile = user.mobile.value,
            isEmailVerified = user.isEmailVerified,
            isMobileVerified = user.isMobileVerified,
        )
    }
}
