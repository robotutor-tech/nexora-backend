package com.robotutor.nexora.context.user.domain.policy.context

import com.robotutor.nexora.context.user.domain.vo.Email

data class DuplicateUserContext(
    val userAlreadyExists: Boolean,
    val email: Email
)
