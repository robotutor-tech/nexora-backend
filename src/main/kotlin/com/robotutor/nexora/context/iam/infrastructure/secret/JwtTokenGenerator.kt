package com.robotutor.nexora.context.iam.infrastructure.secret

import com.robotutor.nexora.context.iam.domain.exception.IAMError
import com.robotutor.nexora.context.iam.domain.service.TokenGenerator
import com.robotutor.nexora.context.iam.domain.vo.SessionPrincipal
import com.robotutor.nexora.context.iam.domain.vo.TokenPayload
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.application.serialization.DefaultSerializer
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtTokenGenerator : TokenGenerator {
    @Value("\${app.security.jwt.secret}")
    private lateinit var secret: String

    override fun generateAccessToken(payload: TokenPayload): TokenValue {
        val principal = DefaultSerializer.serialize(payload.sessionPrincipal)
        val token = Jwts.builder()
            .setSubject(UUID.randomUUID().toString())
            .claim("principal", principal)
            .setIssuedAt(Date())
            .setExpiration(Date.from(payload.expiresAt))
            .signWith(getKey(), SignatureAlgorithm.HS256)
            .compact()
        return TokenValue(token)
    }

    override fun validateAccessToken(tokenValue: TokenValue): TokenPayload {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(tokenValue.value)
                .body

            return TokenPayload(
                sessionPrincipal = DefaultSerializer.deserialize(
                    claims["principal"] as String,
                    SessionPrincipal::class.java
                ),
                expiresAt = claims.expiration.toInstant()
            )
        } catch (e: Exception) {
            throw UnAuthorizedException(IAMError.NEXORA0205)
        }
    }

    private fun getKey() = Keys.hmacShaKeyFor(secret.padStart(48, '0').toByteArray())
}