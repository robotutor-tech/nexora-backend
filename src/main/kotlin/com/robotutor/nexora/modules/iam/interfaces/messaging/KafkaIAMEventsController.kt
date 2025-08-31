package com.robotutor.nexora.modules.iam.interfaces.messaging

import com.robotutor.nexora.modules.iam.application.handler.ResourceCreatedEventHandlerUserCase
import com.robotutor.nexora.modules.iam.domain.model.Role
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.model.ActorData
import reactor.core.publisher.Mono

@com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
class KafkaIAMEventsController(private val resourceCreatedEventHandlerUserCase: ResourceCreatedEventHandlerUserCase) {

    @Suppress("UNUSED")
    @com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener(["shared.resource.created"])
    fun resourceCreatedEventHandler(@com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent event: ResourceCreatedEvent, actorData: ActorData): Mono<List<Role>> {
        return resourceCreatedEventHandlerUserCase.handle(event, actorData)
    }
}