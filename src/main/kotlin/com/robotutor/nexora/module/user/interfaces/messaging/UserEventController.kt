package com.robotutor.nexora.module.user.interfaces.messaging

import com.robotutor.nexora.shared.message.annotation.EventController
import com.robotutor.nexora.shared.message.annotation.EventListener
import com.robotutor.nexora.shared.message.annotation.Message
import com.robotutor.nexora.module.user.application.service.ActivateUserService
import com.robotutor.nexora.module.user.application.service.CompensateUserService
import com.robotutor.nexora.module.user.domain.aggregate.User
import com.robotutor.nexora.module.user.interfaces.messaging.mapper.UserEventMapper
import com.robotutor.nexora.module.user.interfaces.messaging.message.ActivateUserMessage
import com.robotutor.nexora.module.user.interfaces.messaging.message.CompensateUserMessage
import com.robotutor.nexora.shared.message.config.EventName
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@EventController
class UserEventController(
    private val activateUserService: ActivateUserService,
    private val compensateUserService: CompensateUserService
) {

    @EventListener([EventName.IDENTITY_ACCOUNT_REGISTERED_USER])
    fun activateUser(@Message message: ActivateUserMessage): Mono<User> {
        val command = UserEventMapper.toActivateUserCommand(message)
        return activateUserService.execute(command)
    }

    @EventListener([EventName.IDENTITY_ACCOUNT_REGISTRATION_FAILED_USER])
    fun compensateUser(@Message message: CompensateUserMessage): Mono<User> {
        val command = UserEventMapper.toCompensateUserCommand(message)
        return compensateUserService.execute(command)
    }
}
