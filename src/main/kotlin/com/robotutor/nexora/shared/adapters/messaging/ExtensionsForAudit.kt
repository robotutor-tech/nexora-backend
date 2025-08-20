package com.robotutor.nexora.shared.adapters.messaging

import com.robotutor.nexora.modules.audit.domain.model.AuditStatus
import com.robotutor.nexora.modules.iam.models.Role
import com.robotutor.nexora.modules.iam.models.RoleType
import com.robotutor.nexora.shared.adapters.messaging.models.AuditMessage
import com.robotutor.nexora.shared.adapters.messaging.services.KafkaPublisher
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.shared.domain.model.ActorIdentifier
import com.robotutor.nexora.shared.domain.model.Identifier
import com.robotutor.nexora.common.security.models.PremisesActorData
import reactor.core.publisher.Mono
import reactor.util.context.ContextView
import java.time.Instant

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
        role = Role(
            roleId = "missing-role-id",
            premisesId = "missing-premises-id",
            name = "missing-role-name",
            role = RoleType.CUSTOM,
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
        timestamp = Instant.now()
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
        timestamp = Instant.now()
    )
    return kafkaPublisher.publish("AUDIT", auditMessage, "audit") { result }
}