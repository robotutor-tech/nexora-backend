package com.robotutor.nexora.shared.message

import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.message.services.KafkaEventPublisher
import reactor.core.publisher.Mono

//open class EventPublisherImpl<T : Event>(
//    val eventPublisher: KafkaEventPublisher,
//    val mapper: EventMapper<T>
//) : EventPublisher<T> {
//    override fun <R : Any> publish(event: T, transformer: () -> R): Mono<R> {
//        val message = mapper.toEventMessage(event)
//        return eventPublisher.publish(message)
//            .map { transformer() }
//    }
//}
