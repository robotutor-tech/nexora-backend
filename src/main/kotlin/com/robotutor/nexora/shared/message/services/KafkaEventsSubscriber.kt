package com.robotutor.nexora.shared.message.services

import com.robotutor.nexora.shared.application.serialization.DefaultSerializer
import com.robotutor.nexora.shared.context.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.context.ReactiveContext.EVENT_MESSAGE
import com.robotutor.nexora.shared.context.ReactiveContext.MESSAGE_CONTEXT
import com.robotutor.nexora.shared.message.annotation.EventListener
import com.robotutor.nexora.shared.message.consumer.KafkaConsumer
import com.robotutor.nexora.shared.message.message.Message
import com.robotutor.nexora.shared.outbox.persistence.document.MessageContextDocument
import com.robotutor.nexora.shared.outbox.persistence.mapper.MessageContextDocumentMapper
import com.robotutor.nexora.shared.resolver.ArgumentResolver
import com.robotutor.nexora.shared.utility.createFlux
import com.robotutor.nexora.shared.utility.createMono
import jakarta.annotation.PostConstruct
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Service
import reactor.core.Disposable
import reactor.core.publisher.Mono
import reactor.util.context.Context
import java.lang.reflect.Method


@Service
class KafkaEventsSubscriber(
    private val kafkaConsumerImpl: KafkaConsumerImpl,
    private val kafkaConsumers: List<KafkaConsumer>,
    private val resolvers: List<ArgumentResolver>,
) {
    private val handlerRegistry = KafkaHandlerRegistry()

    @PostConstruct
    fun init(): Disposable {
        scanForKafkaEventHandlers()
        return kafkaConsumerImpl.consume(handlerRegistry.getKeys()) { message ->
            val handlers = handlerRegistry.getHandlers(message.topic)
            createFlux(handlers)
                .flatMap { handler ->
                    resolveKafkaHandlerArguments(handler.method)
                        .flatMap { args ->
                            val result = handler.method.invoke(handler.bean, *args)
                            result as? Mono<*> ?: createMono(result)
                        }
                        .contextWrite { writeContext(it, message) }
                }
                .collectList()
                .map {}
        }.subscribe()
    }


    private fun scanForKafkaEventHandlers() {
        kafkaConsumers.forEach { consumer ->
            consumer.javaClass.declaredMethods
                .filter {
                    it.isAnnotationPresent(EventListener::class.java)
                }
                .forEach { method ->
                    val topics = method.annotations
                        .filterIsInstance<EventListener>()
                        .flatMap { it.topics.toList() }
                    topics.forEach { topic ->
                        handlerRegistry.add(topic.topic, KafkaHandler(consumer, method))
                    }
                }
        }
    }


    private fun resolveKafkaHandlerArguments(method: Method): Mono<Array<Any>> {
        val monos = method.parameters.map { parameter ->
            resolvers.first { it.supportsParameter(parameter) }
                .resolveArgument(parameter)
        }
        return Mono.zip(monos) { it as Array<Any> }
    }

    private fun writeContext(ctx: Context, message: Message): Context {
        val messageContextHeaderValue = message.headers.find { it.key == MESSAGE_CONTEXT }!!.value
        val context = MessageContextDocumentMapper.toDomain(
            DefaultSerializer.deserialize(messageContextHeaderValue, MessageContextDocument::class.java),
        )

        context.principalData?.let {
            val authentication = UsernamePasswordAuthenticationToken(context.principalData, null, emptyList())
            val securityContext = createMono(SecurityContextImpl(authentication))
            ReactiveSecurityContextHolder.withSecurityContext(securityContext)
        }

        var newCtx = ctx
        newCtx = newCtx.put(CORRELATION_ID, context.correlationId)
        newCtx = newCtx.put(MESSAGE_CONTEXT, context)
        newCtx = newCtx.put(EVENT_MESSAGE, message.value)
        return newCtx
    }
}

private data class KafkaHandler(val bean: Any, val method: Method)

private class KafkaHandlerRegistry(private val handlers: MutableMap<String, MutableList<KafkaHandler>> = mutableMapOf()) {
    fun add(eventName: String, kafkaHandler: KafkaHandler) {
        handlers.getOrPut(eventName) { mutableListOf() }.add(kafkaHandler)
    }

    fun getHandlers(eventName: String): List<KafkaHandler> {
        return handlers.getOrDefault(eventName, listOf())
    }

    fun getKeys(): List<String> {
        return handlers.keys.toList()
    }
}
