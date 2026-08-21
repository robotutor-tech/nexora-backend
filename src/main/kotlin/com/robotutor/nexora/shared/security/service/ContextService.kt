package com.robotutor.nexora.shared.security.service

import com.robotutor.nexora.shared.application.serialization.DefaultSerializer
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date

@Service
class ContextService : JwtValidationService() {

    fun generateContext(principal: PrincipalData): String {
        return Jwts
            .builder()
            .subject(DefaultSerializer.serialize(principal))
            .issuedAt(Date())
            .expiration(Date.from(Instant.now().plusSeconds(3600)))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun getPrincipalData(token: String?): PrincipalData? {
        if (token == null) return null
        val claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).payload
        return DefaultSerializer.deserialize(claims.subject, PrincipalData::class.java)
    }
}
