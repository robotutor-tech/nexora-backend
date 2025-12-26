package com.robotutor.nexora.context.feed.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class RegisterFeedsRequest(
    @field:NotBlank(message = "FeedId should be valid")
    val deviceId: String,
    @field:NotBlank(message = "ModelNo should be valid")
    val modelNo: String
)