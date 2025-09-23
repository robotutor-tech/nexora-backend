package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.UserDataRetriever
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.modules.user.interfaces.controller.UserController
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.UserId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("SecurityUserClient")
class UserClient(private val userController: UserController) : UserDataRetriever {
    override fun getUserData(userId: UserId): Mono<UserData> {
        return userController.getUser(userId.value)
            .map {
                UserData(
                    userId = UserId(it.userId),
                    name = Name(it.name),
                    email = Email(it.email),
                    registeredAt = it.registeredAt
                )
            }
    }
}
