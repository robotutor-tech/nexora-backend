package com.robotutor.nexora.module.identity.infrastructure.facade.mapper

import com.robotutor.nexora.module.identity.application.command.RegisterUserAccountCommand
import com.robotutor.nexora.module.identity.domain.entity.User
import com.robotutor.nexora.module.identity.infrastructure.facade.view.UserRequest
import com.robotutor.nexora.module.identity.infrastructure.facade.view.UserResponse
import com.robotutor.nexora.shared.domain.vo.UserId

object UserMapper {
    fun toUserRequest(command: RegisterUserAccountCommand): UserRequest {
        return UserRequest(name = command.name.value, email = command.email.value, mobile = command.mobile.value)
    }

    fun toUser(userResponse: UserResponse): User {
        return User(userId = UserId(userResponse.userId))
    }
}
