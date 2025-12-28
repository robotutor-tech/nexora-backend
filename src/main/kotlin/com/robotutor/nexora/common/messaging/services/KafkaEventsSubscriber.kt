package com.robotutor.nexora.common.messaging.services

import com.robotutor.nexora.shared.utility.createFlux
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.common.messaging.annotation.KafkaController
import com.robotutor.nexora.common.messaging.annotation.KafkaEvent
import com.robotutor.nexora.common.messaging.annotation.KafkaEventListener
import com.robotutor.nexora.common.messaging.services.impl.KafkaArgumentResolverConfigurer
import com.robotutor.nexora.common.messaging.services.impl.KafkaConfigurer
import com.robotutor.nexora.common.messaging.services.impl.KafkaConsumerImpl
import com.robotutor.nexora.shared.application.serialization.DefaultSerializer
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import reactor.core.Disposable
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.lang.reflect.Method


@Service
class KafkaEventsSubscriber(
    private val kafkaConsumerImpl: KafkaConsumerImpl,
    private val applicationContext: ApplicationContext,
    private val configurer: KafkaArgumentResolverConfigurer,
    private val kafkaConfigurer: KafkaConfigurer
) {
    private val handlerRegistry =
        KafkaHandlerRegistry()

    private fun resolveKafkaHandlerArguments(method: Method, event: Any): Mono<Array<Any>> {
        val monos = method.parameters.map { param ->
            configurer.resolvers
                .firstOrNull { it.supportsParameter(param) }
                ?.resolveArgument(param, event)
                ?: IllegalArgumentException("No resolver found for parameter $param").toMono()
        }
        return Mono.zip(monos) { it as Array<Any> }
    }

    @PostConstruct
    fun init(): Disposable {
        scanForKafkaEventHandlers()
        return kafkaConsumerImpl.consume(handlerRegistry.handlers.keys.toList()) { kafkaMessage ->
            val handlers = handlerRegistry.handlers[kafkaMessage.topic]
            createFlux(handlers.orEmpty())
                .flatMap { handler ->
                    val event = DefaultSerializer.deserialize(kafkaMessage.message, handler.type)
                    resolveKafkaHandlerArguments(handler.method, event)
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
        kafkaConfigurer.configureArgumentResolvers(configurer)
        applicationContext.getBeansWithAnnotation<KafkaController>()
            .values.forEach { annotatedBean ->
                annotatedBean.javaClass.declaredMethods.filter {
                    it.isAnnotationPresent(KafkaEventListener::class.java)
                }
                    .forEach { method ->
                        val topics = method.annotations.filterIsInstance<KafkaEventListener>()
                            .flatMap { it.topics.toList() }
                        val kafkaEventParam = method.parameters.find { param ->
                            param.isAnnotationPresent(KafkaEvent::class.java)
                        } ?: method.parameters.first()
                        topics.forEach { topic ->
                            val handlers = handlerRegistry.handlers.getOrPut(topic) { mutableListOf() }
                            handlers.add(KafkaHandler(annotatedBean, method, kafkaEventParam.type))
                        }
                    }
            }
    }

}


private data class KafkaHandler(
    val bean: Any,
    val method: Method,
    val type: Class<*>
)

private data class KafkaHandlerRegistry(
    val handlers: MutableMap<String, MutableList<KafkaHandler>> = mutableMapOf()
)