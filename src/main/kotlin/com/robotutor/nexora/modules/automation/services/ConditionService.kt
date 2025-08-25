//package com.robotutor.nexora.modules.automation.services
//
//import com.robotutor.nexora.modules.automation.controllers.views.ConditionRequest
//import com.robotutor.nexora.modules.automation.exceptions.NexoraError
//import com.robotutor.nexora.modules.automation.models.Condition
//import com.robotutor.nexora.modules.automation.models.ConditionConfig
//import com.robotutor.nexora.modules.automation.models.ConditionId
//import com.robotutor.nexora.modules.automation.models.IdType
//import com.robotutor.nexora.modules.automation.models.documents.ConditionDocument
//import com.robotutor.nexora.modules.automation.repositories.ConditionRepository
//import com.robotutor.nexora.modules.automation.services.converter.ConditionConverter
//import com.robotutor.nexora.modules.automation.services.validator.ConditionValidator
//import com.robotutor.nexora.modules.iam.services.EntitlementResource
//import com.robotutor.nexora.shared.audit.model.auditOnSuccess
//import com.robotutor.nexora.shared.adapters.messaging.services.KafkaPublisher
//import com.robotutor.nexora.shared.logger.Logger
//import com.robotutor.nexora.shared.logger.logOnError
//import com.robotutor.nexora.shared.logger.logOnSuccess
//import com.robotutor.nexora.common.security.createMono
//import com.robotutor.nexora.common.security.createMonoError
//import com.robotutor.nexora.common.security.application.annotations.ResourceType
//import com.robotutor.nexora.common.security.models.PremisesActorData
//import com.robotutor.nexora.common.security.service.IdGeneratorService
//import com.robotutor.nexora.shared.adapters.webclient.exceptions.BadDataException
//import com.robotutor.nexora.shared.adapters.webclient.exceptions.DuplicateDataException
//import com.robotutor.nexora.shared.adapters.webclient.exceptions.ErrorResponse
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//
//@Service
//class ConditionService(
//    private val idGeneratorService: IdGeneratorService,
//    private val conditionRepository: ConditionRepository,
//    private val kafkaPublisher: KafkaPublisher,
//    private val conditionValidator: ConditionValidator,
//    private val conditionConverter: ConditionConverter
//) {
//
//    val logger = Logger(this::class.java)
//
//    fun getConditionByConditionId(conditionId: ConditionId, premisesActorData: PremisesActorData): Mono<Condition> {
//        return conditionRepository.findByConditionIdAndPremisesId(conditionId, premisesActorData.premisesId)
//            .flatMap { conditionConverter.toCondition(it) }
//            .switchIfEmpty(
//                createMonoError(BadDataException(NexoraError.NEXORA0306))
//            )
//    }
//
//    fun createCondition(request: ConditionRequest, premisesActorData: PremisesActorData): Mono<Condition> {
//        return conditionConverter.toConditionConfig(request.config)
//            .flatMap { conditionValidator.validateRequest(it) }
//            .flatMap { config ->
//                conditionRepository.findByPremisesIdAndConfig(premisesActorData.premisesId, request.config)
//                    .flatMap { condition ->
//                        val error = NexoraError.NEXORA0312
//                        val errorResponse = ErrorResponse(
//                            error.errorCode,
//                            error.message + " with conditionId: ${condition.conditionId}",
//                        )
//                        createMonoError<ConditionConfig>(DuplicateDataException(errorResponse))
//                    }
//                    .switchIfEmpty(createMono(config))
//            }
//            .flatMap { config ->
//                idGeneratorService.generateId(IdType.CONDITION_ID)
//                    .map { conditionId -> ConditionDocument.from(conditionId, config, request, premisesActorData) }
//            }
//            .flatMap {
//                conditionRepository.save(it)
//                    .auditOnSuccess(
//                        "AUTOMATION_CONDITION_CREATED",
//                        mapOf("conditionId" to it.conditionId, "name" to it.name)
//                    )
//            }
//            .flatMap { conditionConverter.toCondition(it) }
//            .flatMap { condition ->
//                val entitlementResource = EntitlementResource(ResourceType.AUTOMATION_CONDITION, condition.conditionId)
//                kafkaPublisher.publish("entitlement.create", entitlementResource) { condition }
//            }
//            .logOnSuccess(logger, "Successfully created new Condition")
//            .logOnError(logger, "", "Failed to create new Condition")
//    }
//
//    fun getConditions(conditionIds: List<ConditionId>, premisesActorData: PremisesActorData): Flux<Condition> {
//        return conditionRepository.findAllByConditionIdInAndPremisesId(conditionIds, premisesActorData.premisesId)
//            .flatMap { conditionConverter.toCondition(it) }
//    }
//
//    fun getCondition(conditionId: ConditionId, premisesActorData: PremisesActorData): Mono<Condition> {
//        return conditionRepository.findByConditionIdAndPremisesId(conditionId, premisesActorData.premisesId)
//            .flatMap { conditionConverter.toCondition(it) }
//            .switchIfEmpty(createMonoError(BadDataException(NexoraError.NEXORA0315)))
//    }
//}
