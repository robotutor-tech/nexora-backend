package com.robotutor.nexora.shared.message.services

import com.robotutor.nexora.shared.application.logger.LogDetails
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.application.serialization.DefaultSerializer
import com.robotutor.nexora.shared.context.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.context.ReactiveContext.MESSAGE_CONTEXT
import com.robotutor.nexora.shared.context.ReactiveContext.PRINCIPAL_DATA
import com.robotutor.nexora.shared.message.message.EventMessage
import com.robotutor.nexora.shared.outbox.persistence.document.MessageContextDocument
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import reactor.util.retry.Retry
import java.time.Duration

@Service
class KafkaEventPublisher(
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>,
) {
    val logger = Logger(this::class.java)

    fun publish(message: EventMessage, context: MessageContextDocument): Mono<SenderResult<Void>> {
        val messageAsString = DefaultSerializer.serialize(message)
        val topic = message.eventName.topic
        val key = context.principalData?.principalId
        val producerRecord = ProducerRecord(topic, key, messageAsString)
        val headers = producerRecord.headers()
        createKafkaHeaders(context).forEach { headers.add(it) }

        return reactiveKafkaProducerTemplate.send(producerRecord)
            .retryWhen(
                Retry.backoff(3, Duration.ofSeconds(3))
                    .doBeforeRetry {
                        logger.warn(LogDetails("Failed to publish message (will retry) to Kafka topic $topic"))
                    })
            .logOnSuccess(logger, "Successfully published Kafka topic to $topic")
            .logOnError(logger, "Failed to publish Kafka topic to $topic")
    }

    private fun createKafkaHeaders(context: MessageContextDocument): List<RecordHeader> {
        val headers = mutableListOf<RecordHeader>()
        headers.add(RecordHeader(CORRELATION_ID, context.correlationId.toByteArray()))
        if (context.principalData != null) {
            headers.add(
                RecordHeader(PRINCIPAL_DATA, DefaultSerializer.serialize(context.principalData).toByteArray())
            )
        }
        headers.add(RecordHeader(MESSAGE_CONTEXT, DefaultSerializer.serialize(context).toByteArray()))
        return headers
    }
}
