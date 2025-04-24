package com.robotutor.nexora.auth.services

import com.robotutor.iot.exceptions.DataNotFoundException
import com.robotutor.nexora.auth.controllers.views.TokenRequest
import com.robotutor.nexora.auth.exceptions.NexoraError
import com.robotutor.nexora.auth.models.AuthUser
import com.robotutor.nexora.auth.models.IdType
import com.robotutor.nexora.auth.models.Invitation
import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.auth.repositories.TokenRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.UserPremisesData
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class TokenService(val tokenRepository: TokenRepository, val idGeneratorService: IdGeneratorService) {
    val logger = Logger(this::class.java)

    fun generateToken(authUser: AuthUser): Mono<Token> {
        return idGeneratorService.generateId(IdType.TOKEN_ID)
            .map { tokenId ->
                Token.from(tokenId, authUser.userId)
            }
            .flatMap { token -> tokenRepository.save(token) }
            .logOnSuccess(logger, "Successfully generated token")
            .logOnError(logger, "", "Failed to generate token")
    }

    fun validate(token: String, partialSecured: Boolean): Mono<Token> {
        return tokenRepository.findByValue(token)
            .switchIfEmpty { createMonoError(DataNotFoundException(NexoraError.NEXORA0203)) }
            .flatMap {
                if (partialSecured) {
                    createMono(it)
                } else {
                    // TODO: validate if user have the premises access
                    createMono(it)
                }
            }
    }

    fun generateFullSecuredToken(tokenValue: String, tokenRequest: TokenRequest): Mono<Token> {
        return validate(tokenValue, true)
            .flatMap { token ->
                idGeneratorService.generateId(IdType.TOKEN_ID)
                    .map { tokenId -> token.generateToken(tokenId, tokenRequest) }
            }
            .flatMap { tokenRepository.save(it) }
            .logOnSuccess(logger, "Successfully generated fully secured token")
            .logOnError(logger, "", "Failed to generate fully secured token")
    }

    fun generateInvitationToken(userData: UserPremisesData): Mono<Token> {
        return idGeneratorService.generateId(IdType.TOKEN_ID)
            .flatMap { tokenId ->
                tokenRepository.save(Token.generateInvitationToken(tokenId, userData))
            }
            .logOnSuccess(logger, "Successfully generated invitation token")
            .logOnError(logger, "", "Failed to generate invitation token")
    }

    fun getInvitationToken(invitation: Invitation): Mono<Token> {
        return tokenRepository.findByTokenId(invitation.tokenId)
    }
}