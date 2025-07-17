package com.robotutor.nexora.automation.services

import com.robotutor.nexora.automation.controllers.views.AutomationRequest
import com.robotutor.nexora.automation.models.Automation
import com.robotutor.nexora.automation.models.AutomationId
import com.robotutor.nexora.automation.models.IdType
import com.robotutor.nexora.automation.models.documents.AutomationDocument
import com.robotutor.nexora.automation.repositories.AutomationRepository
import com.robotutor.nexora.automation.services.converter.AutomationConverter
import com.robotutor.nexora.automation.services.validator.AutomationValidator
import com.robotutor.nexora.iam.services.EntitlementResource
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.kafka.services.KafkaPublisher
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AutomationService(
    private val idGeneratorService: IdGeneratorService,
    private val automationRepository: AutomationRepository,
    private val kafkaPublisher: KafkaPublisher,
    private val automationConverter: AutomationConverter,
    private val automationValidator: AutomationValidator
) {

    val logger = Logger(this::class.java)

    fun createAutomationRule(request: AutomationRequest, premisesActorData: PremisesActorData): Mono<Automation> {
        val condition = request.condition?.let { automationConverter.getConditionNode(request.condition) }
        return automationValidator.validateRequest(request, condition, premisesActorData)
            .flatMap { idGeneratorService.generateId(IdType.AUTOMATION_ID) }
            .map { automationId -> AutomationDocument.from(automationId, condition, request, premisesActorData) }
            .flatMap {
                automationRepository.save(it)
                    .auditOnSuccess("AUTOMATION_CREATED", mapOf("automationId" to it.automationId, "name" to it.name))
            }
            .flatMap { automationConverter.toAutomation(it) }
            .flatMap { automation ->
                val entitlementResource = EntitlementResource(ResourceType.AUTOMATION_RULE, automation.automationId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { automation }
            }
            .logOnSuccess(logger, "Successfully created new Automation")
            .logOnError(logger, "", "Failed to create new Automation")
    }

    fun getAutomationRules(automationIds: List<AutomationId>, premisesActorData: PremisesActorData): Flux<Automation> {
        return automationRepository.findAllByAutomationIdInAndPremisesId(automationIds, premisesActorData.premisesId)
            .flatMap { automationConverter.toAutomation(it) }
    }

}
