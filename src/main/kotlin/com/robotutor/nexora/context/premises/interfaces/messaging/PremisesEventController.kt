package com.robotutor.nexora.context.premises.interfaces.messaging

import com.robotutor.nexora.context.premises.application.usecase.ActivatePremisesUseCase
import com.robotutor.nexora.context.premises.application.usecase.CompensatePremisesRegistrationUseCase
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.interfaces.messaging.mapper.PremisesEventMapper
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesActivateMessage
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesCompensateMessage
import com.robotutor.nexora.shared.domain.vo.AccountData
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
    @KafkaEventListener(["iam.premises.resource.created"])
    fun activatePremises(
        @KafkaEvent eventMessage: PremisesActivateMessage,
        accountData: AccountData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toActivatePremisesCommand(eventMessage, accountData)
        return activatePremisesUseCase.execute(command)
    }

    @KafkaEventListener(["orchestration.compensate.premises-registration"])
    fun compensatePremisesRegistration(
        @KafkaEvent eventMessage: PremisesCompensateMessage,
        accountData: AccountData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toCompensatePremisesRegistrationCommand(eventMessage, accountData)
        return compensatePremisesRegistrationUseCase.execute(command)
    }
}