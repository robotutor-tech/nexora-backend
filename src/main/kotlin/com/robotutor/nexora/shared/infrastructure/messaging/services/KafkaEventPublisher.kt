package com.robotutor.nexora.shared.infrastructure.messaging.services

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.shared.infrastructure.serializer.DefaultSerializer
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
import reactor.kafka.sender.SenderResult
import reactor.util.context.ContextView

@Service
class KafkaEventPublisher(
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>,
) {
    val logger = Logger(this::class.java)

    fun publish(message: EventMessage): Mono<SenderResult<Void>> {
        val messageAsString = DefaultSerializer.serialize(message)
        return Mono.deferContextual { ctx ->
            val headers = createHeadersRecord(ctx)
            val producerRecord = ProducerRecord(message.eventName, "", messageAsString)
            headers.forEach { producerRecord.headers().add(it) }
            reactiveKafkaProducerTemplate.send(producerRecord)
        }
            .logOnSuccess(logger, "Successfully published kafka topic to ${message.eventName}")
            .logOnError(logger, "", "Failed to publish kafka topic to ${message.eventName}")
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
