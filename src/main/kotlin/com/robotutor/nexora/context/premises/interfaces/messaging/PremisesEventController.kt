package com.robotutor.nexora.context.premises.interfaces.messaging

import com.robotutor.nexora.context.premises.application.usecase.ActivatePremisesUseCase
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.interfaces.messaging.mapper.PremisesEventMapper
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesActivateMessage
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class PremisesEventController(private val activatePremisesUseCase: ActivatePremisesUseCase) {
    @KafkaEventListener(["iam.premises.resource.created"])
    fun activatePremises(
        @KafkaEvent eventMessage: PremisesActivateMessage,
        accountData: AccountData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toActivatePremisesCommand(eventMessage, accountData)
        return activatePremisesUseCase.execute(command)
    }
}