package com.robotutor.nexora.context.zone.domain.policy.context

import com.robotutor.nexora.shared.domain.vo.Name

data class DuplicateZoneNameContext(
    val zoneAlreadyExists: Boolean,
    val zoneName: Name
)
