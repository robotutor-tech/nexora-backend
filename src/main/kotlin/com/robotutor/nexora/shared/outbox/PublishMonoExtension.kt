package com.robotutor.nexora.shared.outbox

import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.context.ReactiveContext
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.DeviceData
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.vo.UserData
import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.message.message.EventMessage
import com.robotutor.nexora.shared.outbox.audit.AuditEventMessage
import com.robotutor.nexora.shared.outbox.audit.AuditState
import com.robotutor.nexora.shared.outbox.audit.ResourceMessage
import com.robotutor.nexora.shared.outbox.recorder.RecorderInfrastructure
import com.robotutor.nexora.shared.utility.createFlux
import com.robotutor.nexora.shared.utility.createMonoError
import reactor.core.publisher.Mono

fun <D : Event, ID : Identifier, T : AggregateRoot<T, ID, D>> Mono<T>.publishEvents(
    aggregate: AggregateRoot<T, ID, D>,
    mapper: EventMapper<D>,
): Mono<T> {
    return flatMap { result ->
        createFlux(aggregate.domainEvents)
            .flatMap {
                RecorderInfrastructure.recorder.record(mapper.toEventMessage(it))
            }
            .collectList()
            .map {
                aggregate.clearEvents()
                result
            }
    }
}

fun <D : Event, ID : Identifier, T : AggregateRoot<T, ID, D>> Mono<T>.publishEvent(
    event: D,
    mapper: EventMapper<D>
): Mono<T> {
    return flatMap { result ->
        RecorderInfrastructure.recorder.record(mapper.toEventMessage(event))
            .map { result }
    }
}

fun <D : Event, ID : Identifier, T : AggregateRoot<T, ID, D>> Mono<T>.publishEventOnError(
    event: D,
    mapper: EventMapper<D>
): Mono<T> {
    val logger = Logger(this.javaClass)
    return onErrorResume { throwable ->
        val eventMessage: EventMessage = mapper.toEventMessage(event)
        val additionalDetails = mapOf("event" to eventMessage.eventName)

        RecorderInfrastructure.recorder.record(eventMessage)
            .logOnSuccess(logger, "Successfully added event to outbox", additionalDetails)
            .logOnError(logger, "Failed to add event to outbox", additionalDetails)
            .then(createMonoError(throwable))

    }
}

fun <T> Mono<T>.auditOnSuccess(
    action: String,
    type: ResourceType,
    identifier: Identifier,
    metadata: Map<String, Any?> = emptyMap(),
    principal: PrincipalData? = null,
): Mono<T> {
    val logger = Logger(this.javaClass)
    return flatMap { domain ->
        ReactiveContext.getContextData()
            .flatMap {
                val principal = principal ?: it.principalData
                var actorId: String? = null
                var premisesId: String? = null
                var userId: String? = null
                var deviceId: String? = null
                if (principal is ActorData) {
                    actorId = principal.actorId.value
                    premisesId = principal.premisesId.value
                    if (principal.accountData is UserData) {
                        userId = principal.accountData.userId.value
                    }
                    if (principal.accountData is DeviceData) {
                        deviceId = principal.accountData.deviceId.value
                    }
                }
                if (principal is UserData) {
                    userId = principal.userId.value
                }
                if (principal is DeviceData) {
                    deviceId = principal.deviceId.value
                }
                RecorderInfrastructure.recorder.record(
                    AuditEventMessage(
                        action = action,
                        resource = ResourceMessage(type, identifier.value),
                        state = AuditState.SUCCESS,
                        premisesId = premisesId,
                        metadata = metadata,
                        userId = userId,
                        deviceId = deviceId,
                        actorId = actorId,
                    )
                )
            }
            .logOnSuccess(logger, "Successfully added audit event")
            .logOnError(logger, "Failed to add audit event")
            .then(Mono.fromCallable { domain })
    }
}


