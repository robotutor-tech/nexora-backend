package com.robotutor.nexora.context.premises.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String
) : ValueObject() {
    init {
        validate()
    }

    override fun validate() {
        validation(street.isNotBlank()) { "Street must not be blank" }
        validation(city.isNotBlank()) { "City must not be blank" }
        validation(state.isNotBlank()) { "State must not be blank" }
        validation(country.isNotBlank()) { "Country must not be blank" }
        validation(postalCode.isNotBlank()) { "Postal code must not be blank" }
    }
}