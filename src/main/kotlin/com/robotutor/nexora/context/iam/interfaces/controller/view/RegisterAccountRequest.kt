package com.robotutor.nexora.context.iam.interfaces.controller.view

import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import jakarta.validation.constraints.NotBlank

data class RegisterAccountRequest(
    @field:NotBlank(message = "CredentialId should be valid")
    val credentialId: String,
    @field:NotBlank(message = "Secret is required")
    val secret: String,
    val kind: CredentialKind,
    val type: AccountType
)