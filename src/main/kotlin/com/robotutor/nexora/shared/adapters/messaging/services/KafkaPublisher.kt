//package com.robotutor.nexora.shared.adapters.messaging.services
//
//import com.robotutor.nexora.shared.adapters.messaging.models.KafkaTopicName
//import com.robotutor.nexora.shared.logger.Logger
//import com.robotutor.nexora.shared.logger.ReactiveContext.getTraceId
//import com.robotutor.nexora.shared.logger.logOnError
//import com.robotutor.nexora.shared.logger.logOnSuccess
//import com.robotutor.nexora.shared.logger.models.ServerWebExchangeDTO
//import com.robotutor.nexora.shared.logger.serializer.DefaultSerializer
//import com.robotutor.nexora.shared.domain.event.DomainEvent
//import com.robotutor.nexora.shared.domain.event.EventPublisher
//import com.robotutor.nexora.shared.domain.model.ActorData
//import org.apache.kafka.clients.producer.ProducerRecord
//import org.apache.kafka.common.header.internals.RecordHeader
//import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//import reactor.kafka.sender.SenderResult
//import reactor.util.context.ContextView
//
//@Service
//class KafkaPublisher(
//    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>,
//)  {
//    val logger = Logger(this::class.java)
//
//    override fun publish(event: DomainEvent): Mono<Unit> {
//        return publish(event).map { }
//    }
//
//    override fun <R : Any> publish(event: DomainEvent, transformer: () -> R): Mono<R> {
//        return publishEvent(event.name, event).map { transformer() }
//    }
//
//    private fun publishEvent(topicName: KafkaTopicName, message: Any, key: String = ""): Mono<SenderResult<Void>> {
//        val messageAsString = DefaultSerializer.serialize(message)
//        return Mono.deferContextual { ctx ->
//            val headers = createHeadersRecord(ctx)
//            val producerRecord = ProducerRecord(topicName, key, messageAsString)
//            headers.forEach { producerRecord.headers().add(it) }
//            reactiveKafkaProducerTemplate.send(producerRecord)
//        }
//            .logOnSuccess(logger, "Successfully published kafka topic to $topicName")
//            .logOnError(logger, "", "Failed to publish kafka topic to $topicName")
//    }
//
//    private fun createHeadersRecord(ctx: ContextView): MutableList<RecordHeader> {
//        val traceId = getTraceId(ctx)
//        val exchangeDTO = ctx.get(ServerWebExchangeDTO::class.java)
//        val actorData = ctx.getOrEmpty<ActorData>(ActorData::class.java)
//
//        val headers = mutableListOf<RecordHeader>()
//        headers.add(RecordHeader("exchange", DefaultSerializer.serialize(exchangeDTO).toByteArray()))
//        headers.add(RecordHeader("x-trace-id", traceId.toByteArray()))
//        if (actorData.isPresent) {
//            headers.add(
//                RecordHeader("actorData", DefaultSerializer.serialize(actorData.get()).toByteArray())
//            )
//        }
//        return headers
//    }
//}
