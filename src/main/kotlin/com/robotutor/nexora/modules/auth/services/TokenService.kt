package com.robotutor.nexora.modules.auth.services

import com.robotutor.nexora.common.security.service.IdGeneratorService
import com.robotutor.nexora.modules.auth.repositories.TokenRepository
import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service

@Service("TempTokenService")
class TokenService(
    private val tokenRepository: TokenRepository,
    private val idGeneratorService: IdGeneratorService,
) {
    val logger = Logger(this::class.java)

//    fun generateAuthUserToken(authUser: AuthUser): Mono<Token> {
//        return idGeneratorService.generateId(IdType.TOKEN_ID)
//            .map { tokenId -> Token.generateAuthUser(tokenId, authUser.userId) }
//            .flatMap { token -> tokenRepository.save(token) }
//            .logOnSuccess(logger, "Successfully generated auth user token")
//            .logOnError(logger, "", "Failed to generate auth user token")
//            .auditOnSuccess("GENERATE_USER_TOKEN", identifier = Identifier(authUser.userId, ActorIdentifier.USER))
//            .auditOnError("GENERATE_USER_TOKEN", identifier = Identifier(authUser.userId, ActorIdentifier.USER))
//    }
//
//    fun validate(tokenValue: String): Mono<Token> {
//        return tokenRepository.findByValueAndExpiresOnGreaterThan(tokenValue, Instant.now())
//            .switchIfEmpty { createMonoError(UnAuthorizedException(NexoraError.NEXORA0203)) }
//            .logOnSuccess(logger, "Successfully validated token")
//            .logOnError(logger, "", "Failed to validate token")
//    }
//
//    fun generatePremisesActorToken(tokenValue: String, request: PremisesActorRequest): Mono<Token> {
//        return validate(tokenValue)
//            .flatMap { token ->
//                iamGateway.getActor(request.actorId, request.roleId)
//                    .flatMap { actor ->
//                        idGeneratorService.generateId(IdType.TOKEN_ID)
//                            .map { tokenId -> token.generatePremisesActorToken(tokenId, actor) }
//                            .flatMap {
//                                tokenRepository.save(it)
//                                    .auditOnSuccess(
//                                        "GENERATE_ACTOR_TOKEN",
//                                        identifier = actor.identifier,
//                                        premisesId = actor.premisesId
//                                    )
//                            }
//                    }
//            }
//            .logOnSuccess(logger, "Successfully generated premises actor token")
//            .logOnError(logger, "", "Failed to generate premises actor token")
//    }
//
//    fun generateInvitationToken(invitation: Invitation): Mono<Token> {
//        return idGeneratorService.generateId(IdType.TOKEN_ID)
//            .map { tokenId -> Token.generateInvitationToken(tokenId, invitation) }
//            .flatMap { token ->
//                tokenRepository.save(token)
//                    .auditOnSuccess("GENERATE_INVITATION_TOKEN", mapOf("tokenId" to token.tokenId))
//            }
//            .logOnSuccess(logger, "Successfully generated invitation token")
//            .logOnError(logger, "", "Failed to generate invitation token")
//    }
//
//    fun getInvitationToken(invitation: Invitation): Mono<Token> {
//        return tokenRepository.findByIdentifier_IdAndIdentifier_TypeAndExpiresOnGreaterThan(
//            invitation.invitationId,
//            TokenIdentifier.INVITATION
//        )
//            .switchIfEmpty { createMonoError(DataNotFoundException(NexoraError.NEXORA0204)) }
//    }
//
//    fun generateDevicePremisesActorToken(
//        premisesActorRequest: PremisesActorRequest,
//        invitationData: InvitationData
//    ): Mono<Token> {
//        return idGeneratorService.generateId(IdType.TOKEN_ID)
//            .map { Token.generateDeviceActorToken(it, premisesActorRequest) }
//            .flatMap { token ->
//                tokenRepository.save(token)
//                    .auditOnSuccess(
//                        "GENERATE_DEVICE_ACTOR_TOKEN",
//                        mapOf("tokenId" to token.tokenId, "actorId" to premisesActorRequest.actorId),
//                        Identifier(invitationData.invitedBy, ActorIdentifier.USER)
//                    )
//            }
//            .logOnSuccess(logger, "Successfully generated device actor token")
//            .logOnError(logger, "", "Failed to generate device actor token")
//    }
}