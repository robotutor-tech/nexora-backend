package com.robotutor.nexora.modules.automation.services.converter

import com.robotutor.nexora.modules.automation.exceptions.NexoraError
import com.robotutor.nexora.modules.automation.models.*
import com.robotutor.nexora.modules.automation.models.documents.ActionDocument
import com.robotutor.nexora.shared.logger.serializer.DefaultSerializer
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.utils.toMap
import com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions.BadDataException
import com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActionConverter {
    fun toAction(actionDocument: ActionDocument): Mono<Action> {
        return toActionConfig(actionDocument.config)
            .map {
                Action(
                    id = actionDocument.id,
                    actionId = actionDocument.actionId,
                    premisesId = actionDocument.premisesId,
                    name = actionDocument.name,
                    description = actionDocument.description,
                    config = it,
                    createdOn = actionDocument.createdOn,
                    updatedOn = actionDocument.updatedOn,
                    version = actionDocument.version,
                )
            }
    }

    fun toActionDocument(action: Action): ActionDocument {
        return ActionDocument(
            id = action.id,
            actionId = action.actionId,
            premisesId = action.premisesId,
            name = action.name,
            description = action.description,
            config = action.config.toMap(),
            createdOn = action.createdOn,
            updatedOn = action.updatedOn,
            version = action.version,
        )
    }

    fun toActionConfig(configMap: Map<String, Any?>): Mono<ActionConfig> {
        val errorCode = NexoraError.NEXORA0307.errorCode
        try {
            val type = ActionType.valueOf(configMap["type"] as String)
            val config = DefaultSerializer.serialize(configMap)
            return when (type) {
                ActionType.AUTOMATION_TRIGGER -> getAutomationTriggerConfig(config, errorCode)
                ActionType.FEED_CONTROL -> getFeedControlActionConfig(config, errorCode)
                ActionType.WAIT -> getWaitActionConfig(config, errorCode)
            }
                .map { it as ActionConfig }
        } catch (_: Exception) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config type")))
        }
    }

    private fun getAutomationTriggerConfig(config: String, errorCode: String): Mono<AutomationActionConfig> {
        return try {
            createMono(DefaultSerializer.deserialize(config, AutomationActionConfig::class.java))
        } catch (_: Exception) {
            createMonoError(
                BadDataException(ErrorResponse(errorCode, "Invalid config for AUTOMATION action type"))
            )
        }

    }

    private fun getWaitActionConfig(config: String, errorCode: String): Mono<WaitActionConfig> {
        return try {
            createMono(DefaultSerializer.deserialize(config, WaitActionConfig::class.java))
        } catch (_: Exception) {
            createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config for DELAY action type")))
        }
    }

    private fun getFeedControlActionConfig(
        config: String,
        errorCode: String,
    ): Mono<FeedControlActionConfig> {
        return try {
            createMono(DefaultSerializer.deserialize(config, FeedControlActionConfig::class.java))
        } catch (_: Exception) {
            createMonoError(
                BadDataException(ErrorResponse(errorCode, "Invalid config for FEED_CONTROL action type"))
            )
        }
    }

}