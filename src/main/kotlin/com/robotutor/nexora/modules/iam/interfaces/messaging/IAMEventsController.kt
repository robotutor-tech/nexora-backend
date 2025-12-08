//package com.robotutor.nexora.modules.iam.interfaces.messaging
//
//import com.robotutor.nexora.modules.iam.application.handler.ResourceCreatedEventHandlerUserCase
//import com.robotutor.nexora.modules.iam.domain.entity.Role
//import com.robotutor.nexora.modules.iam.interfaces.messaging.mapper.ResourceCreatedEventMapper
//import com.robotutor.nexora.shared.domain.model.ActorData
//import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaController
//import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEvent
//import com.robotutor.nexora.shared.infrastructure.messaging.annotation.KafkaEventListener
//import com.robotutor.nexora.shared.infrastructure.messaging.message.ResourceCreatedEventMessage
//import reactor.core.publisher.Mono
//
//@KafkaController
//class IAMEventsController(private val resourceCreatedEventHandlerUserCase: ResourceCreatedEventHandlerUserCase) {
//
//    @Suppress("UNUSED")
//    @KafkaEventListener(["shared.resource.created"])
//    fun resourceCreatedEventHandler(
//        @KafkaEvent event: ResourceCreatedEventMessage,
//        actorData: ActorData
//    ): Mono<List<Role>> {
//        val resourceCreatedEvent = ResourceCreatedEventMapper.toResourceCreatedEvent(event)
//        return resourceCreatedEventHandlerUserCase.createResource(resourceCreatedEvent, actorData)
//    }
//}