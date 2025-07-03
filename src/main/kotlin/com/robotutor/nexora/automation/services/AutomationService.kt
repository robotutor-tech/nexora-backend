package com.robotutor.nexora.automation.services

import com.robotutor.nexora.automation.controllers.views.AutomationRequest
import com.robotutor.nexora.automation.models.Automation
import com.robotutor.nexora.automation.models.IdType
import com.robotutor.nexora.automation.repositories.AutomationRepository
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
import reactor.core.publisher.Mono

@Service
class AutomationService(
    private val idGeneratorService: IdGeneratorService,
    private val automationRepository: AutomationRepository,
    private val triggerService: TriggerService,
    private val conditionService: ConditionService,
    private val actionService: ActionService,
    private val kafkaPublisher: KafkaPublisher
) {

    val logger = Logger(this::class.java)

    fun createAutomationRule(request: AutomationRequest, premisesActorData: PremisesActorData): Mono<Automation> {
        return triggerService.validateTriggers(request.triggers, premisesActorData)
            .flatMap {
                conditionService.validateConditionNode(request.condition, premisesActorData)
            }
            .flatMap { actionService.validateActions(request.actions, premisesActorData) }
            .flatMap { idGeneratorService.generateId(IdType.AUTOMATION_ID) }
            .map { automationId -> Automation.from(automationId, request, premisesActorData) }
            .flatMap {
                automationRepository.save(it)
                    .auditOnSuccess("AUTOMATION_CREATED", mapOf("automationId" to it.automationId, "name" to it.name))
            }
            .flatMap { automation ->
                val entitlementResource = EntitlementResource(ResourceType.AUTOMATION_RULE, automation.automationId)
                kafkaPublisher.publish("entitlement.create", entitlementResource) { automation }
            }
            .logOnSuccess(logger, "Successfully created new Automation")
            .logOnError(logger, "", "Failed to create new Automation")
    }

}
