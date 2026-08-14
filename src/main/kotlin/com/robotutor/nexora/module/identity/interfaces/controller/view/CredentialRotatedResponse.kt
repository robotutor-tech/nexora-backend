package com.robotutor.nexora.module.identity.interfaces.controller.view

data class CredentialRotatedResponse(
    val credentialId: String,
    val secret: String,
)
