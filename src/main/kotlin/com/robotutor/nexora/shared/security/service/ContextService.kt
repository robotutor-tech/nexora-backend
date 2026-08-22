package com.robotutor.nexora.shared.security.service

import com.robotutor.nexora.shared.application.serialization.DefaultSerializer
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.outbox.persistence.document.PrincipalDataDocument
import com.robotutor.nexora.shared.outbox.persistence.mapper.PrincipalDataDocumentMapper
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class ContextService : JwtValidationService() {
    fun generateContext(principal: PrincipalData): String {
        return Jwts
            .builder()
            .subject(DefaultSerializer.serialize(PrincipalDataDocumentMapper.toDocument(principal)))
            .issuedAt(Date())
            .expiration(Date.from(Instant.now().plusSeconds(3600)))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun getPrincipalData(token: String?): PrincipalData? {
        if (token == null) return null
        val claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).payload
        val document = DefaultSerializer.deserialize(claims.subject, PrincipalDataDocument::class.java)
        return PrincipalDataDocumentMapper.toDomain(document)
    }
}
