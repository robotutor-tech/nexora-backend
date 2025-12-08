package com.robotutor.nexora.orchestration.client.view

import java.time.Instant

data class AddressResponse(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String
)

data class PremisesResponse(
    val premisesId: String,
    val name: String,
    val address: AddressResponse,
    val createdAt: Instant,
    val updatedAt: Instant
)

