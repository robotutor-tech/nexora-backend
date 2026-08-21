package com.robotutor.nexora.module.user.interfaces.controller

import com.robotutor.nexora.module.user.application.service.GetUserService
import com.robotutor.nexora.module.user.application.service.RegisterUserService
import com.robotutor.nexora.module.user.interfaces.controller.mapper.UserMapper
import com.robotutor.nexora.module.user.interfaces.controller.view.UserRequest
import com.robotutor.nexora.module.user.interfaces.controller.view.UserResponse
import com.robotutor.nexora.shared.domain.vo.UserData
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UserController(
    val registerUserService: RegisterUserService,
    private val getUserService: GetUserService
) {

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    fun register(@RequestBody request: UserRequest): Mono<UserResponse> {
        val command = UserMapper.toRegisterUserCommand(request)
        return registerUserService.execute(command)
            .map { UserMapper.toUserResponse(it) }
    }

    @GetMapping("/me")
    fun me(userData: UserData): Mono<UserResponse> {
        return getUserService.execute(userData.userId)
            .map { UserMapper.toUserResponse(it) }
    }
}
