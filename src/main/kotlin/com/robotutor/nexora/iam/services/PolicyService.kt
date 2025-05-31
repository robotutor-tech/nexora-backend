package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.PolicyRequest
import com.robotutor.nexora.iam.exceptions.NexoraError
import com.robotutor.nexora.iam.models.IdType
import com.robotutor.nexora.iam.models.Policy
import com.robotutor.nexora.iam.models.PolicyId
import com.robotutor.nexora.iam.models.Scope
import com.robotutor.nexora.iam.repositories.PolicyRepository
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.webClient.exceptions.BadDataException
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
        if (!validatePolicyRequest(request)) {
            return createMonoError(BadDataException(NexoraError.NEXORA0202))
        }
        return idGeneratorService.generateId(IdType.POLICY_ID)
            .map { policyId -> Policy.from(policyId, request, premisesActorData) }
            .flatMap { policy ->
                policyRepository.save(policy)
                    .auditOnSuccess(
                        "POLICY_CREATED",
                        mapOf(
                            "policyId" to policy.policyId,
                            "permission" to policy.permission,
                            "identifier" to policy.identifier
                        )
                    )
            }
            .logOnSuccess(logger, "Successfully created policy")
            .logOnError(logger, "", "Failed to create policy")
    }

    fun getPolicies(policyIds: List<PolicyId>): Flux<Policy> {
        return policyRepository.findAllByPolicyIdIn(policyIds)
    }

    fun validatePolicyRequest(request: PolicyRequest): Boolean {
        return when (request.permission.scope) {
            Scope.GLOBAL -> request.identifier == null
            Scope.RESOURCE -> request.identifier != null && request.permission.resource == request.identifier.type
        }
    }
}
