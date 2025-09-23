package com.robotutor.nexora.modules.feed.interfaces.controller.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class FeedValueRequest(
    @field:Max(100, message = "Value should be less than 100")
    @field:Min(0, message = "Value should be greater than 0")
    val value: Int,
)