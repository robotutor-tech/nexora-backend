package com.robotutor.nexora.shared.adapters.messaging.services

import com.robotutor.nexora.shared.adapters.messaging.models.KafkaTopicName
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.shared.logger.models.ServerWebExchangeDTO
import com.robotutor.nexora.shared.logger.serializer.DefaultSerializer
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.models.PremisesActorData
import org.apache.kafka.common.header.Headers
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.nio.charset.StandardCharsets

@Service
class KafkaConsumer(
    private val kafkaReceiverFactory: (List<String>) -> ReactiveKafkaConsumerTemplate<String, String>,
    private val kafkaPublisher: KafkaPublisher
) {
    val logger = Logger(this::class.java)

    fun <T : Any, R : Any> consume(
        topics: List<KafkaTopicName>,
        messageType: Class<T>,
        process: (it: KafkaTopicMessage<T>) -> Mono<R>
    ): Flux<R> {
        val kafkaReceiver = kafkaReceiverFactory(topics.map { it })
        return kafkaReceiver.receive()
            .flatMap({ receiverRecord ->
                val message = DefaultSerializer.deserialize(receiverRecord.value(), messageType)
                val topic = receiverRecord.topic()
                createMono(KafkaTopicMessage(topic, message))
                    .flatMap { process(it) }
                    .contextWrite { ctx -> writeContext(receiverRecord.headers(), ctx) }
                    .doFinally { receiverRecord.receiverOffset().acknowledge() }
                    .logOnSuccess(logger, "Successfully consumed kafka topic to $topic")
                    .logOnError(logger, "", "Failed to consume kafka topic to $topic")
                    .onErrorResume { Mono.empty() }
            }, 8)
    }

    private fun writeContext(receiverHeaders: Headers, ctx: Context): Context {
        val headers = receiverHeaders.toArray()
            .map { KafkaHeader(it.key(), it.value().toString(StandardCharsets.UTF_8)) }
        val premisesData = headers.find { it.key == "premisesActorData" }?.value
        val exchangeDTO = headers.find { it.key == "exchange" }?.value
        val traceId = headers.find { it.key == "x-trace-id" }!!.value
        var newCtx = ctx
        newCtx = newCtx.put("x-trace-id", traceId)
        premisesData?.let {
            newCtx = newCtx.put(
                PremisesActorData::class.java,
                DefaultSerializer.deserialize(premisesData, PremisesActorData::class.java)
            )
        }
        exchangeDTO?.let {
            newCtx = newCtx.put(
                ServerWebExchangeDTO::class.java,
                DefaultSerializer.deserialize(exchangeDTO, ServerWebExchangeDTO::class.java)
            )
        }
        newCtx = newCtx.put(KafkaPublisher::class.java, kafkaPublisher)
        return newCtx
    }
}

data class KafkaTopicMessage<T : Any>(val topic: KafkaTopicName, val message: T)
data class KafkaHeader(val key: String, val value: String)