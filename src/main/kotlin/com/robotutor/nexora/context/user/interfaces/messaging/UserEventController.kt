package com.robotutor.nexora.context.user.interfaces.messaging

import com.robotutor.nexora.context.user.application.usecase.ActivateUserUseCase
import com.robotutor.nexora.context.user.application.usecase.CompensateUserRegistrationUseCase
import com.robotutor.nexora.context.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.context.user.interfaces.messaging.mapper.UserEventMapper
import com.robotutor.nexora.context.user.interfaces.messaging.message.CompensateUserRegistrationMessage
import com.robotutor.nexora.context.user.interfaces.messaging.message.UserAccountCreatedMessage
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class UserEventController(
    private val activateUserUseCase: ActivateUserUseCase,
    private val compensateUserRegistrationUseCase: CompensateUserRegistrationUseCase
) {

    @KafkaEventListener(["iam.account.created.human"])
    fun activateUser(@KafkaEvent eventMessage: UserAccountCreatedMessage): Mono<UserAggregate> {
        val command = UserEventMapper.toActivateUserCommand(eventMessage)
        return activateUserUseCase.execute(command)
    }

    @KafkaEventListener(["orchestration.compensate.user-registration"])
    fun compensateUser(@KafkaEvent eventMessage: CompensateUserRegistrationMessage): Mono<UserAggregate> {
        val command = UserEventMapper.toCompensateUserRegistrationCommand(eventMessage)
        return compensateUserRegistrationUseCase.execute(command)
    }
}