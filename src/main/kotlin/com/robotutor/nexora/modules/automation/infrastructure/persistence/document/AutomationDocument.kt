package com.robotutor.nexora.modules.automation.infrastructure.persistence.document

import com.robotutor.nexora.modules.automation.domain.entity.AutomationState
import com.robotutor.nexora.modules.automation.domain.entity.ExecutionMode
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val AUTOMATION_COLLECTION = "automations"

@TypeAlias("Automation")
@Document(AUTOMATION_COLLECTION)
data class AutomationDocument(
    val id: String? = null,
    @Indexed(unique = true)
    val automationId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val description: String?,
    val triggers: List<String>,
    val condition: ConditionNodeDocument?,
    val actions: List<String>,
    val state: AutomationState,
    val executionMode: ExecutionMode,
    val createdOn: Instant,
    val expiresOn: Instant,
    val updatedOn: Instant,
    @Version
    val version: Long? = null
)