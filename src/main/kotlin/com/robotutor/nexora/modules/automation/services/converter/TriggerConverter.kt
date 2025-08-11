package com.robotutor.nexora.modules.automation.services.converter

import com.robotutor.nexora.modules.automation.controllers.views.ScheduleTriggerRequest
import com.robotutor.nexora.modules.automation.exceptions.NexoraError
import com.robotutor.nexora.modules.automation.models.*
import com.robotutor.nexora.modules.automation.models.documents.TriggerDocument
import com.robotutor.nexora.shared.logger.serializer.DefaultSerializer
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.utils.toMap
import com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions.BadDataException
import com.robotutor.nexora.shared.adapters.outbound.webclient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TriggerConverter {
    fun toTrigger(triggerDocument: TriggerDocument): Mono<Trigger> {
        return toTriggerConfig(triggerDocument.config)
            .map {
                Trigger(
                    id = triggerDocument.id,
                    triggerId = triggerDocument.triggerId,
                    premisesId = triggerDocument.premisesId,
                    name = triggerDocument.name,
                    description = triggerDocument.description,
                    config = it,
                    createdOn = triggerDocument.createdOn,
                    updatedOn = triggerDocument.updatedOn,
                    version = triggerDocument.version,
                )
            }
    }

    fun toTriggerDocument(trigger: Trigger): TriggerDocument {
        return TriggerDocument(
            id = trigger.id,
            triggerId = trigger.triggerId,
            premisesId = trigger.premisesId,
            name = trigger.name,
            description = trigger.description,
            config = trigger.config.toMap(),
            createdOn = trigger.createdOn,
            updatedOn = trigger.updatedOn,
            version = trigger.version
        )
    }

    fun toTriggerConfig(configMap: Map<String, Any?>): Mono<TriggerConfig> {
        val errorCode = NexoraError.NEXORA0307.errorCode
        try {
            val type = TriggerType.valueOf(configMap["type"] as String)
            val config = DefaultSerializer.serialize(configMap)
            return when (type) {
                TriggerType.SCHEDULE -> getScheduleTriggerConfig(config, errorCode)
                TriggerType.VOICE -> getVoiceTriggerConfig(config, errorCode)
                TriggerType.FEED -> getFeedTriggerConfig(config, errorCode)
            }
                .map { it as TriggerConfig }
        } catch (_: Exception) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config type")))
        }
    }

    private fun getFeedTriggerConfig(config: String, errorCode: String): Mono<FeedTriggerConfig> {
        return try {
            createMono(DefaultSerializer.deserialize(config, FeedTriggerConfig::class.java))
        } catch (_: Exception) {
            createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config for FEED trigger type")))
        }
    }

    private fun getVoiceTriggerConfig(config: String, errorCode: String): Mono<VoiceTriggerConfig> {
        return try {
            createMono(DefaultSerializer.deserialize(config, VoiceTriggerConfig::class.java))
        } catch (_: Exception) {
            createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config for VOICE trigger type")))
        }
    }

    private fun getScheduleTriggerConfig(config: String, errorCode: String): Mono<ScheduleTriggerConfig> {
        try {
            val scheduleTriggerRequest = DefaultSerializer.deserialize(config, ScheduleTriggerRequest::class.java)
            val type = ScheduleType.valueOf(scheduleTriggerRequest.config["type"] as String)
            val scheduleConfig = DefaultSerializer.serialize(scheduleTriggerRequest.config)
            val scheduleTriggerConfig = when (type) {
                ScheduleType.TIME -> DefaultSerializer.deserialize(scheduleConfig, TimeTriggerConfig::class.java)
                ScheduleType.SUN -> DefaultSerializer.deserialize(scheduleConfig, SunTriggerConfig::class.java)
            }
            return createMono(
                ScheduleTriggerConfig(config = scheduleTriggerConfig, repeat = scheduleTriggerRequest.repeat.sorted())
            )
        } catch (_: Exception) {
            return createMonoError(
                BadDataException(ErrorResponse(errorCode, "Invalid config for SCHEDULE trigger type"))
            )
        }
    }
}