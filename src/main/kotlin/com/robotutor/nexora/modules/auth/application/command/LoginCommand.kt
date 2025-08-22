package com.robotutor.nexora.modules.auth.application.command

import com.robotutor.nexora.modules.auth.domain.model.Password
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.UserData

data class LoginCommand(val email: Email, val password: Password)
data class ActorLoginCommand(val actorId: ActorId, val roleId: RoleId, val userData: UserData, val token: String)
