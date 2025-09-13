package com.robotutor.nexora.modules.user.interfaces.controller

import com.robotutor.nexora.modules.user.application.RegisterUserUseCase
import com.robotutor.nexora.modules.user.application.UserUseCase
import com.robotutor.nexora.modules.user.application.command.GetUserCommand
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserRequest
import com.robotutor.nexora.modules.user.interfaces.controller.dto.UserResponse
import com.robotutor.nexora.modules.user.interfaces.controller.mapper.UserMapper
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.shared.domain.model.UserId
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UserController(val registerUserUseCase: RegisterUserUseCase, private val userUseCase: UserUseCase) {

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    fun register(@RequestBody @Validated userRequest: UserRequest): Mono<UserResponse> {
        val registerUserCommand = UserMapper.toRegisterUserCommand(userRequest)
        return registerUserUseCase.register(registerUserCommand)
            .map { UserMapper.toUserResponse(it) }
    }

    @GetMapping("/me")
    fun me(userData: UserData): Mono<UserResponse> {
        return userUseCase.getUser(GetUserCommand(userData.userId))
            .map { UserMapper.toUserResponse(it) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String): Mono<UserResponse> {
        return userUseCase.getUser(GetUserCommand(UserId(userId)))
            .map { UserMapper.toUserResponse(it) }
    }
}