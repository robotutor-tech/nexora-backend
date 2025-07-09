package com.robotutor.nexora.automation.models

import com.robotutor.nexora.automation.controllers.views.AutomationRequest
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.PremisesActorData
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val AUTOMATION_COLLECTION = "automations"

@TypeAlias("Automation")
@Document(AUTOMATION_COLLECTION)
data class Automation(
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val automationId: AutomationId,
    @Indexed
    val premisesId: PremisesId,
    val name: String,
    val description: String? = null,
    val triggers: List<TriggerId>,
    val condition: ConditionNode? = null,
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
            request: AutomationRequest,
            premisesActorData: PremisesActorData
        ): Automation {
            return Automation(
                automationId = automationId,
                premisesId = premisesActorData.premisesId,
                name = request.name,
                description = request.description,
                triggers = request.triggers,
                condition = request.condition,
                actions = request.actions,
                executionMode = request.executionMode ?: ExecutionMode.MULTIPLE,
            )
        }
    }
}

enum class AutomationState {
    ACTIVE,
    INACTIVE,
}

enum class ExecutionMode {
    MULTIPLE,
    SINGLE,
    REPLACE
}

sealed interface ConditionNode

data class ConditionLeaf(val conditionId: ConditionId) : ConditionNode

data class ConditionGroup(val operator: LogicalOperator, val children: List<ConditionNode>) : ConditionNode

data class ConditionNot(val child: ConditionNode) : ConditionNode

enum class LogicalOperator {
    AND, OR
}

typealias AutomationId = String
