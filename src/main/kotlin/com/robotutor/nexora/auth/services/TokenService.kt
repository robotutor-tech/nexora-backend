package com.robotutor.nexora.auth.services

import com.robotutor.nexora.auth.controllers.views.PremisesActorRequest
import com.robotutor.nexora.auth.exceptions.NexoraError
import com.robotutor.nexora.auth.gateways.IAMGateway
import com.robotutor.nexora.auth.models.AuthUser
import com.robotutor.nexora.auth.models.IdType
import com.robotutor.nexora.auth.models.Invitation
import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.auth.repositories.TokenRepository
import com.robotutor.nexora.kafka.auditOnError
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.Identifier
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.security.models.TokenIdentifier
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.webClient.exceptions.DataNotFoundException
import com.robotutor.nexora.webClient.exceptions.UnAuthorizedException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

@Service
class TokenService(
    private val tokenRepository: TokenRepository,
    private val idGeneratorService: IdGeneratorService,
    private val iamGateway: IAMGateway
) {
    val logger = Logger(this::class.java)

    fun generateAuthUserToken(authUser: AuthUser): Mono<Token> {
        return idGeneratorService.generateId(IdType.TOKEN_ID)
            .map { tokenId -> Token.generateAuthUser(tokenId, authUser.userId) }
            .flatMap { token -> tokenRepository.save(token) }
            .logOnSuccess(logger, "Successfully generated auth user token")
            .logOnError(logger, "", "Failed to generate auth user token")
            .auditOnSuccess("GENERATE_USER_TOKEN", identifier = Identifier(authUser.userId, ActorIdentifier.USER))
            .auditOnError("GENERATE_USER_TOKEN", identifier = Identifier(authUser.userId, ActorIdentifier.USER))
    }

    fun validate(tokenValue: String): Mono<Token> {
        return tokenRepository.findByValueAndExpiresOnGreaterThan(tokenValue, LocalDateTime.now())
            .switchIfEmpty { createMonoError(UnAuthorizedException(NexoraError.NEXORA0203)) }
    }

    fun generatePremisesActorToken(tokenValue: String, request: PremisesActorRequest): Mono<Token> {
        return validate(tokenValue)
            .flatMap { token ->
                iamGateway.getActor(request.actorId, request.roleId)
                    .flatMap { actor ->
                        idGeneratorService.generateId(IdType.TOKEN_ID)
                            .map { tokenId -> token.generatePremisesActorToken(tokenId, actor) }
                            .flatMap {
                                tokenRepository.save(it)
                                    .auditOnSuccess(
                                        "GENERATE_ACTOR_TOKEN",
                                        identifier = actor.identifier,
                                        premisesId = actor.premisesId
                                    )
                            }
                    }
            }
            .logOnSuccess(logger, "Successfully generated premises actor token")
            .logOnError(logger, "", "Failed to generate premises actor token")
    }

    fun generateInvitationToken(invitation: Invitation): Mono<Token> {
        return idGeneratorService.generateId(IdType.TOKEN_ID)
            .map { tokenId -> Token.generateInvitationToken(tokenId, invitation) }
            .flatMap { token ->
                tokenRepository.save(token)
                    .auditOnSuccess("GENERATE_INVITATION_TOKEN", mapOf("tokenId" to token.tokenId))
            }
            .logOnSuccess(logger, "Successfully generated invitation token")
            .logOnError(logger, "", "Failed to generate invitation token")
    }

    fun getInvitationToken(invitation: Invitation): Mono<Token> {
        return tokenRepository.findByIdentifier_IdAndIdentifier_TypeAndExpiresOnGreaterThan(
            invitation.invitationId,
            TokenIdentifier.INVITATION
        )
            .switchIfEmpty { createMonoError(DataNotFoundException(NexoraError.NEXORA0204)) }
    }

    fun generateDevicePremisesActorToken(
        premisesActorRequest: PremisesActorRequest,
        invitationData: InvitationData
    ): Mono<Token> {
        return idGeneratorService.generateId(IdType.TOKEN_ID)
            .map { Token.generateDeviceActorToken(it, premisesActorRequest) }
            .flatMap { token ->
                tokenRepository.save(token)
                    .auditOnSuccess(
                        "GENERATE_DEVICE_ACTOR_TOKEN",
                        mapOf("tokenId" to token.tokenId, "actorId" to premisesActorRequest.actorId),
                        Identifier(invitationData.invitedBy, ActorIdentifier.USER)
                    )
            }
            .logOnSuccess(logger, "Successfully generated device actor token")
            .logOnError(logger, "", "Failed to generate device actor token")
    }
}