package com.robotutor.nexora.module.user.interfaces.controller.mapper

import com.robotutor.nexora.module.user.application.command.RegisterUserCommand
import com.robotutor.nexora.module.user.domain.aggregate.User
import com.robotutor.nexora.module.user.domain.vo.Email
import com.robotutor.nexora.module.user.domain.vo.Mobile
import com.robotutor.nexora.shared.domain.vo.FullName
import com.robotutor.nexora.module.user.interfaces.controller.view.UserRequest
import com.robotutor.nexora.module.user.interfaces.controller.view.UserResponse

object UserMapper {
    fun toRegisterUserCommand(userRequest: UserRequest): RegisterUserCommand {
        return RegisterUserCommand(
            fullName = FullName.of(userRequest.name),
            email = Email(userRequest.email),
            mobile = Mobile(userRequest.mobile)
        )
    }

    fun toUserResponse(user: User): UserResponse {
        return UserResponse(
            userId = user.userId.value,
            state = user.state().name,
            name = user.fullName.value,
            email = user.email.value,
            mobile = user.mobile.value,
            isEmailVerified = user.email.isVerified,
            isMobileVerified = user.mobile.isVerified,
            registeredAt = user.registeredAt,
            updatedAt = user.updatedAt()
        )
    }
}
