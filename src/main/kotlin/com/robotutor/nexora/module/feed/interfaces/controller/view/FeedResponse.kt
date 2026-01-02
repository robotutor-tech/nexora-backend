package com.robotutor.nexora.module.feed.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class RegisterFeedsRequest(
    @field:NotBlank(message = "FeedId should be valid")
    val deviceId: String,
    @field:NotBlank(message = "ModelNo should be valid")
    val modelNo: String
)