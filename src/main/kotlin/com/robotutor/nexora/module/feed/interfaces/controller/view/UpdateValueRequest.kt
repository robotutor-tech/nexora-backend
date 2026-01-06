package com.robotutor.nexora.module.feed.interfaces.controller.view

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class UpdateValueRequest(
    @field:Min(0, message = "Value should be between 0 and 100")
    @field:Max(100, message = "Value should be between 0 and 100")
    val value: Int
)
