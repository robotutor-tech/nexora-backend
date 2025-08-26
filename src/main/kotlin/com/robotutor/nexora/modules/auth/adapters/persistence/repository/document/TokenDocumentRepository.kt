package com.robotutor.nexora.modules.auth.adapters.persistence.repository.document

import com.robotutor.nexora.modules.auth.adapters.persistence.mapper.TokenDocumentMapper
import com.robotutor.nexora.modules.auth.adapters.persistence.model.TokenDocument
import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.shared.adapters.persistence.repository.BaseDocumentRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Component

@Component
class TokenDocumentRepository(
    mongoTemplate: ReactiveMongoTemplate,
    mapper: TokenDocumentMapper
) : BaseDocumentRepository<Token, TokenDocument>(mongoTemplate, TokenDocument::class.java, mapper)