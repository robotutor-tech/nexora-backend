package com.robotutor.nexora.module.identity.interfaces.controller.view

import com.robotutor.nexora.module.identity.domain.vo.CredentialKind
import com.robotutor.nexora.shared.domain.vo.principal.SubjectType
import jakarta.validation.constraints.NotBlank

data class RegisterAccountRequest(
    @field:NotBlank(message = "CredentialId should be valid")
    val credentialId: String,
    @field:NotBlank(message = "Secret is required")
    val secret: String,
    val kind: CredentialKind,
    val type: SubjectType,
    val principalId: String
)
