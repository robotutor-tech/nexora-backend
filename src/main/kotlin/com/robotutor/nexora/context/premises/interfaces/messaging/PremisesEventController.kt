package com.robotutor.nexora.context.premises.interfaces.messaging

import com.robotutor.nexora.context.premises.application.service.ActivatePremisesService
import com.robotutor.nexora.context.premises.application.service.CompensatePremisesRegistrationService
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.interfaces.messaging.mapper.PremisesEventMapper
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesOwnerRegisteredMessage
import com.robotutor.nexora.context.premises.interfaces.messaging.message.PremisesOwnerRegistrationFailedMessage
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.common.messaging.annotation.KafkaController
import com.robotutor.nexora.common.messaging.annotation.KafkaEvent
import com.robotutor.nexora.common.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class PremisesEventController(
    private val activatePremisesService: ActivatePremisesService,
    private val compensatePremisesRegistrationService: CompensatePremisesRegistrationService
) {
    @KafkaEventListener(["iam.premises.owner.registered"])
    fun activatePremises(
        @KafkaEvent eventMessage: PremisesOwnerRegisteredMessage,
        accountData: AccountData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toActivatePremisesCommand(eventMessage, accountData)
        return activatePremisesService.execute(command)
    }

    @KafkaEventListener(["iam.premises.owner.registration.failed"])
    fun compensatePremisesRegistration(
        @KafkaEvent eventMessage: PremisesOwnerRegistrationFailedMessage,
        accountData: AccountData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toCompensatePremisesRegistrationCommand(eventMessage, accountData)
        return compensatePremisesRegistrationService.execute(command)
    }
}