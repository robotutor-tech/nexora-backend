package com.robotutor.nexora.shared.message.services

import com.robotutor.nexora.shared.application.logger.LogDetails
import com.robotutor.nexora.shared.application.logger.LogLevel
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.message.message.Message
import com.robotutor.nexora.shared.message.persistence.document.DLDocument
import com.robotutor.nexora.shared.message.persistence.document.KafkaHeader
import com.robotutor.nexora.shared.message.persistence.repository.DLRepository
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.logging.Level.WARNING

@Service
class KafkaConsumerImpl(
    private val kafkaReceiverFactory: (List<String>) -> ReactiveKafkaConsumerTemplate<String, String>,
    private val dlRepository: DLRepository
) {
    val logger = Logger(this::class.java)

    fun consume(topics: List<String>, process: (it: Message) -> Mono<Any>): Flux<Any> {
        val kafkaReceiver = kafkaReceiverFactory(topics)
        return kafkaReceiver.receive()
            .flatMap { receiverRecord ->
                val message = receiverRecord.value()
                val topic = receiverRecord.topic()
                val headers = receiverRecord.headers().toArray()
                    .map { KafkaHeader(it.key(), it.value().toString(StandardCharsets.UTF_8)) }
                createMono(message)
                    .flatMap { process(Message(topic, it, headers)) }
                    .logOnError(logger, "Failed to process Kafka message", level = LogLevel.WARN)
                    .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(1))
                            .doBeforeRetry {
                                logger.warn(LogDetails("Failed to consume message (will retry) Kafka topic to $topic"))
                            })
                    .logOnSuccess(logger, "Successfully consumed Kafka topic to $topic")
                    .logOnError(logger, "Failed to consume Kafka topic to $topic")
                    .onErrorResume {
                        val document = DLDocument(topic = topic, message = message, headers = headers)
                        dlRepository.save(document)
                            .logOnSuccess(logger, "Successfully added message in DLQ for $topic")
                            .logOnError(logger, "Failed to add message in DLQ for $topic")
                    }
                    .doFinally { receiverRecord.receiverOffset().acknowledge() }
            }
    }
}

