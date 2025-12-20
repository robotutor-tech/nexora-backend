package com.robotutor.nexora.context.iam.interfaces.messaging

import com.robotutor.nexora.context.iam.application.usecase.account.ActivateAccountUseCase
import com.robotutor.nexora.context.iam.application.usecase.account.CompensateAccountUseCase
import com.robotutor.nexora.context.iam.application.usecase.RegisterPremisesResourceUseCase
import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.interfaces.messaging.mapper.IAMMapper
import com.robotutor.nexora.context.iam.interfaces.messaging.message.AccountActivatedMessage
import com.robotutor.nexora.context.iam.interfaces.messaging.message.AccountCompensateMessage
import com.robotutor.nexora.context.iam.interfaces.messaging.message.PremisesRegisteredMessage
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@KafkaController
class IAMEventController(
    private val registerPremisesResourceUseCase: RegisterPremisesResourceUseCase,
    private val activateAccountUseCase: ActivateAccountUseCase,
    private val compensateAccountUseCase: CompensateAccountUseCase
) {
    @KafkaEventListener(["premises.registered"])
    fun registerPremisesResource(
        @KafkaEvent eventMessage: PremisesRegisteredMessage,
        accountData: AccountData
    ): Mono<ActorAggregate> {
        val command = IAMMapper.toRegisterPremisesResourceCommand(eventMessage, accountData)
        return registerPremisesResourceUseCase.execute(command)
    }

    @KafkaEventListener(["orchestration.compensate.account-registration"])
    fun compensateAccount(@KafkaEvent eventMessage: AccountCompensateMessage): Mono<AccountAggregate> {
        val command = IAMMapper.toCompensateAccountCommand(eventMessage)
        return compensateAccountUseCase.execute(command)
    }

    @KafkaEventListener(["user.registered"])
    fun activateAccount(@KafkaEvent eventMessage: AccountActivatedMessage): Mono<AccountAggregate> {
        val command = IAMMapper.toAccountActivatedCommand(eventMessage)
        return activateAccountUseCase.execute(command)
    }
}