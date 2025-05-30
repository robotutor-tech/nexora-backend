package com.robotutor.nexora.kafka.services

import com.robotutor.nexora.kafka.models.KafkaTopicName
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.logger.models.ServerWebExchangeDTO
import com.robotutor.nexora.logger.serializer.DefaultSerializer
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.models.PremisesActorData
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.util.context.ContextView

@Service
class KafkaPublisher(
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>,
) {
    val logger = Logger(this::class.java)
    fun publish(topicName: KafkaTopicName, message: Any, key: String = ""): Mono<Unit> {
        return publish(topicName, message, key) { }
    }

    fun <R : Any> publish(topicName: KafkaTopicName, message: Any, key: String = "", transformer: () -> R): Mono<R> {
        val messageAsString = DefaultSerializer.serialize(message)
        return Mono.deferContextual { ctx ->
            val headers = createHeadersRecord(ctx)
            val producerRecord = ProducerRecord(topicName, key, messageAsString)
            headers.forEach { producerRecord.headers().add(it) }
            reactiveKafkaProducerTemplate.send(producerRecord).map { transformer() }
        }
            .logOnSuccess(logger, "Successfully published kafka topic to $topicName")
            .logOnError(logger, "", "Failed to publish kafka topic to $topicName")
    }

    fun publishInBatch(topicName: KafkaTopicName, messages: List<Any>, key: String = ""): Mono<Unit> {
        return createFlux(messages)
            .flatMap { publish(topicName, it, key) }
            .collectList()
            .map { }
    }

    fun <R : Any> publishInBatch(
        topicName: KafkaTopicName,
        messages: List<Any>,
        key: String = "",
        transformer: () -> R
    ): Mono<R> {
        return createFlux(messages)
            .flatMap { publish(topicName, it, key) }
            .collectList()
            .map { transformer() }
    }

    private fun createHeadersRecord(ctx: ContextView): MutableList<RecordHeader> {
        val exchangeDTO = ctx.get(ServerWebExchangeDTO::class.java)
        val premisesData = ctx.getOrEmpty<PremisesActorData>(PremisesActorData::class.java)

        val headers = mutableListOf<RecordHeader>()
        headers.add(RecordHeader("exchange", DefaultSerializer.serialize(exchangeDTO).toByteArray()))
        if (premisesData.isPresent) {
            headers.add(
                RecordHeader(
                    "premisesActorData",
                    DefaultSerializer.serialize(premisesData.get()).toByteArray()
                )
            )
        }
        return headers
    }
}