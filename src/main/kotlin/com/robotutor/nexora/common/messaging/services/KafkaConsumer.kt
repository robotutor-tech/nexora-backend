package com.robotutor.nexora.common.messaging.services

import com.robotutor.nexora.shared.application.serialization.DefaultSerializer
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.utility.createMono
import org.apache.kafka.common.header.Headers
import org.springframework.http.HttpHeaders
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.nio.charset.StandardCharsets

@Service
class KafkaConsumer(
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
        val actor = headers.find { it.key == "Actor" }?.value
        val account = headers.find { it.key == "Account" }?.value
        val httpHeaders = headers.find { it.key == "headers" }?.value
        val correlationId = headers.find { it.key == CORRELATION_ID }!!.value
        var newCtx = ctx
        newCtx = newCtx.put(CORRELATION_ID, correlationId)
        actor?.let {
            newCtx = newCtx.put(
                ActorData::class.java,
                DefaultSerializer.deserialize(actor, ActorData::class.java)
            )
        }
        account?.let {
            newCtx = newCtx.put(
                AccountData::class.java,
                DefaultSerializer.deserialize(account, AccountData::class.java)
            )
        }
        httpHeaders?.let {
            newCtx = newCtx.put(
                HttpHeaders::class.java,
                DefaultSerializer.deserialize(httpHeaders, HttpHeaders::class.java)
            )
        }
        newCtx = newCtx.put(KafkaEventPublisher::class.java, kafkaEventPublisher)
        return newCtx
    }
}

data class KafkaHeader(val key: String, val value: String)
data class KafkaMessage(val topic: String, val message: String)