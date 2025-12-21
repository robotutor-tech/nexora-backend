package com.robotutor.nexora.context.iam.interfaces.controller.view

data class CredentialRotatedResponse(
    val credentialId: String,
    val secret: String,
)