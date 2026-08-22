package com.robotutor.nexora.module.premises.interfaces.controller.view

data class AddressRequest(
    val street: String,
    val postalCode: String
)

data class PremisesCreateRequest(
    val name: String,
    val address: AddressRequest
)
