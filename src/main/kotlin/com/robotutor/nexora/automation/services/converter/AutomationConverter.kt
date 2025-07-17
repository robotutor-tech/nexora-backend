package com.robotutor.nexora.automation.services.converter

import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.models.*
import com.robotutor.nexora.automation.models.documents.AutomationDocument
import com.robotutor.nexora.logger.serializer.DefaultSerializer
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.webClient.exceptions.BadDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AutomationConverter {
    fun toAutomation(automationDocument: AutomationDocument): Mono<Automation> {
        return createMono(
            Automation(
                id = automationDocument.id,
                automationId = automationDocument.automationId,
                premisesId = automationDocument.premisesId,
                name = automationDocument.name,
                description = automationDocument.description,
                triggers = automationDocument.triggers,
                actions = automationDocument.actions,
                condition = automationDocument.condition?.let { getConditionNode(automationDocument.condition) },
                state = automationDocument.state,
                executionMode = automationDocument.executionMode,
                createdOn = automationDocument.createdOn,
                expiresOn = automationDocument.expiresOn,
                updatedOn = automationDocument.updatedOn,
                version = automationDocument.version
            )
        )
    }

    fun getConditionNode(configMap: Map<String, Any?>): ConditionNode {
        val errorCode = NexoraError.NEXORA0307.errorCode
        try {
            val type = ConditionNodeType.valueOf(configMap["type"] as String)
            val config = DefaultSerializer.serialize(configMap)
            return when (type) {
                ConditionNodeType.NOT -> getConditionNot(config, errorCode)
                ConditionNodeType.GROUP -> getConditionGroup(config, errorCode)
                ConditionNodeType.LEAF -> getConditionLeaf(config, errorCode)
            }
        } catch (_: Exception) {
            throw BadDataException(ErrorResponse(errorCode, "Invalid condition node type"))
        }
    }

    private fun getConditionGroup(
        config: String,
        errorCode: String
    ): ConditionGroup {
        return try {
            val conditionGroupMap = DefaultSerializer.deserialize(config, ConditionGroupMap::class.java)
            ConditionGroup(conditionGroupMap.operator, conditionGroupMap.children.map { getConditionNode(it) })
        } catch (_: Exception) {
            throw BadDataException(ErrorResponse(errorCode, "Invalid condition group type"))
        }
    }

    private fun getConditionNot(config: String, errorCode: String): ConditionNot {
        return try {
            val conditionNotMap = DefaultSerializer.deserialize(config, ConditionNotMap::class.java)
            ConditionNot(getConditionNode(conditionNotMap.child))
        } catch (_: Exception) {
            throw BadDataException(ErrorResponse(errorCode, "Invalid condition not type"))
        }
    }

    private fun getConditionLeaf(config: String, errorCode: String): ConditionLeaf {
        return try {
            DefaultSerializer.deserialize(config, ConditionLeaf::class.java)
        } catch (_: Exception) {
            throw BadDataException(ErrorResponse(errorCode, "Invalid condition leaf type"))
        }
    }
}

data class ConditionGroupMap(
    val children: List<Map<String, Any>>,
    val operator: LogicalOperator,
    val type: ConditionNodeType = ConditionNodeType.GROUP
)

data class ConditionNotMap(
    val child: Map<String, Any>,
    val type: ConditionNodeType = ConditionNodeType.NOT
)
