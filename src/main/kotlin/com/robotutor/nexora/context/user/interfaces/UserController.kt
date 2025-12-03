package com.robotutor.nexora.context.user.interfaces

import com.robotutor.nexora.context.user.application.usecase.RegisterUserUseCase
import com.robotutor.nexora.context.user.application.UserUseCase
import com.robotutor.nexora.context.user.application.command.GetUserQuery
import com.robotutor.nexora.context.user.interfaces.dto.UserRequest
import com.robotutor.nexora.context.user.interfaces.dto.UserResponse
import com.robotutor.nexora.context.user.interfaces.mapper.UserMapper
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
        val command = UserMapper.toRegisterUserCommand(userRequest)
        return registerUserUseCase.execute(command)
            .map { UserMapper.toUserResponse(it) }
    }

    @GetMapping("/me")
    fun me(userData: UserData): Mono<UserResponse> {
        return userUseCase.getUser(GetUserQuery(userData.userId))
            .map { UserMapper.toUserResponse(it) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String): Mono<UserResponse> {
        return userUseCase.getUser(GetUserQuery(UserId(userId)))
            .map { UserMapper.toUserResponse(it) }
    }
}