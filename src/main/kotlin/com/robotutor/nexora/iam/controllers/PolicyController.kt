package com.robotutor.nexora.iam.controllers

import com.robotutor.nexora.iam.services.PolicyService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/iam/policies")
class PolicyController(private val policyService: PolicyService) {

}