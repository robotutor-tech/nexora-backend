package com.robotutor.nexora.modules.user.adapters.inbound.conroller.mapper

import com.robotutor.nexora.modules.user.adapters.inbound.conroller.dto.UserRequest
import com.robotutor.nexora.modules.user.adapters.inbound.conroller.dto.UserResponse
import com.robotutor.nexora.modules.user.domain.model.Email
import com.robotutor.nexora.modules.user.domain.model.User
import com.robotutor.nexora.modules.user.domain.model.UserDetails


object UserMapper {
    fun toUserDetails(userRequest: UserRequest): UserDetails {
        return UserDetails(
            name = userRequest.name,
            email = Email(userRequest.email),
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