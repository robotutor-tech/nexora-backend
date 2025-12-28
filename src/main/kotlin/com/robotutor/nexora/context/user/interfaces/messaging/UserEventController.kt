package com.robotutor.nexora.context.user.interfaces.messaging

import com.robotutor.nexora.context.user.application.usecase.ActivateUserUseCase
import com.robotutor.nexora.context.user.application.usecase.CompensateUserUseCase
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.interfaces.messaging.mapper.UserEventMapper
import com.robotutor.nexora.context.user.interfaces.messaging.message.ActivateUserMessage
import com.robotutor.nexora.context.user.interfaces.messaging.message.CompensateUserMessage
import com.robotutor.nexora.common.messaging.infrastructure.annotation.KafkaController
import com.robotutor.nexora.common.messaging.infrastructure.annotation.KafkaEvent
import com.robotutor.nexora.common.messaging.infrastructure.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class UserEventController(
    private val activateUserUseCase: ActivateUserUseCase,
    private val compensateUserUseCase: CompensateUserUseCase
) {

    @KafkaEventListener(["iam.account.registered.user"])
    fun activateUser(@KafkaEvent eventMessage: ActivateUserMessage): Mono<UserAggregate> {
        val command = UserEventMapper.toActivateUserCommand(eventMessage)
        return activateUserUseCase.execute(command)
    }

    @KafkaEventListener(["iam.account.registration.failed.user"])
    fun compensateUser(@KafkaEvent eventMessage: CompensateUserMessage): Mono<UserAggregate> {
        val command = UserEventMapper.toCompensateUserCommand(eventMessage)
        return compensateUserUseCase.execute(command)
    }
}