package com.robotutor.nexora.modules.user.interfaces.controller

import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserRequest
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserResponse
import com.robotutor.nexora.modules.user.interfaces.controller.mapper.UserMapper
import com.robotutor.nexora.modules.user.application.RegisterUserUseCase
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UserController(val registerUserUseCase: RegisterUserUseCase) {

    @PostMapping("/register")
    fun register(@RequestBody @Validated userRequest: UserRequest): Mono<UserResponse> {
        val registerUserCommand = UserMapper.toRegisterUserCommand(userRequest)
        return registerUserUseCase.register(registerUserCommand)
            .map { UserMapper.toUserResponse(it) }
    }
}