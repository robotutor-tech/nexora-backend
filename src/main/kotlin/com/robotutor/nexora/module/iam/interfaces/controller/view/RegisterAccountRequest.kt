package com.robotutor.nexora.module.iam.interfaces.controller.view

import com.robotutor.nexora.module.iam.domain.vo.CredentialKind
import com.robotutor.nexora.shared.domain.vo.principal.AccountType
import jakarta.validation.constraints.NotBlank

data class RegisterAccountRequest(
    @field:NotBlank(message = "CredentialId should be valid")
    val credentialId: String,
    @field:NotBlank(message = "Secret is required")
    val secret: String,
    val kind: CredentialKind,
    val type: AccountType,
    val principalId: String
)