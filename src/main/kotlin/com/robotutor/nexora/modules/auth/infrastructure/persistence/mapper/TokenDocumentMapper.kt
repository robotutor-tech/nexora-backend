package com.robotutor.nexora.modules.auth.infrastructure.persistence.mapper


import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.domain.model.TokenValue
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.ActorContextDocument
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.ActorPrincipalContextDocument
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.DeviceContextDocument
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.InternalContextDocument
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.InvitationContextDocument
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.PrincipalContextDocument
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.TokenDocument
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.UserContextDocument
import com.robotutor.nexora.shared.domain.model.ActorContext
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.ActorPrincipalContext
import com.robotutor.nexora.shared.domain.model.DeviceContext
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.InternalContext
import com.robotutor.nexora.shared.domain.model.InvitationContext
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.UserContext
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import org.springframework.stereotype.Service

@Service
class TokenDocumentMapper : DocumentMapper<Token, TokenDocument> {
    override fun toMongoDocument(domain: Token): TokenDocument {
        return TokenDocument(
            tokenId = domain.tokenId.value,
            tokenType = domain.tokenType,
            value = domain.value.value,
            principalType = domain.principalType,
            principal = toPrincipalContextDocument(domain.principal),
            issuedAt = domain.issuedAt,
            expiresAt = domain.expiresAt,
            otherToken = domain.otherTokenId?.value,
        )
    }

    override fun toDomainModel(document: TokenDocument): Token {
        return Token(
            tokenId = TokenId(document.tokenId),
            tokenType = document.tokenType,
            value = TokenValue(document.value),
            principalType = document.principalType,
            principal = toPrincipalContext(document.principal),
            issuedAt = document.issuedAt,
            expiresAt = document.expiresAt,
            otherTokenId = document.otherToken?.let { TokenId(it) }
        )
    }

    private fun toPrincipalContext(contextDocument: PrincipalContextDocument): PrincipalContext {
        return when (contextDocument) {
            is ActorContextDocument -> ActorContext(
                ActorId(contextDocument.actorId),
                RoleId(contextDocument.roleId),
                toActorPrincipalContext(contextDocument.context)
            )

            is DeviceContextDocument -> DeviceContext(DeviceId(contextDocument.deviceId))
            is UserContextDocument -> UserContext(UserId(contextDocument.userId))
            is InternalContextDocument -> InternalContext(contextDocument.value)
            is InvitationContextDocument -> InvitationContext(InvitationId(contextDocument.invitationId))
        }
    }

    private fun toActorPrincipalContext(context: ActorPrincipalContextDocument): ActorPrincipalContext {
        return when (context) {
            is DeviceContextDocument -> DeviceContext(DeviceId(context.deviceId))
            is UserContextDocument -> UserContext(UserId(context.userId))
        }
    }

    private fun toPrincipalContextDocument(principal: PrincipalContext): PrincipalContextDocument {
        return when (principal) {
            is ActorContext -> ActorContextDocument(
                actorId = principal.actorId.value,
                roleId = principal.roleId.value,
                context = toActorPrincipalContextDocument(principal.principalContext),
            )

            is DeviceContext -> DeviceContextDocument(principal.deviceId.value)
            is UserContext -> UserContextDocument(principal.userId.value)
            is InternalContext -> InternalContextDocument(principal.value)
            is InvitationContext -> InvitationContextDocument(principal.invitationId.value)
        }

    }

    private fun toActorPrincipalContextDocument(principalContext: ActorPrincipalContext): ActorPrincipalContextDocument {
        return when (principalContext) {
            is DeviceContext -> DeviceContextDocument(principalContext.deviceId.value)
            is UserContext -> UserContextDocument(principalContext.userId.value)
        }
    }
}