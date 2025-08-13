package com.robotutor.nexora.modules.zone.application.command

data class CreateZoneCommand(
    val premisesId: String,
    val name: String,
    val createdBy: String,
)
