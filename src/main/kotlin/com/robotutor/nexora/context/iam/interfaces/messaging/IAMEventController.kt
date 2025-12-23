package com.robotutor.nexora.context.iam.interfaces.messaging

import com.robotutor.nexora.context.iam.application.usecase.account.ActivateAccountUseCase
import com.robotutor.nexora.context.iam.application.usecase.account.CompensateAccountUseCase
import com.robotutor.nexora.context.iam.application.usecase.RegisterOwnerUseCase
import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.interfaces.messaging.mapper.IAMMapper
import com.robotutor.nexora.context.iam.interfaces.messaging.message.AccountActivatedMessage
import com.robotutor.nexora.context.iam.interfaces.messaging.message.AccountRegistrationFailedEventMessage
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class IAMEventController(
    private val registerOwnerUseCase: RegisterOwnerUseCase,
    private val activateAccountUseCase: ActivateAccountUseCase,
    private val compensateAccountUseCase: CompensateAccountUseCase
) {
    @KafkaEventListener(["user.registration.failed", "device.registration.failed"])
    fun compensateAccount(@KafkaEvent eventMessage: AccountRegistrationFailedEventMessage): Mono<AccountAggregate> {
        val command = IAMMapper.toCompensateAccountCommand(eventMessage)
        return compensateAccountUseCase.execute(command)
    }

    @KafkaEventListener(["user.registered"])
    fun activateAccount(@KafkaEvent eventMessage: AccountActivatedMessage): Mono<AccountAggregate> {
        val command = IAMMapper.toAccountActivatedCommand(eventMessage)
        return activateAccountUseCase.execute(command)
    }
}