package com.robotutor.nexora.context.user.interfaces.controller

import com.robotutor.nexora.context.user.application.command.GetUserQuery
import com.robotutor.nexora.context.user.application.service.GetUserService
import com.robotutor.nexora.context.user.application.service.RegisterUserService
import com.robotutor.nexora.context.user.interfaces.controller.mapper.UserMapper
import com.robotutor.nexora.context.user.interfaces.controller.view.UserRequest
import com.robotutor.nexora.context.user.interfaces.controller.view.UserResponse
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
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
    fun register(@RequestBody @Validated userRequest: UserRequest): Mono<UserResponse> {
        val command = UserMapper.toRegisterUserCommand(userRequest)
        return registerUserService.execute(command)
            .map { UserMapper.toUserResponse(it) }
    }

    @GetMapping("/me")
    fun me(accountData: AccountData): Mono<UserResponse> {
        return getUserService.execute(GetUserQuery(accountData.principalId))
            .map { UserMapper.toUserResponse(it) }
    }

    @GetMapping("/{accountId}")
    fun getUser(@PathVariable accountId: String): Mono<UserResponse> {
        return getUserService.execute(GetUserQuery(PrincipalId(accountId)))
            .map { UserMapper.toUserResponse(it) }
    }
}