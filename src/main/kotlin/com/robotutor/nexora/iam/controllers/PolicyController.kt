package com.robotutor.nexora.iam.controllers

import com.robotutor.nexora.iam.controllers.view.PolicyRequest
import com.robotutor.nexora.iam.controllers.view.PolicyView
import com.robotutor.nexora.iam.services.PolicyService
import com.robotutor.nexora.iam.services.RoleService
import com.robotutor.nexora.security.models.PremisesActorData
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/policies")
class PolicyController(private val policyService: PolicyService, private val roleService: RoleService) {

    @PostMapping
    fun createPolicy(
        @RequestBody @Validated request: PolicyRequest,
        premisesActorData: PremisesActorData
    ): Mono<PolicyView> {
        return policyService.createPolicy(request, premisesActorData).map { PolicyView.from(it) }
    }

    @PostMapping("/batch")
    fun createPolicies(
        @RequestBody @Validated request: List<PolicyRequest>,
        premisesActorData: PremisesActorData
    ): Mono<List<PolicyView>> {
        return policyService.createPolicies(request, premisesActorData)
            .collectList()
            .flatMap { policies ->
                roleService.assignPolicyToCurrentActor(policies, premisesActorData)
                    .flatMap { roleService.assignPolicyToHumanRole(policies, premisesActorData).collectList() }
                    .map { policies }
            }
            .map { policies -> policies.map { policy -> PolicyView.from(policy) } }
    }
}