package com.robotutor.nexora.module.identity.infrastructure.secret

import com.robotutor.nexora.module.identity.domain.service.SessionExpiryService
import com.robotutor.nexora.module.identity.domain.service.TokenGenerator
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.domain.vo.AccessToken
import com.robotutor.nexora.shared.domain.vo.Tokens
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.DeviceData
import com.robotutor.nexora.shared.domain.vo.UserData
import com.robotutor.nexora.shared.security.service.JwtValidationService
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class JwtService(
    private val sessionExpiryService: SessionExpiryService
) : TokenGenerator, JwtValidationService() {
    override fun generateTokens(accountData: AccountData, sessionId: SessionId): Tokens {
        val accessToken = generateAccessToken(
            principal = accountData,
            sessionId = sessionId,
            expiresAt = sessionExpiryService.getExpiryForAccessToken(accountData),
        )
        val refreshToken = generateAccessToken(
            principal = accountData,
            sessionId = sessionId,
            expiresAt = sessionExpiryService.getExpiryForRefreshToken(accountData),
        )
        return Tokens(accessToken, refreshToken)
    }


    private fun generateAccessToken(
        principal: AccountData,
        sessionId: SessionId,
        expiresAt: Instant
    ): AccessToken {
        val jwtBuilder = Jwts
            .builder()
            .subject(sessionId.value)
            .issuedAt(Date())
            .expiration(Date.from(expiresAt))
            .claim(accountId, principal.accountId.value)
            .claim(subjectId, principal.subjectId.value)
            .claim(subjectType, principal.subjectType)
            .claim(principalType, principal.accountType)

        when (principal) {
            is ActorData -> jwtBuilder.claim(actorId, principal.actorId.value)
                .claim(premisesId, principal.premisesId.value)

            is DeviceData -> jwtBuilder.claim(deviceId, principal.deviceId.value)

            is UserData -> jwtBuilder.claim(userId, principal.userId.value)
        }

        val token = jwtBuilder.signWith(getKey(), Jwts.SIG.HS256).compact()
        return AccessToken(token)
    }
}
