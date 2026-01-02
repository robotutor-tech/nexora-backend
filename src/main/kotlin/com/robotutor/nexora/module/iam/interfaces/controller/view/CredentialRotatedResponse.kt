package com.robotutor.nexora.module.iam.interfaces.controller.view

data class CredentialRotatedResponse(
    val credentialId: String,
    val secret: String,
)