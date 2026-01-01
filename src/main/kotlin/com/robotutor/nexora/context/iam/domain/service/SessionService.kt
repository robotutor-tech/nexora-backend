package com.robotutor.nexora.context.iam.domain.service

import com.robotutor.nexora.context.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.SessionAggregate
import com.robotutor.nexora.context.iam.domain.vo.AccountPrincipal
import com.robotutor.nexora.context.iam.domain.vo.ActorPrincipal
import com.robotutor.nexora.context.iam.domain.vo.HashedTokenValue
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import org.springframework.stereotype.Service

@Service
class SessionService(private val tokenGenerator: TokenGenerator) {
    fun create(account: AccountAggregate, refreshToken: TokenValue): SessionAggregate {
        account.ensureActive()
        val sessionPrincipal = AccountPrincipal.from(account)
        return SessionAggregate.create(
            sessionPrincipal = sessionPrincipal,
            accessToken = tokenGenerator.generateAccessToken(sessionPrincipal),
            refreshTokenHash = HashedTokenValue.create(refreshToken)
        )
    }

    fun create(actor: ActorAggregate, account: AccountData, refreshToken: TokenValue): SessionAggregate {
        actor.ensureActive()
        val sessionPrincipal = ActorPrincipal.from(actor, account)
        return SessionAggregate.create(
            sessionPrincipal = sessionPrincipal,
            accessToken = tokenGenerator.generateAccessToken(sessionPrincipal),
            refreshTokenHash = HashedTokenValue.create(refreshToken)
        )
    }

    fun refresh(session: SessionAggregate, refreshToken: TokenValue): SessionAggregate {
        val accessToken = tokenGenerator.generateAccessToken(session.sessionPrincipal)
        return session.refresh(accessToken, HashedTokenValue(refreshToken.value))
    }
}