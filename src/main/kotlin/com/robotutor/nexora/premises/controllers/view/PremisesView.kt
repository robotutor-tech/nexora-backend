package com.robotutor.nexora.premises.controllers.view

import com.robotutor.nexora.premises.models.Premises
import com.robotutor.nexora.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class PremisesCreateRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 30, message = "Name should not be less than 2 char or more than 30 char")
    val name: String
)

data class PremisesView(
    val premisesId: PremisesId,
    val name: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(premises: Premises): PremisesView {
            return PremisesView(
                premisesId = premises.premisesId,
                name = premises.name,
                createdAt = premises.createdAt
            )
        }
    }
}
