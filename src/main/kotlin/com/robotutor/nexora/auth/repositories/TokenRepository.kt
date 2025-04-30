package com.robotutor.nexora.auth.repositories

import com.robotutor.nexora.auth.models.InvitationId
import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.auth.models.TokenId
import com.robotutor.nexora.auth.models.TokenIdentifierType
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
interface TokenRepository : ReactiveCrudRepository<Token, TokenId> {
    fun findByValueAndExpiresOnGreaterThan(token: String, now: LocalDateTime): Mono<Token>
    fun findByMetadata_IdentifierAndMetadata_IdentifierTypeAndExpiresOnLessThan(
        invitationId: InvitationId,
        invitation: TokenIdentifierType,
        now: LocalDateTime
    ): Mono<Token>

}
