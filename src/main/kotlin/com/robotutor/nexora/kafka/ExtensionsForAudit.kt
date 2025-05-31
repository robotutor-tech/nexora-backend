package com.robotutor.nexora.kafka

import com.robotutor.nexora.iam.controllers.view.RoleView
import com.robotutor.nexora.iam.models.RoleType
import com.robotutor.nexora.kafka.models.AuditMessage
import com.robotutor.nexora.kafka.models.AuditStatus
import com.robotutor.nexora.kafka.services.KafkaPublisher
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.models.PremisesActorData
import reactor.core.publisher.Mono
import reactor.util.context.ContextView
import java.time.LocalDateTime
import java.time.ZoneId

fun <T : Any> Mono<T>.auditOnError(
    event: String,
    metadata: Map<String, Any?> = emptyMap(),
    identifier: Identifier<ActorIdentifier>? = null,
    premisesId: String? = null,
): Mono<T> {
    return onErrorResume { error ->
        Mono.deferContextual { ctx ->
            auditOnError<T>(ctx, identifier, metadata, event, premisesId, error)
        }
    }
}

fun <T : Any> Mono<T>.auditOnSuccess(
    event: String,
    metadata: Map<String, Any?> = emptyMap(),
    identifier: Identifier<ActorIdentifier>? = null,
    premisesId: PremisesId? = null,
): Mono<T> {
    return flatMap { result ->
        Mono.deferContextual { ctx ->
            auditOnSuccess(ctx, identifier, premisesId, metadata, event, result)
        }
    }
}

fun getPremisesActorData(contextView: ContextView): PremisesActorData {
    val premisesActorData = contextView.getOrEmpty<PremisesActorData>(PremisesActorData::class.java)
    if (premisesActorData.isPresent) return premisesActorData.get()
    return PremisesActorData(
        actorId = "missing-actor-id",
        role = RoleView(
            roleId = "missing-role-id",
            premisesId = "missing-premises-id",
            name = "missing-role-name",
            role = RoleType.CUSTOM,
            policies = emptySet(),
        ),
        premisesId = "missing-premises-id",
        identifier = Identifier("missing-identifier", ActorIdentifier.USER),
    )
}

fun <T : Any> auditOnError(
    ctx: ContextView,
    identifier: Identifier<ActorIdentifier>?,
    metadata: Map<String, Any?>,
    event: String,
    premisesId: String?,
    error: Throwable
): Mono<T> {
    val kafkaPublisher = ctx.get(KafkaPublisher::class.java)

    val premisesActorData = getPremisesActorData(ctx)
    val auditMessage = AuditMessage(
        status = AuditStatus.FAILURE,
        identifier = identifier ?: premisesActorData.identifier,
        actorId = premisesActorData.actorId,
        metadata = metadata,
        event = event,
        premisesId = premisesId ?: premisesActorData.premisesId,
        timestamp = LocalDateTime.now(ZoneId.of("UTC"))
    )
    return kafkaPublisher.publish("AUDIT", auditMessage, "audit")
        .flatMap { Mono.error { error } }
}

fun <T : Any> auditOnSuccess(
    ctx: ContextView,
    identifier: Identifier<ActorIdentifier>?,
    premisesId: PremisesId?,
    metadata: Map<String, Any?>,
    event: String,
    result: T
): Mono<T> {
    val kafkaPublisher = ctx.get(KafkaPublisher::class.java)

    val premisesActorData = getPremisesActorData(ctx)
    val auditMessage = AuditMessage(
        status = AuditStatus.SUCCESS,
        identifier = identifier ?: premisesActorData.identifier,
        metadata = metadata,
        event = event,
        actorId = premisesActorData.actorId,
        premisesId = premisesId ?: premisesActorData.premisesId,
        timestamp = LocalDateTime.now(ZoneId.of("UTC"))
    )
    return kafkaPublisher.publish("AUDIT", auditMessage, "audit") { result }
}