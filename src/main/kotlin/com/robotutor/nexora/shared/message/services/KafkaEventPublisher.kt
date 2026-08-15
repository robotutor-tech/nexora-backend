package com.robotutor.nexora.shared.message.services

import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.application.serialization.DefaultSerializer
import com.robotutor.nexora.shared.context.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.message.message.KafkaMessage
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult

@Service
class KafkaEventPublisher(
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>,
) {
    val logger = Logger(this::class.java)

    fun publish(message: KafkaMessage): Mono<SenderResult<Void>> {
        val messageAsString = DefaultSerializer.serialize(message.eventMessage)
        val topic = message.eventMessage.eventName.topic
        val key = message.principalData?.principalId
        val producerRecord = ProducerRecord(topic, key, messageAsString)
        val headers = producerRecord.headers()
        createKafkaHeaders(message).forEach { headers.add(it) }

        return reactiveKafkaProducerTemplate.send(producerRecord)
            .logOnSuccess(logger, "Successfully published Kafka topic to $topic")
            .logOnError(logger, "Failed to publish Kafka topic to $topic")
    }

    private fun createKafkaHeaders(message: KafkaMessage): List<RecordHeader> {
        val headers = mutableListOf<RecordHeader>()
        headers.add(RecordHeader(CORRELATION_ID, message.correlationId.toByteArray()))
        if (message.principalData != null) {
            headers.add(
                RecordHeader("PrincipalData", DefaultSerializer.serialize(message.principalData).toByteArray())
            )
        }
        return headers
    }
}
