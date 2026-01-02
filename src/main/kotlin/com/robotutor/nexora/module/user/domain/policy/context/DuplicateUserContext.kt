package com.robotutor.nexora.module.user.domain.policy.context

import com.robotutor.nexora.module.user.domain.vo.Email

data class DuplicateUserContext(
    val userAlreadyExists: Boolean,
    val email: Email
)
