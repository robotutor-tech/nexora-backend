package com.robotutor.nexora.module.premises.interfaces.messaging

import com.robotutor.nexora.module.premises.application.service.ActivatePremisesService
import com.robotutor.nexora.module.premises.application.service.CompensatePremisesRegistrationService
import com.robotutor.nexora.module.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.module.premises.interfaces.messaging.mapper.PremisesEventMapper
import com.robotutor.nexora.module.premises.interfaces.messaging.message.PremisesOwnerRegisteredMessage
import com.robotutor.nexora.module.premises.interfaces.messaging.message.PremisesOwnerRegistrationFailedMessage
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.common.message.annotation.EventController
import com.robotutor.nexora.common.message.annotation.EventListener
import com.robotutor.nexora.common.message.annotation.Message
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@EventController
class PremisesEventController(
    private val activatePremisesService: ActivatePremisesService,
    private val compensatePremisesRegistrationService: CompensatePremisesRegistrationService
) {
    @EventListener(["iam.premises.owner.registered"])
    fun activatePremises(
        @Message message: PremisesOwnerRegisteredMessage,
        accountData: AccountData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toActivatePremisesCommand(message, accountData)
        return activatePremisesService.execute(command)
    }

    @EventListener(["iam.premises.owner.registration.failed"])
    fun compensatePremisesRegistration(
        @Message message: PremisesOwnerRegistrationFailedMessage,
        accountData: AccountData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toCompensatePremisesRegistrationCommand(message, accountData)
        return compensatePremisesRegistrationService.execute(command)
    }
}