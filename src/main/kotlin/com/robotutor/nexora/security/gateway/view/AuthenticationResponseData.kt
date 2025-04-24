package com.robotutor.nexora.security.gateway.view

import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.UserId

data class AuthenticationResponseData(val userId: UserId, val premisesId: PremisesId?)
