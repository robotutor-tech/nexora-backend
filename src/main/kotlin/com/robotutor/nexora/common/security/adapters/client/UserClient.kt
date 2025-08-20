package com.robotutor.nexora.common.security.adapters.client

import com.robotutor.nexora.common.security.application.ports.UserDataRetriever
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.modules.user.interfaces.controller.UserController
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.UserId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("SecurityUserClient")
class UserClient(private val userController: UserController) : UserDataRetriever {
    override fun getUserData(userId: String): Mono<UserData> {
        return userController.getUser(userId)
            .map {
                UserData(
                    userId = UserId(it.userId),
                    name = it.name,
                    email = Email(it.email),
                    registeredAt = it.registeredAt
                )
            }
    }
}