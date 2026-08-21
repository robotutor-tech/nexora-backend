package com.robotutor.nexora.module.identity.infrastructure.facade

import com.robotutor.nexora.module.identity.application.command.RegisterUserAccountCommand
import com.robotutor.nexora.module.identity.application.facade.UserFacade
import com.robotutor.nexora.module.identity.domain.entity.User
import com.robotutor.nexora.module.identity.infrastructure.facade.mapper.UserMapper
import com.robotutor.nexora.module.identity.infrastructure.facade.view.UserResponse
import com.robotutor.nexora.shared.webclient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserFacadeImpl(private val webClient: WebClientWrapper, private val config: IdentityConfig) : UserFacade {
    override fun register(command: RegisterUserAccountCommand): Mono<User> {
        return webClient.post(
            baseUrl = config.userBaseUrl,
            path = config.registerUserPath,
            body = UserMapper.toUserRequest(command),
            returnType = UserResponse::class.java
        )
            .map {
                UserMapper.toUser(it)
            }

    }
}
