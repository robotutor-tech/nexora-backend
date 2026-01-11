package com.robotutor.nexora.module.automation.infrastructure.persistence.mapper.config

import com.robotutor.nexora.module.automation.domain.vo.component.Wait
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.config.WaitConfigDocument

object WaitDocumentMapper : Mapper<Wait, WaitConfigDocument> {
    override fun toDocument(config: Wait): WaitConfigDocument {
        return WaitConfigDocument(duration = config.duration)
    }

    override fun toDomain(doc: WaitConfigDocument): Wait {
        return Wait(duration = doc.duration)
    }
}

