package com.robotutor.nexora.modules.zone.interfaces.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ZoneRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 30, message = "Name should not be less than 2 char or more than 30 char")
    val name: String
)
