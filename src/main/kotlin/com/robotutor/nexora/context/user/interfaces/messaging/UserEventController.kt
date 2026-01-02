package com.robotutor.nexora.context.user.interfaces.messaging

import com.robotutor.nexora.common.messaging.annotation.KafkaController
import com.robotutor.nexora.common.messaging.annotation.KafkaEvent
import com.robotutor.nexora.common.messaging.annotation.KafkaEventListener
import com.robotutor.nexora.context.user.application.service.ActivateUserService
import com.robotutor.nexora.context.user.application.service.CompensateUserService
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.interfaces.messaging.mapper.UserEventMapper
import com.robotutor.nexora.context.user.interfaces.messaging.message.ActivateUserMessage
import com.robotutor.nexora.context.user.interfaces.messaging.message.CompensateUserMessage
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class UserEventController(
    private val activateUserService: ActivateUserService,
    private val compensateUserService: CompensateUserService
) {

    @KafkaEventListener(["iam.account.registered.user"])
    fun activateUser(@KafkaEvent eventMessage: ActivateUserMessage): Mono<UserAggregate> {
        val command = UserEventMapper.toActivateUserCommand(eventMessage)
        return activateUserService.execute(command)
    }

    @KafkaEventListener(["iam.account.registration.failed.user"])
    fun compensateUser(@KafkaEvent eventMessage: CompensateUserMessage): Mono<UserAggregate> {
        val command = UserEventMapper.toCompensateUserCommand(eventMessage)
        return compensateUserService.execute(command)
    }
}