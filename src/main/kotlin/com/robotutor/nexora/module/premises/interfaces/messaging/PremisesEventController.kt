package com.robotutor.nexora.module.premises.interfaces.messaging

import com.robotutor.nexora.module.premises.application.service.ActivatePremisesService
import com.robotutor.nexora.module.premises.application.service.CompensatePremisesRegistrationService
import com.robotutor.nexora.module.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.module.premises.interfaces.messaging.mapper.PremisesEventMapper
import com.robotutor.nexora.module.premises.interfaces.messaging.message.PremisesOwnerRegisteredMessage
import com.robotutor.nexora.module.premises.interfaces.messaging.message.PremisesOwnerRegistrationFailedMessage
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.message.annotation.EventController
import com.robotutor.nexora.shared.message.annotation.EventListener
import com.robotutor.nexora.shared.message.annotation.Message
import com.robotutor.nexora.shared.message.config.EventName
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@EventController
class PremisesEventController(
    private val activatePremisesService: ActivatePremisesService,
    private val compensatePremisesRegistrationService: CompensatePremisesRegistrationService
) {

    @EventListener([EventName.IDENTITY_PREMISES_OWNER_REGISTERED])
    fun activatePremises(
        @Message message: PremisesOwnerRegisteredMessage,
        actorData: ActorData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toActivatePremisesCommand(message, actorData)
        return activatePremisesService.execute(command)
    }


    @EventListener([EventName.IDENTITY_PREMISES_OWNER_REGISTRATION_FAILED])
    fun compensatePremisesRegistration(
        @Message message: PremisesOwnerRegistrationFailedMessage,
        actorData: ActorData
    ): Mono<PremisesAggregate> {
        val command = PremisesEventMapper.toCompensatePremisesRegistrationCommand(message, actorData)
        return compensatePremisesRegistrationService.execute(command)
    }
}
