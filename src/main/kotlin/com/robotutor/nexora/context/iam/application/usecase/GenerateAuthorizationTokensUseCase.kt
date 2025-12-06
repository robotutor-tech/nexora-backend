package com.robotutor.nexora.context.iam.application.usecase

import com.robotutor.nexora.context.iam.application.view.Tokens
import com.robotutor.nexora.context.iam.domain.factory.TokenFactory
import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.TokenPrincipalType
import com.robotutor.nexora.context.iam.domain.aggregate.TokenType
import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.repository.TokenRepository
import com.robotutor.nexora.context.iam.domain.vo.AccountTokenPrincipalContext
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class GenerateAuthorizationTokensUseCase(
    private val tokenFactory: TokenFactory,
    private val tokenRepository: TokenRepository,
    private val eventPublisher: EventPublisher<IAMEvent>
) {
    private val logger = Logger(this::class.java)

    fun generateTokensForAccount(account: AccountAggregate): Mono<Tokens> {
        val principalType = TokenPrincipalType.ACCOUNT
        val principalContext = AccountTokenPrincipalContext(accountId = account.accountId, type = account.type)
        val authToken = tokenFactory.getStrategy(TokenType.AUTHORIZATION).generate(principalType, principalContext)
        val refreshToken = tokenFactory.getStrategy(TokenType.REFRESH).generate(principalType, principalContext)
        val updatedAuthToken = authToken.updateOtherTokenId(refreshToken.tokenId)
        val updatedRefreshToken = refreshToken.updateOtherTokenId(authToken.tokenId)

        return tokenRepository.save(updatedAuthToken)
            .map { updatedAuthToken }
            .publishEvents(eventPublisher)
            .flatMap { tokenRepository.save(updatedRefreshToken).map { updatedRefreshToken } }
            .publishEvents(eventPublisher)
            .logOnSuccess(logger, "Successfully generated tokens for ${account.accountId.value}")
            .logOnError(logger, "", "Failed to generate tokens for ${account.accountId.value}")
            .map { Tokens(updatedAuthToken, updatedRefreshToken) }
    }

    fun generateTokensForActor(account: AccountAggregate, actorId: ActorId, roleId: RoleId) {
//        val authToken = tokenFactory.getStrategy(TokenType.AUTHORIZATION).generate(principalType, principalContext)
//        val refreshToken = tokenFactory.getStrategy(TokenType.REFRESH).generate(principalType, principalContext)
//        authToken.updateOtherTokenId(refreshToken.tokenId)
//        refreshToken.updateOtherTokenId(authToken.tokenId)
    }

}