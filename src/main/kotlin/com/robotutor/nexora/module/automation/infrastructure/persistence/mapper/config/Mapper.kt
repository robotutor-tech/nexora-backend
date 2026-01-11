package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.module.automation.domain.entity.config.Config
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.ConfigDocument

interface Mapper<C: Config, D: ConfigDocument> {
    fun toDocument(config: C): D
    fun toDomain(doc: D): C
}