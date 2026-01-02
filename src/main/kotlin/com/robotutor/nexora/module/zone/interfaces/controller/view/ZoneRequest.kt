package com.robotutor.nexora.module.zone.interfaces.controller.view

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ZoneRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 4, max = 30, message = "Name should not be less than 4 char or more than 30 char")
    val name: String
)
