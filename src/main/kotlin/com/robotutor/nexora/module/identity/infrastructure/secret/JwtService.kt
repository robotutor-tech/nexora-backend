package com.robotutor.nexora.module.identity.infrastructure.secret

import com.robotutor.nexora.module.identity.domain.service.SessionExpiryService
import com.robotutor.nexora.module.identity.domain.service.TokenGenerator
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.domain.vo.AccessToken
import com.robotutor.nexora.shared.domain.vo.Tokens
import com.robotutor.nexora.shared.domain.vo.PrincipalData
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
    override fun generateTokens(principalData: PrincipalData, sessionId: SessionId): Tokens {
        val accessToken = generateAccessToken(
            principal = principalData,
            sessionId = sessionId,
            expiresAt = sessionExpiryService.getExpiryForAccessToken(principalData),
        )
        val refreshToken = generateAccessToken(
            principal = principalData,
            sessionId = sessionId,
            expiresAt = sessionExpiryService.getExpiryForRefreshToken(principalData),
        )
        return Tokens(accessToken, refreshToken)
    }


    private fun generateAccessToken(
        principal: PrincipalData,
        sessionId: SessionId,
        expiresAt: Instant
    ): AccessToken {
        val jwtBuilder = Jwts
            .builder()
            .subject(sessionId.value)
            .issuedAt(Date())
            .expiration(Date.from(expiresAt))
            .claim(principalId, principal.principalId.value)
            .claim(principalType, principal.principalType)

        when (principal) {
            is ActorData -> jwtBuilder.claim(actorId, principal.actorId.value)
                .claim(premisesId, principal.premisesId.value)
                .claim(accountId, principal.accountData.accountId.value)
                .claim(accountType, principal.accountData.principalType)

            is DeviceData -> jwtBuilder.claim(deviceId, principal.deviceId.value)
                .claim(accountId, principal.accountId.value)
                .claim(accountType, principal.principalType)

            is UserData -> jwtBuilder.claim(userId, principal.userId.value)
                .claim(accountId, principal.accountId.value)
                .claim(accountType, principal.principalType)
        }

        val token = jwtBuilder.signWith(getKey(), Jwts.SIG.HS256).compact()
        return AccessToken(token)
    }
}
