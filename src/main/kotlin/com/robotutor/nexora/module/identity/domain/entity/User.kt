package com.robotutor.nexora.module.identity.domain.entity

import com.robotutor.nexora.shared.domain.Entity
import com.robotutor.nexora.shared.domain.vo.UserId

data class User(val userId: UserId) : Entity<User, UserId>(userId)
