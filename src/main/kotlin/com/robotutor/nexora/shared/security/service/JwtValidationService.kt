package com.robotutor.nexora.shared.security.service

import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.module.identity.domain.vo.SessionData
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.module.user.domain.vo.UserId
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.vo.AccessToken
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.principal.*
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import javax.crypto.SecretKey

@Service
open class JwtValidationService {
    @Value("\${app.security.jwt.secret}")
    protected lateinit var secret: String

    protected val accountId = "accountId"
    protected val subjectId = "subjectId"
    protected val subjectType = "subjectType"
    protected val principalType = "principalType"
    protected val actorId = "actorId"
    protected val premisesId = "premisesId"
    protected val deviceId = "deviceId"
    protected val userId = "userId"

    fun getSession(token: AccessToken): SessionData {
        val claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token.value).payload


        val subjectType = SubjectType.valueOf(claims[subjectType] as String)
        val subjectId = SubjectId(claims[subjectId] as String)
        val accountId = AccountId(claims[accountId] as String)
        val accountType = AccountType.valueOf(claims[principalType] as String)

        val accountData = when (accountType) {
            AccountType.USER -> UserData(
                userId = UserId(claims[userId] as String),
                accountId = accountId
            )

            AccountType.DEVICE -> DeviceData(
                deviceId = DeviceId(claims[deviceId] as String),
                accountId = accountId
            )

            AccountType.ACTOR -> ActorData(
                actorId = ActorId(claims[actorId] as String),
                premisesId = PremisesId(claims[premisesId] as String),
                accountId = accountId,
                subjectId = subjectId,
                subjectType = subjectType,
            )
        }

        return SessionData(
            sessionId = SessionId(claims.subject),
            issuedAt = claims.issuedAt.toInstant(),
            expiresAt = claims.expiration.toInstant(),
            accountData = accountData,
        )
    }

    fun validate(token: String): Mono<AccountData> {
        try {
            val session = getSession(AccessToken(token))
            return createMono(session.accountData)
        } catch (_: Throwable) {
            return createMonoError(UnAuthorizedException(SharedNexoraError.NEXORA0101))
        }
    }

    protected fun getKey(): SecretKey {
        return Keys.hmacShaKeyFor(secret.padStart(48, '0').toByteArray())
    }
}
