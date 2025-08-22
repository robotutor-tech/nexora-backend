//package com.robotutor.nexora.modules.automation.services.converter
//
//import com.robotutor.nexora.modules.automation.exceptions.NexoraError
//import com.robotutor.nexora.modules.automation.models.*
//import com.robotutor.nexora.modules.automation.models.documents.ConditionDocument
//import com.robotutor.nexora.shared.logger.serializer.DefaultSerializer
//import com.robotutor.nexora.common.security.createMono
//import com.robotutor.nexora.common.security.createMonoError
//import com.robotutor.nexora.utils.toMap
//import com.robotutor.nexora.shared.adapters.webclient.exceptions.BadDataException
//import com.robotutor.nexora.shared.adapters.webclient.exceptions.ErrorResponse
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class ConditionConverter {
//    fun toCondition(conditionDocument: ConditionDocument): Mono<Condition> {
//        return toConditionConfig(conditionDocument.config)
//            .map {
//                Condition(
//                    id = conditionDocument.id,
//                    conditionId = conditionDocument.conditionId,
//                    premisesId = conditionDocument.premisesId,
//                    name = conditionDocument.name,
//                    description = conditionDocument.description,
//                    config = it,
//                    createdOn = conditionDocument.createdOn,
//                    updatedOn = conditionDocument.updatedOn,
//                    version = conditionDocument.version,
//                )
//            }
//    }
//
//    fun toConditionDocument(condition: Condition): ConditionDocument {
//        return ConditionDocument(
//            id = condition.id,
//            conditionId = condition.conditionId,
//            premisesId = condition.premisesId,
//            name = condition.name,
//            description = condition.description,
//            config = condition.config.toMap(),
//            createdOn = condition.createdOn,
//            updatedOn = condition.updatedOn,
//            version = condition.version
//        )
//    }
//
//    fun toConditionConfig(configMap: Map<String, Any?>): Mono<ConditionConfig> {
//        val errorCode = NexoraError.NEXORA0307.errorCode
//        try {
//            val type = ConditionType.valueOf(configMap["type"] as String)
//            val config = DefaultSerializer.serialize(configMap)
//            return when (type) {
//                ConditionType.FEED -> getFeedConditionConfig(config, errorCode)
//                ConditionType.TIME_RANGE -> getTimeRangeConditionConfig(config, errorCode)
//            }
//                .map { it as ConditionConfig }
//        } catch (_: Exception) {
//            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config type")))
//        }
//    }
//
//    private fun getFeedConditionConfig(config: String, errorCode: String): Mono<FeedConditionConfig> {
//        return try {
//            createMono(DefaultSerializer.deserialize(config, FeedConditionConfig::class.java))
//        } catch (_: Exception) {
//            createMonoError(
//                BadDataException(ErrorResponse(errorCode, "Invalid config for Feed condition type"))
//            )
//        }
//    }
//
//    private fun getTimeRangeConditionConfig(config: String, errorCode: String): Mono<TimeRangeConditionConfig> {
//        return try {
//            createMono(DefaultSerializer.deserialize(config, TimeRangeConditionConfig::class.java))
//        } catch (_: Exception) {
//            createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config for DELAY condition type")))
//        }
//    }
//}