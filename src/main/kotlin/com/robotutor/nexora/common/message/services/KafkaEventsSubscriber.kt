package com.robotutor.nexora.common.message.services

import com.robotutor.nexora.shared.utility.createFlux
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.common.message.annotation.EventController
import com.robotutor.nexora.common.message.annotation.EventListener
import com.robotutor.nexora.common.message.message.Message
import com.robotutor.nexora.common.message.resolver.ArgumentResolverConfigurer
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import reactor.core.Disposable
import reactor.core.publisher.Mono
import java.lang.reflect.Method


@Service
class KafkaEventsSubscriber(
    private val kafkaConsumer: KafkaConsumer,
    private val applicationContext: ApplicationContext,
    private val configurer: ArgumentResolverConfigurer,
) {
    private val handlerRegistry = KafkaHandlerRegistry()

    @PostConstruct
    fun init(): Disposable {
        scanForKafkaEventHandlers()
        return kafkaConsumer.consume(handlerRegistry.getKeys()) { message ->
            val handlers = handlerRegistry.getHandlers(message.topic)
            createFlux(handlers)
                .flatMap { handler ->
                    resolveKafkaHandlerArguments(handler.method, message)
                        .flatMap { args ->
                            val result = handler.method.invoke(handler.bean, *args)
                            result as? Mono<*> ?: createMono(result)
                        }
                }
                .collectList()
                .map {}
        }.subscribe()
    }

    private fun scanForKafkaEventHandlers() {
        applicationContext.getBeansWithAnnotation<EventController>()
            .values.forEach { annotatedBean ->
                annotatedBean.javaClass.declaredMethods
                    .filter {
                        it.isAnnotationPresent(EventListener::class.java)
                    }
                    .forEach { method ->
                        val topics = method.annotations.filterIsInstance<EventListener>()
                            .flatMap { it.topics.toList() }
                        topics.forEach { topic ->
                            handlerRegistry.add(topic, KafkaHandler(annotatedBean, method))
                        }
                    }
            }
    }


    private fun resolveKafkaHandlerArguments(method: Method, message: Message): Mono<Array<Any>> {
        val monos = method.parameters.map { parameter ->
            configurer.getResolvers()
                .first { it.supportsParameter(parameter) }
                .resolveArgument(parameter)
                .contextWrite { it.put("EventMessage", message.value) }
        }
        return Mono.zip(monos) { it as Array<Any> }
    }
}


private data class KafkaHandler(
    val bean: Any,
    val method: Method,
)

private class KafkaHandlerRegistry(
    private val handlers: MutableMap<String, MutableList<KafkaHandler>> = mutableMapOf()
) {
    fun add(topic: String, kafkaHandler: KafkaHandler) {
        handlers.getOrPut(topic) { mutableListOf() }.add(kafkaHandler)
    }

    fun getHandlers(topic: String): List<KafkaHandler> {
        return handlers.getOrDefault(topic, listOf())
    }

    fun getKeys(): List<String> {
        return handlers.keys.toList()
    }
}
