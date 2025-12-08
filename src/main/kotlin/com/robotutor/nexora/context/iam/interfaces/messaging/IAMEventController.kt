package com.robotutor.nexora.context.iam.interfaces.messaging

import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.context.iam.application.usecase.RegisterPremisesResourceUseCase
import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.interfaces.messaging.mapper.IAMMapper
import com.robotutor.nexora.context.iam.interfaces.messaging.message.PremisesRegisteredMessage
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class IAMEventController(
    private val registerPremisesResourceUseCase: RegisterPremisesResourceUseCase,
) {

    @KafkaEventListener(["premises.registered"])
    fun registerPremisesResource(
        @KafkaEvent eventMessage: PremisesRegisteredMessage,
        accountData: AccountData
    ): Mono<ActorAggregate> {
        val command = IAMMapper.toRegisterPremisesResourceCommand(eventMessage, accountData)
        return registerPremisesResourceUseCase.execute(command)
    }

}