package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.shared.domain.vo.AccessToken

data class ValidateTokenCommand(val token: AccessToken)
