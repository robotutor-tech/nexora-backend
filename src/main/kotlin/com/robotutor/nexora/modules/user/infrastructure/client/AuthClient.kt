package com.robotutor.nexora.modules.user.infrastructure.client

import com.robotutor.nexora.modules.auth.interfaces.controller.AuthController
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.AuthUserRequest
import com.robotutor.nexora.modules.user.application.command.RegisterAuthUserCommand
import com.robotutor.nexora.modules.user.application.service.RegisterAuthUser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthClient(private val authController: AuthController) : RegisterAuthUser {
    override fun register(registerAuthUserCommand: RegisterAuthUserCommand): Mono<RegisterAuthUserCommand> {
        val authUserRequest = AuthUserRequest(
            userId = registerAuthUserCommand.userId.value,
            email = registerAuthUserCommand.email.value,
            password = registerAuthUserCommand.password.value
        )
        return authController.register(authUserRequest).map { registerAuthUserCommand }
    }
}