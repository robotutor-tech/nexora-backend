package com.robotutor.nexora.context.user.interfaces.controller.mapper

import com.robotutor.nexora.context.user.application.command.RegisterUserCommand
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.context.user.interfaces.controller.view.UserRequest
import com.robotutor.nexora.context.user.interfaces.controller.view.UserResponse
import com.robotutor.nexora.shared.domain.vo.Name

object UserMapper {
    fun toRegisterUserCommand(userRequest: UserRequest): RegisterUserCommand {
        return RegisterUserCommand(
            name = Name(userRequest.name),
            email = Email(userRequest.email),
            mobile = Mobile(userRequest.mobile)
        )
    }

    fun toUserResponse(userAggregate: UserAggregate): UserResponse {
        return UserResponse(
            userId = userAggregate.userId.value,
            accountId = userAggregate.accountId?.value,
            state = userAggregate.state.name,
            name = userAggregate.name.value,
            email = userAggregate.email.value,
            mobile = userAggregate.mobile.value,
            isEmailVerified = userAggregate.email.isVerified,
            isMobileVerified = userAggregate.mobile.isVerified,
            registeredAt = userAggregate.registeredAt,
            updatedAt = userAggregate.updatedAt
        )
    }
}
