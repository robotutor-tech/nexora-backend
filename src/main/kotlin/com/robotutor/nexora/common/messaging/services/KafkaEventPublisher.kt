package com.robotutor.nexora.common.messaging.services

import com.robotutor.nexora.shared.application.serialization.DefaultSerializer
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.application.logger.ReactiveContext.X_PREMISES_ID
import com.robotutor.nexora.shared.application.logger.ReactiveContext.getCorrelationId
import com.robotutor.nexora.shared.application.logger.ReactiveContext.getPremisesId
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.http.HttpHeaders
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

    fun publish(message: com.robotutor.nexora.common.messaging.message.EventMessage): Mono<SenderResult<Void>> {
        val messageAsString = DefaultSerializer.serialize(message)
        return Mono.deferContextual { ctx ->
            val headers = createHeadersRecord(ctx)
            val producerRecord = ProducerRecord(message.eventName, "", messageAsString)
            headers.forEach { producerRecord.headers().add(it) }
            reactiveKafkaProducerTemplate.send(producerRecord)
        }
            .logOnSuccess(logger, "Successfully published kafka topic to ${message.eventName}")
            .logOnError(logger, "Failed to publish kafka topic to ${message.eventName}")
    }

    private fun createHeadersRecord(ctx: ContextView): MutableList<RecordHeader> {
        val httpHeaders = ctx.getOrEmpty<HttpHeaders>(HttpHeaders::class.java)
        val actorData = ctx.getOrEmpty<ActorData>(ActorData::class.java)
        val accountData = ctx.getOrEmpty<AccountData>(AccountData::class.java)
        val correlationId = getCorrelationId(ctx)
        val premisesId = getPremisesId(ctx)


        val headers = mutableListOf<RecordHeader>()
        headers.add(RecordHeader(CORRELATION_ID, correlationId.toByteArray()))
        headers.add(RecordHeader(X_PREMISES_ID, premisesId.value.toByteArray()))
        if (actorData.isPresent) {
            headers.add(
                RecordHeader("Actor", DefaultSerializer.serialize(actorData.get()).toByteArray())
            )
        }
        if (accountData.isPresent) {
            headers.add(
                RecordHeader("Account", DefaultSerializer.serialize(accountData.get()).toByteArray())
            )
        }
        if (httpHeaders.isPresent) {
            headers.add(
                RecordHeader("headers", DefaultSerializer.serialize(httpHeaders.get()).toByteArray())
            )
        }
        return headers
    }
}
