package com.robotutor.nexora.module.user.application.command

import com.robotutor.nexora.shared.application.command.Query
import com.robotutor.nexora.shared.domain.vo.SubjectId

data class GetUserQuery(val subjectId: SubjectId) : Query
