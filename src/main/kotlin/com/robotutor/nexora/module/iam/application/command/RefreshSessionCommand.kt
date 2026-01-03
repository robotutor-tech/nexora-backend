package com.robotutor.nexora.module.iam.application.command

import com.robotutor.nexora.module.iam.domain.vo.HashedTokenValue

data class RefreshSessionCommand(val token: HashedTokenValue)
