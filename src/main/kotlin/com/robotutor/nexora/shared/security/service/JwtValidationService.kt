package com.robotutor.nexora.shared.security.service

import com.robotutor.nexora.module.identity.domain.vo.SessionData
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.vo.*
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.crypto.SecretKey

@Service
open class JwtValidationService {
    @Value("\${app.security.jwt.secret}")
    protected lateinit var secret: String

    protected val accountId = "accountId"
    protected val subjectId = "subjectId"
    protected val accountType = "accountType"
    protected val principalId = "principalId"
    protected val principalType = "principalType"
    protected val actorId = "actorId"
    protected val premisesId = "premisesId"
    protected val deviceId = "deviceId"
    protected val userId = "userId"

    fun getSession(token: AccessToken): SessionData {
        val claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token.value).payload

        val principalType = PrincipalType.valueOf(claims[principalType] as String)

        val accountData = when (principalType) {
            PrincipalType.ACCOUNT -> resolveAccount(claims)

            PrincipalType.ACTOR -> ActorData(
                actorId = ActorId(claims[actorId] as String),
                premisesId = PremisesId(claims[premisesId] as String),
                accountData = resolveAccount(claims)
            )
        }

        return SessionData(
            sessionId = SessionId(claims.subject),
            issuedAt = claims.issuedAt.toInstant(),
            expiresAt = claims.expiration.toInstant(),
            principalData = accountData,
        )
    }

    private fun resolveAccount(claims: Claims): AccountData {
        val accountType = AccountType.valueOf(claims[accountType] as String)
        val accountId = AccountId(claims[accountId] as String)
        return when (accountType) {
            AccountType.DEVICE -> DeviceData(
                deviceId = DeviceId(claims[deviceId] as String),
                accountId = accountId,
            )

            AccountType.USER -> UserData(
                userId = UserId(claims[userId] as String),
                accountId = accountId,
            )
        }
    }

    fun validate(token: String): PrincipalData {
        try {
            return getSession(AccessToken(token)).principalData
        } catch (_: Throwable) {
            throw UnAuthorizedException(SharedNexoraError.NEXORA0101)
        }
    }

    protected fun getKey(): SecretKey {
        return Keys.hmacShaKeyFor(secret.padStart(48, '0').toByteArray())
    }
}
