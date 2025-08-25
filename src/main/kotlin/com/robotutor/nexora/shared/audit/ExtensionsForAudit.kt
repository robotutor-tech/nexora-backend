//package com.robotutor.nexora.shared.audit
//
//import com.robotutor.nexora.shared.application.service.ContextDataResolver
//import com.robotutor.nexora.shared.audit.model.AuditEvent
//import com.robotutor.nexora.shared.audit.model.AuditStatus
//import com.robotutor.nexora.shared.domain.model.ActorData
//import com.robotutor.nexora.shared.domain.model.DeviceData
//import com.robotutor.nexora.shared.domain.model.InternalData
//import com.robotutor.nexora.shared.domain.model.InvitationData
//import com.robotutor.nexora.shared.domain.model.PremisesId
//import com.robotutor.nexora.shared.domain.model.PrincipalContext
//import com.robotutor.nexora.shared.domain.model.PrincipalData
//import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
//import com.robotutor.nexora.shared.domain.model.UserData
//import com.robotutor.nexora.shared.interfaces.mapper.PrincipalContextMapper
//import reactor.core.publisher.Mono
//
//fun <T : Any> Mono<T>.audit(
//    event: String,
//    metadata: Map<String, Any?> = emptyMap(),
//    premisesId: PremisesId? = null,
//    principalType: TokenPrincipalType? = null,
//    principalContext: PrincipalContext? = null,
//): Mono<T> {
//    return auditOnSuccess(event, metadata, premisesId, principalType, principalContext)
//        .auditOnError(event, metadata, premisesId, principalType, principalContext)
//}
//
//private fun <T : Any> Mono<T>.auditOnError(
//    event: String,
//    metadata: Map<String, Any?>,
//    premisesId: PremisesId?,
//    principalType: TokenPrincipalType?,
//    principalContext: PrincipalContext?,
//): Mono<T> {
//    return onErrorResume { error ->
//        ContextDataResolver.getEventPublisher()
//            .flatMap { eventPublisher ->
//                ContextDataResolver.getPrincipalData()
//                    .map { principalData ->
//                        AuditEvent(
//                            event = event,
//                            premisesId = getPremisesId(principalData, premisesId),
//                            principalType = getPrincipalType(principalData, principalType),
//                            principal = principalContext ?: PrincipalContextMapper.toPrincipalContext(principalData),
//                            status = AuditStatus.SUCCESS,
//                            metadata = metadata
//                        )
//                    }
//                    .flatMap { auditEvent -> eventPublisher.publish(auditEvent) { throw error } }
//            }
//    }
//}
//
//private fun <T : Any> Mono<T>.auditOnSuccess(
//    event: String,
//    metadata: Map<String, Any?>,
//    premisesId: PremisesId?,
//    principalType: TokenPrincipalType?,
//    principalContext: PrincipalContext?,
//): Mono<T> {
//    return flatMap { result ->
//        ContextDataResolver.getEventPublisher()
//            .flatMap { eventPublisher ->
//                ContextDataResolver.getPrincipalData()
//                    .map { principalData ->
//                        AuditEvent(
//                            event = event,
//                            premisesId = getPremisesId(principalData, premisesId),
//                            principalType = getPrincipalType(principalData, principalType),
//                            principal = principalContext ?: PrincipalContextMapper.toPrincipalContext(principalData),
//                            status = AuditStatus.SUCCESS,
//                            metadata = metadata
//                        )
//                    }
//                    .flatMap { auditEvent -> eventPublisher.publish(auditEvent) { result } }
//            }
//    }
//}
//
//private fun getPremisesId(principalData: PrincipalData, premisesId: PremisesId?): PremisesId? {
//    if (premisesId != null) return premisesId
//    return when (principalData) {
//        is ActorData -> principalData.premisesId
//        else -> null
//    }
//}
//
//private fun getPrincipalType(principalData: PrincipalData, principalType: TokenPrincipalType?): TokenPrincipalType {
//    if (principalType != null) return principalType
//    return when (principalData) {
//        is ActorData -> TokenPrincipalType.ACTOR
//        is UserData -> TokenPrincipalType.USER
//        is InternalData -> TokenPrincipalType.INTERNAL
//        is InvitationData -> TokenPrincipalType.INVITATION
//        is DeviceData -> TokenPrincipalType.INTERNAL
//    }
//}
