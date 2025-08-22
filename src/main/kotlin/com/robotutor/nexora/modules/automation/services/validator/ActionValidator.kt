//package com.robotutor.nexora.modules.automation.services.validator
//
//import com.robotutor.nexora.modules.automation.exceptions.NexoraError
//import com.robotutor.nexora.modules.automation.gateways.FeedGateway
//import com.robotutor.nexora.modules.automation.models.*
//import com.robotutor.nexora.modules.automation.repositories.ActionRepository
//import com.robotutor.nexora.modules.automation.repositories.AutomationRepository
//import com.robotutor.nexora.common.security.createMono
//import com.robotutor.nexora.common.security.createMonoError
//import com.robotutor.nexora.common.security.models.PremisesActorData
//import com.robotutor.nexora.shared.adapters.webclient.exceptions.BadDataException
//import com.robotutor.nexora.shared.adapters.webclient.exceptions.ErrorResponse
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class ActionValidator(
//    private val feedGateway: FeedGateway,
//    private val automationRepository: AutomationRepository,
//    private val actionRepository: ActionRepository
//) {
//
//    fun validateActions(actionIds: List<ActionId>, premisesActorData: PremisesActorData): Mono<List<ActionId>> {
//        if (actionIds.isEmpty())
//            return createMonoError(BadDataException(NexoraError.NEXORA0303))
//
//        val uniqueIds = actionIds.toSet().toList()
//
//        return actionRepository.findAllByActionIdInAndPremisesId(uniqueIds, premisesActorData.premisesId)
//            .collectList()
//            .flatMap { actions ->
//                val missingIds = actions.map { it.actionId }.toSet() - uniqueIds
//                if (missingIds.isNotEmpty()) {
//                    createMonoError(
//                        BadDataException(
//                            ErrorResponse(
//                                NexoraError.NEXORA0304.errorCode,
//                                "Invalid action Ids: ${missingIds.joinToString(",")}"
//                            )
//                        )
//                    )
//                } else {
//                    createMono(actionIds)
//                }
//            }
//    }
//
//    fun validateRequest(config: ActionConfig, premisesActorData: PremisesActorData): Mono<ActionConfig> {
//        val errorCode = NexoraError.NEXORA0308.errorCode
//        return when (config) {
//            is WaitActionConfig -> validateWaitActionConfig(config, errorCode)
//            is FeedControlActionConfig -> validateFeedControlActionConfig(config)
//            is AutomationActionConfig -> validateAutomationTriggerConfig(config, errorCode, premisesActorData)
//        }
//            .map { it as ActionConfig }
//    }
//
//    private fun validateAutomationTriggerConfig(
//        config: AutomationActionConfig,
//        errorCode: String,
//        premisesActorData: PremisesActorData
//    ): Mono<AutomationActionConfig> {
//        return automationRepository.findByAutomationIdAndPremisesId(
//            config.automationId,
//            premisesActorData.premisesId
//        )
//            .map { config }
//            .switchIfEmpty(
//                createMonoError(
//                    BadDataException(ErrorResponse(errorCode, "Automation ID ${config.automationId} not found"))
//                )
//            )
//    }
//
//    private fun validateWaitActionConfig(config: WaitActionConfig, errorCode: String): Mono<WaitActionConfig> {
//        return if (config.duration > 0 && config.duration <= 120) {
//            createMono(config)
//        } else {
//            createMonoError(BadDataException(ErrorResponse(errorCode, "Duration must be between 0 and 120")))
//        }
//    }
//
//    private fun validateFeedControlActionConfig(config: FeedControlActionConfig): Mono<FeedControlActionConfig> {
//        return feedGateway.getFeedByFeedId(config.feedId).map { config }
//    }
//}