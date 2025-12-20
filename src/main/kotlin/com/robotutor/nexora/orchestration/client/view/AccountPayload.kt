package com.robotutor.nexora.orchestration.client.view

data class AccountPayload(
    val credentialId: String,
    val secret: String,
    val kind: String,
    val type: String,
)
