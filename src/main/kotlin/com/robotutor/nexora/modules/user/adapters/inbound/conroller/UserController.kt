package com.robotutor.nexora.modules.user.adapters.inbound.conroller

import com.robotutor.nexora.modules.user.adapters.inbound.conroller.dto.UserRequest
import com.robotutor.nexora.modules.user.adapters.inbound.conroller.dto.UserResponse
import com.robotutor.nexora.modules.user.adapters.inbound.conroller.mapper.UserMapper
import com.robotutor.nexora.modules.user.application.UserUseCase
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UserController(val userUseCase: UserUseCase) {

    @PostMapping("/register")
    fun register(@RequestBody @Validated userRequest: UserRequest): Mono<UserResponse> {
        return userUseCase.register(UserMapper.toUserDetails(userRequest))
            .map { UserMapper.toUserResponse(it) }
    }
}