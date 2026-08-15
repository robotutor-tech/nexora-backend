package com.robotutor.nexora.module.identity.domain.vo

data class Credential(
    val credentialId: CredentialId,
    val hashedSecret: HashedSecret
)
