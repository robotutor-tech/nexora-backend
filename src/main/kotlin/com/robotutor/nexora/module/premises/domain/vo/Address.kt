package com.robotutor.nexora.module.premises.domain.vo

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.ValueObject

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String
) : ValueObject {
    init {
        validation(street.isBlank()) { "Street must not be blank" }
        validation(city.isBlank()) { "City must not be blank" }
        validation(state.isBlank()) { "State must not be blank" }
        validation(country.isBlank()) { "Country must not be blank" }
        validation(postalCode.isBlank()) { "Postal code must not be blank" }
    }

}