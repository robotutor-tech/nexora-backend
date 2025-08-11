package com.robotutor.nexora.modules.automation.models.documents

import com.robotutor.nexora.modules.automation.controllers.views.AutomationRequest
import com.robotutor.nexora.modules.automation.models.*
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.utils.toMap
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
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val automationId: AutomationId,
    @Indexed
    val premisesId: PremisesId,
    val name: String,
    val description: String? = null,
    val triggers: List<TriggerId>,
    val condition: Map<String, Any?>? = null,
    val actions: List<ActionId>,
    val state: AutomationState = AutomationState.ACTIVE,
    val executionMode: ExecutionMode = ExecutionMode.MULTIPLE,
    val createdOn: Instant = Instant.now(),
    val expiresOn: Instant = Instant.parse("9999-12-31T00:00:00.00Z"),
    val updatedOn: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    companion object {
        fun from(
            automationId: AutomationId,
            condition: ConditionNode?,
            request: AutomationRequest,
            premisesActorData: PremisesActorData
        ): AutomationDocument {
            return AutomationDocument(
                automationId = automationId,
                premisesId = premisesActorData.premisesId,
                name = request.name,
                description = request.description,
                triggers = request.triggers.toSet().toList(),
                condition = condition?.toMap(),
                actions = request.actions,
                executionMode = request.executionMode ?: ExecutionMode.MULTIPLE,
            )
        }
    }
}

