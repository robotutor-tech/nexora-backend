package com.robotutor.nexora.module.user.interfaces.messaging

import com.robotutor.nexora.shared.message.annotation.EventController
import com.robotutor.nexora.shared.message.annotation.EventListener
import com.robotutor.nexora.shared.message.annotation.Message
import com.robotutor.nexora.module.user.application.service.ActivateUserService
import com.robotutor.nexora.module.user.application.service.CompensateUserAccountCreationService
import com.robotutor.nexora.module.user.domain.aggregate.User
import com.robotutor.nexora.module.user.interfaces.messaging.mapper.UserEventMapper
import com.robotutor.nexora.module.user.interfaces.messaging.message.ActivateUserMessage
import com.robotutor.nexora.module.user.interfaces.messaging.message.CompensateUserAccountCreationMessage
import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.consumer.KafkaConsumer
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@EventController
class UserEventConsumer(
    private val activateUserService: ActivateUserService,
    private val compensateUserAccountCreationService: CompensateUserAccountCreationService
) : KafkaConsumer {

    @EventListener([EventName.USER_ACCOUNT_CREATED])
    fun activateUser(@Message message: ActivateUserMessage): Mono<User> {
        val command = UserEventMapper.toActivateUserCommand(message)
        return activateUserService.execute(command)
    }

    @EventListener([EventName.USER_ACCOUNT_CREATION_FAILED])
    fun compensateUser(@Message message: CompensateUserAccountCreationMessage): Mono<User> {
        val command = UserEventMapper.toCompensateUserAccountCreationCommand(message)
        return compensateUserAccountCreationService.execute(command)
    }
}
