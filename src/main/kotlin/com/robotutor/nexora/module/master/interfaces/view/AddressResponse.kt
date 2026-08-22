package com.robotutor.nexora.module.master.interfaces.view

data class AddressResponse(
    val pinCode: String,
    val city: String,
    val district: String,
    val state: String,
    val country: String,
)
