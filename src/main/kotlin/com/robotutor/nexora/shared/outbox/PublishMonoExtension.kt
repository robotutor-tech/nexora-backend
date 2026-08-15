package com.robotutor.nexora.shared.outbox

import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.outbox.entity.OutboxEvent
import com.robotutor.nexora.shared.outbox.recorder.RecorderInfrastructure
import com.robotutor.nexora.shared.utility.createFlux
import reactor.core.publisher.Mono

fun <D : Event, ID : Identifier, T : AggregateRoot<T, ID, D>> Mono<T>.publishEvents(
    aggregate: AggregateRoot<T, ID, D>,
    mapper: EventMapper<D>,
): Mono<T> {
    val logger = Logger(this.javaClass)
    return flatMap { result ->
        createFlux(aggregate.domainEvents)
            .map { OutboxEvent(mapper.toEventMessage(it)) }
            .flatMap {
                val additionalDetails = mapOf("event" to it.message.eventName, "eventId" to it.eventId)
                RecorderInfrastructure.recorder.record(it)
                    .logOnSuccess(logger, "Successfully added event to outbox", additionalDetails)
                    .logOnError(logger, "Failed to add event to outbox", additionalDetails)
            }
            .collectList()
            .map {
                aggregate.clearEvents()
                result
            }
    }
}

//fun <T> Mono<T>.auditOnSuccess(
//    action: String,
//    type: ResourceType,
//    identifier: Identifier,
//    metadata: Map<String, Any?> = emptyMap(),
//    userId: UserId? = null,
//): Mono<T> {
//    val logger = Logger(this.javaClass)
//    return flatMap { domain ->
//        Mono.zip(ReactiveContext.getTraceData(), ReactiveContext.getPrincipalData())
//            .flatMap {
//                val event = DomainEvent()
//                val id = merchantId?.value
//                    ?: if (it.t2 is MerchantData) (it.t2 as MerchantData).merchantId.value else null
//                RecorderInfrastructure.recorder.record(
//                    AuditEventMessage(
//                        userId = (userId ?: it.t2.identifier).value,
//                        action = action,
//                        resource = ResourceMessage(type, identifier.value),
//                        merchantId = id,
//                        metadata = metadata,
//                        state = AuditState.SUCCESS,
//                        eventId = event.eventId.value,
//                        correlationId = it.t1.correlationId,
//                        occurredAt = event.occurredAt
//                    )
//                )
//                    .logOnSuccess(logger, "Successfully added audit event")
//                    .logOnError(logger, "Failed to add audit event")
//            }
//            .then(Mono.fromCallable { domain })
//    }
//}

//fun <T> Flux<T>.auditOnSuccess(
//    action: String,
//    type: ResourceType,
//    identifier: Identifier,
//    metadata: Map<String, Any?> = emptyMap(),
//    userId: UserId? = null,
//    merchantId: MerchantId? = null,
//): Mono<T> {
//    val logger = Logger(this.javaClass)
//    val audits = mutableListOf<AuditEventMessage>()
//    val hasElements = AtomicBoolean(false)
//    var traceData: TraceData? = null
//    var principalData: PrincipalData? = null
//    return flatMap { result ->
//        if (!hasElements.get()) {
//            Mono.zip(ReactiveContext.getTraceData(), ReactiveContext.getPrincipalData())
//                .map {
//                    hasElements.set(true)
//                    traceData = it.t1
//                    principalData = it.t2
//                    result
//                }
//        } else {
//            @Suppress("UNCHECKED_CAST")
//            createMono(result as Any) as Mono<T>
//        }
//    }
//        .doOnComplete {
//            RecorderInfrastructure.recorder.record(
//                AuditEventMessage(
//                    userId = (userId ?: it.t2.identifier).value,
//                    action = action,
//                    resource = ResourceMessage(type, identifier.value),
//                    merchantId = id,
//                    metadata = metadata,
//                    state = AuditState.SUCCESS,
//                    eventId = event.eventId.value,
//                    correlationId = it.t1.correlationId,
//                    occurredAt = event.occurredAt
//                )
//            )
//                .logOnSuccess(logger, "Successfully added audit event")
//                .logOnError(logger, "Failed to add audit event")
//        }
//
//
//}
