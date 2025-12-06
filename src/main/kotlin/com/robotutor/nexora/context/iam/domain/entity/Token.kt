//package com.robotutor.nexora.context.iam.domain.entity
//
//import com.robotutor.nexora.context.iam.domain.event.IAMEvent
//import com.robotutor.nexora.shared.domain.AggregateRoot
//import com.robotutor.nexora.shared.domain.model.PrincipalContext
//import java.time.Instant
//import java.util.*
//
//data class Token(
//    val tokenId: TokenId,
//    val principalType: TokenPrincipalType,
//    val principal: PrincipalContext,
//    val tokenType: TokenType,
//    val value: TokenValue,
//    val issuedAt: Instant,
//    var expiresAt: Instant,
//    var otherTokenId: TokenId? = null,
//) : AggregateRoot<Token, TokenId, IAMEvent>(tokenId) {
//
//    fun updateOtherTokenId(tokenId: TokenId): Token {
//        this.otherTokenId = tokenId
//        return this
//    }
//
//    fun invalidate(): Token {
//        this.expiresAt = Instant.now().minusSeconds(100)
//        return this
//    }
//
////    companion object {
////        fun generate(
////            tokenType: TokenType,
////            expiresAt: Instant,
////            principalType: TokenPrincipalType,
////            principal: PrincipalContext
////        ): Token {
////            return Token(
////                tokenId = TokenId(UUID.randomUUID().toString()),
////                tokenType = tokenType,
////                value = TokenValue.generate(),
////                issuedAt = Instant.now(),
////                expiresAt = expiresAt,
////                principalType = principalType,
////                principal = principal,
////            )
////        }
////    }
//}
//
//data class Tokens(val token: Token, val refreshToken: Token)
//
