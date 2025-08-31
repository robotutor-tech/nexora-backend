package com.robotutor.nexora.shared.infrastructure.messaging.services

import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.shared.infrastructure.jackson.DefaultSerializer
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.ReactiveContext.getTraceId
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.shared.logger.models.ServerWebExchangeDTO
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.util.context.ContextView

@Service
class KafkaEventPublisher(
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>,
) : EventPublisher {
    val logger = Logger(this::class.java)

    override fun <T : DomainEvent, R : Any> publish(event: T, transformer: () -> R): Mono<R> {
        val messageAsString = DefaultSerializer.serialize(event)
        val topic = event.eventName.value
        return Mono.deferContextual { ctx ->
            val headers = createHeadersRecord(ctx)
            val producerRecord = ProducerRecord(topic, "", messageAsString)
            headers.forEach { producerRecord.headers().add(it) }
            reactiveKafkaProducerTemplate.send(producerRecord)
                .map { transformer() }
        }
            .logOnSuccess(logger, "Successfully published kafka topic to $topic")
            .logOnError(logger, "", "Failed to publish kafka topic to $topic")
    }

    private fun createHeadersRecord(ctx: ContextView): MutableList<RecordHeader> {
        val traceId = getTraceId(ctx)
        val exchangeDTO = ctx.get(ServerWebExchangeDTO::class.java)
        val actorData = ctx.getOrEmpty<ActorData>(ActorData::class.java)
        val userData = ctx.getOrEmpty<UserData>(UserData::class.java)

        val headers = mutableListOf<RecordHeader>()
        headers.add(RecordHeader("exchange", DefaultSerializer.serialize(exchangeDTO).toByteArray()))
        headers.add(RecordHeader("x-trace-id", traceId.toByteArray()))
        if (actorData.isPresent) {
            headers.add(
                RecordHeader("actorData", DefaultSerializer.serialize(actorData.get()).toByteArray())
            )
        }
        if (userData.isPresent) {
            headers.add(
                RecordHeader("userData", DefaultSerializer.serialize(userData.get()).toByteArray())
            )
        }
        return headers
    }
}
