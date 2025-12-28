package com.robotutor.nexora.common.messaging.infrastructure.services.impl

import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.common.messaging.infrastructure.services.KafkaEventPublisher
import com.robotutor.nexora.common.serialization.infrastructure.DefaultSerializer
import com.robotutor.nexora.common.observability.infrastructure.logger.Logger
import com.robotutor.nexora.common.observability.infrastructure.logger.logOnError
import com.robotutor.nexora.common.observability.infrastructure.logger.logOnSuccess
import com.robotutor.nexora.common.observability.infrastructure.models.ServerWebExchangeDTO
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
                    .logOnError(logger, "Failed to consume kafka topic to $topic")
                    .onErrorResume { Mono.empty() }
            }, 8)
    }

    private fun writeContext(receiverHeaders: Headers, ctx: Context): Context {
        val headers = receiverHeaders.toArray()
            .map { KafkaHeader(it.key(), it.value().toString(StandardCharsets.UTF_8)) }
        val Actor = headers.find { it.key == "Actor" }?.value
        val Account = headers.find { it.key == "Account" }?.value
        val exchangeDTO = headers.find { it.key == "exchange" }?.value
        val traceId = headers.find { it.key == "x-trace-id" }!!.value
        var newCtx = ctx
        newCtx = newCtx.put("x-trace-id", traceId)
        Actor?.let {
            newCtx = newCtx.put(
                Actor::class.java,
                DefaultSerializer.deserialize(Actor, Actor::class.java)
            )
        }
        Account?.let {
            newCtx = newCtx.put(
                Account::class.java,
                DefaultSerializer.deserialize(Account, Account::class.java)
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