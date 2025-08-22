package com.robotutor.nexora.modules.auth.application

import com.robotutor.nexora.modules.auth.application.command.ActorLoginCommand
import com.robotutor.nexora.modules.auth.application.dto.TokenResponses
import com.robotutor.nexora.modules.auth.domain.repository.TokenRepository
import com.robotutor.nexora.shared.domain.model.ActorContext
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActorLoginUseCase(private val tokenUseCase: TokenUseCase, private val tokenRepository: TokenRepository) {
    private val logger = Logger(this::class.java)

    fun actorLogin(actorLoginCommand: ActorLoginCommand): Mono<TokenResponses> {
        return tokenUseCase.generateTokenWithRefreshToken(
            TokenPrincipalType.ACTOR,
            ActorContext(actorLoginCommand.actorId, actorLoginCommand.roleId)
        )
            .flatMap { tokens ->
                tokenUseCase.findTokenByValue(actorLoginCommand.token)
                    .flatMap { token -> tokenRepository.invalidateToken(token) }
                    .map { TokenResponses.from(tokens) }
            }
            .logOnSuccess(logger, "Successfully logged in actor")
            .logOnError(logger, "", "Failed to log in actor")
    }
}