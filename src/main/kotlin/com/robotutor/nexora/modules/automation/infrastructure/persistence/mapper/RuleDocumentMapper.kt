package com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.automation.domain.entity.Rule
import com.robotutor.nexora.modules.automation.domain.entity.RuleId
import com.robotutor.nexora.modules.automation.infrastructure.persistence.document.RuleDocument
import com.robotutor.nexora.modules.automation.infrastructure.persistence.mapper.config.ConfigDocumentMapper
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object RuleDocumentMapper : DocumentMapper<Rule, RuleDocument> {
    override fun toMongoDocument(domain: Rule): RuleDocument {
        return RuleDocument(
            ruleId = domain.ruleId.value,
            premisesId = domain.premisesId.value,
            name = domain.name.value,
            description = domain.description,
            type = domain.type,
            config = ConfigDocumentMapper.toConfigDocument(domain.config),
            createdOn = domain.createdOn,
            updatedOn = domain.updatedOn,
            version = domain.version,
        )
    }

    override fun toDomainModel(document: RuleDocument): Rule {
        return Rule(
            ruleId = RuleId(document.ruleId),
            premisesId = PremisesId(document.premisesId),
            name = Name(document.name),
            description = document.description,
            type = document.type,
            config = ConfigDocumentMapper.toConfig(document.config),
            createdOn = document.createdOn,
            updatedOn = document.updatedOn,
            version = document.version
        )
    }
}