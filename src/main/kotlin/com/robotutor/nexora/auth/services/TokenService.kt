package com.robotutor.nexora.auth.services

import com.robotutor.iot.exceptions.DataNotFoundException
import com.robotutor.nexora.auth.controllers.views.PremisesActorRequest
import com.robotutor.nexora.auth.exceptions.NexoraError
import com.robotutor.nexora.auth.models.*
import com.robotutor.nexora.auth.repositories.TokenRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

@Service
class TokenService(val tokenRepository: TokenRepository, val idGeneratorService: IdGeneratorService) {
    val logger = Logger(this::class.java)

    fun generateAuthUserToken(authUser: AuthUser): Mono<Token> {
        return idGeneratorService.generateId(IdType.TOKEN_ID)
            .map { tokenId -> Token.generateAuthUser(tokenId, authUser.userId) }
            .flatMap { token -> tokenRepository.save(token) }
            .logOnSuccess(logger, "Successfully generated auth user token")
            .logOnError(logger, "", "Failed to generate auth user token")
    }

    fun validate(token: String): Mono<Token> {
        return tokenRepository.findByValueAndExpiresOnGreaterThan(token, LocalDateTime.now())
            .switchIfEmpty { createMonoError(DataNotFoundException(NexoraError.NEXORA0203)) }
        // TODO: Validate other scenarios like premises id
    }

    fun generatePremisesActorToken(tokenValue: String, premisesActorRequest: PremisesActorRequest): Mono<Token> {
        return validate(tokenValue)
//            TODO: Validate premises Actor
            .flatMap { token ->
                idGeneratorService.generateId(IdType.TOKEN_ID)
                    .map { tokenId -> token.generatePremisesActorToken(tokenId, premisesActorRequest) }
            }
            .flatMap { tokenRepository.save(it) }
            .logOnSuccess(logger, "Successfully generated premises actor token")
            .logOnError(logger, "", "Failed to generate premises actor token")
    }

    fun generateInvitationToken(invitation: Invitation, userData: PremisesActorData): Mono<Token> {
        return idGeneratorService.generateId(IdType.TOKEN_ID)
            .map { tokenId -> Token.generateInvitationToken(tokenId, invitation, userData) }
            .flatMap { token -> tokenRepository.save(token) }
            .logOnSuccess(logger, "Successfully generated invitation token")
            .logOnError(logger, "", "Failed to generate invitation token")
    }

    fun getInvitationToken(invitation: Invitation): Mono<Token> {
        return tokenRepository.findByMetadata_IdentifierAndMetadata_IdentifierTypeAndExpiresOnLessThan(
            invitation.invitationId,
            TokenIdentifierType.INVITATION,
            LocalDateTime.now()
        )
    }
}