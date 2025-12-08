package com.robotutor.nexora.context.premises.interfaces.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddressRequest(
    @field:NotBlank(message = "Street is required")
    @field:Size(min = 2, max = 50, message = "Street should not be less than 2 char or more than 50 char")
    val street: String,
    @field:NotBlank(message = "City is required")
    @field:Size(min = 2, max = 50, message = "City should not be less than 2 char or more than 50 char")
    val city: String,
    @field:NotBlank(message = "State is required")
    @field:Size(min = 2, max = 50, message = "State should not be less than 2 char or more than 50 char")
    val state: String,
    @field:NotBlank(message = "Country is required")
    @field:Size(min = 2, max = 50, message = "Country should not be less than 2 char or more than 50 char")
    val country: String,
    @field:NotBlank(message = "Postal code is required")
    @field:Size(min = 2, max = 20, message = "Postal code should not be less than 2 char or more than 20 char")
    val postalCode: String
)

data class PremisesCreateRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 30, message = "Name should not be less than 2 char or more than 30 char")
    val name: String,
    val address: AddressRequest
)
