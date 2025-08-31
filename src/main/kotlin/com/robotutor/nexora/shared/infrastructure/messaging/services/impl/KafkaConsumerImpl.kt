package com.robotutor.nexora.shared.infrastructure.messaging.services.impl

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.shared.infrastructure.jackson.DefaultSerializer
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.shared.logger.models.ServerWebExchangeDTO
import org.apache.kafka.common.header.Headers
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.nio.charset.StandardCharsets

@Service
class KafkaConsumerImpl(
    private val kafkaReceiverFactory: (List<String>) -> ReactiveKafkaConsumerTemplate<String, String>,
    private val kafkaEventPublisher: KafkaEventPublisher
) {
    val logger = Logger(this::class.java)

    fun consume(topics: List<String>, process: (it: KafkaMessage) -> Mono<Any>): Flux<Any> {
        val kafkaReceiver = kafkaReceiverFactory(topics)
        return kafkaReceiver.receive()
            .flatMap({ receiverRecord ->
                val message = receiverRecord.value()
                val topic = receiverRecord.topic()
                createMono(message)
                    .flatMap { process(KafkaMessage(topic, it)) }
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
        val actorData = headers.find { it.key == "actorData" }?.value
        val userData = headers.find { it.key == "userData" }?.value
        val exchangeDTO = headers.find { it.key == "exchange" }?.value
        val traceId = headers.find { it.key == "x-trace-id" }!!.value
        var newCtx = ctx
        newCtx = newCtx.put("x-trace-id", traceId)
        actorData?.let {
            newCtx = newCtx.put(
                ActorData::class.java,
                DefaultSerializer.deserialize(actorData, ActorData::class.java)
            )
        }
        userData?.let {
            newCtx = newCtx.put(
                UserData::class.java,
                DefaultSerializer.deserialize(userData, UserData::class.java)
            )
        }
        exchangeDTO?.let {
            newCtx = newCtx.put(
                ServerWebExchangeDTO::class.java,
                DefaultSerializer.deserialize(exchangeDTO, ServerWebExchangeDTO::class.java)
            )
        }
        newCtx = newCtx.put(KafkaEventPublisher::class.java, kafkaEventPublisher)
        return newCtx
    }
}

data class KafkaHeader(val key: String, val value: String)
data class KafkaMessage(val topic: String, val message: String)