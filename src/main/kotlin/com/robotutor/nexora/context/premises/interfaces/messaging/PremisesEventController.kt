package com.robotutor.nexora.context.premises.interfaces.messaging

import com.robotutor.nexora.context.premises.application.usecase.ActivatePremisesUseCase
import com.robotutor.nexora.context.premises.application.usecase.CompensatePremisesRegistrationUseCase
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.interfaces.messaging.mapper.PremisesEventMapper
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesOwnerRegisteredMessage
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesOwnerRegistrationFailedMessage
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class PremisesEventController(
    private val activatePremisesUseCase: ActivatePremisesUseCase,
    private val compensatePremisesRegistrationUseCase: CompensatePremisesRegistrationUseCase
) {
    @KafkaEventListener(["iam.premises.owner.registered"])
    fun activatePremises(
        @KafkaEvent eventMessage: PremisesOwnerRegisteredMessage,
        AccountData: AccountData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toActivatePremisesCommand(eventMessage, AccountData)
        return activatePremisesUseCase.execute(command)
    }

    @KafkaEventListener(["iam.premises.owner.registration.failed"])
    fun compensatePremisesRegistration(
        @KafkaEvent eventMessage: PremisesOwnerRegistrationFailedMessage,
        AccountData: AccountData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toCompensatePremisesRegistrationCommand(eventMessage, AccountData)
        return compensatePremisesRegistrationUseCase.execute(command)
    }
}