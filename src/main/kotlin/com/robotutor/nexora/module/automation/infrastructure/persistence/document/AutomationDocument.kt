package com.robotutor.nexora.module.automation.infrastructure.persistence.document

import com.robotutor.nexora.common.persistence.document.MongoDocument
import com.robotutor.nexora.module.automation.domain.aggregate.AutomationAggregate
import com.robotutor.nexora.module.automation.domain.aggregate.AutomationState
import com.robotutor.nexora.module.automation.domain.aggregate.ExecutionMode
import com.robotutor.nexora.module.automation.infrastructure.persistence.document.component.ComponentDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val AUTOMATION_COLLECTION = "automations"

@TypeAlias("Automation")
@Document(AUTOMATION_COLLECTION)
data class AutomationDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val automationId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val description: String?,
    val triggers: List<ComponentDocument>,
    val condition: ConditionDocument?,
    val actions: List<ComponentDocument>,
    val state: AutomationState,
    val executionMode: ExecutionMode,
    val createdOn: Instant,
    val expiresOn: Instant,
    val updatedOn: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<AutomationAggregate>