package com.robotutor.nexora.module.premises.interfaces.controller.view

import com.robotutor.nexora.module.premises.domain.aggregate.PremisesState
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
    val state: PremisesState,
    val createdAt: Instant,
    val updatedAt: Instant
)
