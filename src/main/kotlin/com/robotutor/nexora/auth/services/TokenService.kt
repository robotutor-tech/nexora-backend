package com.robotutor.nexora.auth.services

import com.robotutor.iot.exceptions.DataNotFoundException
import com.robotutor.loggingstarter.Logger
import com.robotutor.loggingstarter.logOnError
import com.robotutor.loggingstarter.logOnSuccess
import com.robotutor.nexora.auth.exceptions.NexoraError
import com.robotutor.nexora.auth.models.AuthUser
import com.robotutor.nexora.auth.models.IdType
import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.auth.repositories.TokenRepository
import com.robotutor.nexora.utils.createMono
import com.robotutor.nexora.utils.createMonoError
import com.robotutor.nexora.utils.services.IdGeneratorService
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
}