package com.robotutor.nexora.module.user.interfaces.messaging

import com.robotutor.nexora.common.message.annotation.EventController
import com.robotutor.nexora.common.message.annotation.EventListener
import com.robotutor.nexora.common.message.annotation.Message
import com.robotutor.nexora.module.user.application.service.ActivateUserService
import com.robotutor.nexora.module.user.application.service.CompensateUserService
import com.robotutor.nexora.module.user.domain.aggregate.UserAggregate
import com.robotutor.nexora.module.user.interfaces.messaging.mapper.UserEventMapper
import com.robotutor.nexora.module.user.interfaces.messaging.message.ActivateUserMessage
import com.robotutor.nexora.module.user.interfaces.messaging.message.CompensateUserMessage
import com.robotutor.nexora.module.zone.infrastructure.messaging.message.ZoneCreatedEventMessage
import com.robotutor.nexora.shared.utility.createMono
import reactor.core.publisher.Mono

@Suppress("UNUSED")
@EventController
class UserEventController(
    private val activateUserService: ActivateUserService,
    private val compensateUserService: CompensateUserService
) {

    @EventListener(["iam.account.registered.user"])
    fun activateUser(@Message message: ActivateUserMessage): Mono<UserAggregate> {
        val command = UserEventMapper.toActivateUserCommand(message)
        return activateUserService.execute(command)
    }

    @EventListener(["iam.account.registration.failed.user"])
    fun compensateUser(@Message message: CompensateUserMessage): Mono<UserAggregate> {
        val command = UserEventMapper.toCompensateUserCommand(message)
        return compensateUserService.execute(command)
    }


    @EventListener(["zone.created"])
    fun zoneCreatedEvent(@Message message: ZoneCreatedEventMessage): Mono<ZoneCreatedEventMessage> {
        println("================================>>>>>>>>>>>>>message: $message")
        return createMono(message)
    }
}