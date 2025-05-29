package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.PolicyRequest
import com.robotutor.nexora.iam.models.IdType
import com.robotutor.nexora.iam.models.Policy
import com.robotutor.nexora.iam.models.PolicyId
import com.robotutor.nexora.iam.repositories.PolicyRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PolicyService(
    private val policyRepository: PolicyRepository,
    private val idGeneratorService: IdGeneratorService
) {

    private val logger = Logger(this::class.java)

    fun createPolicy(request: PolicyRequest, premisesActorData: PremisesActorData): Mono<Policy> {
        return idGeneratorService.generateId(IdType.POLICY_ID)
            .map { policyId -> Policy.from(policyId, request, premisesActorData) }
            .flatMap { policyRepository.save(it) }
            .logOnSuccess(logger, "Successfully created policy")
            .logOnError(logger, "", "Failed to create policy")
    }

    fun createPolicies(request: List<PolicyRequest>, premisesActorData: PremisesActorData): Flux<Policy> {
        return createFlux(request).flatMap { createPolicy(it, premisesActorData) }
    }

    fun getPolicies(policyIds: List<PolicyId>): Flux<Policy> {
        return policyRepository.findAllByPolicyIdIn(policyIds)
    }

}
