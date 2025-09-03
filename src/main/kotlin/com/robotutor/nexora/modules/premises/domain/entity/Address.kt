package com.robotutor.nexora.modules.premises.domain.entity

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String
)
