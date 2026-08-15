package com.robotutor.nexora.module.identity.domain.service

import com.robotutor.nexora.module.identity.domain.vo.SessionData
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.domain.vo.AccessToken
import com.robotutor.nexora.shared.domain.vo.Tokens
import com.robotutor.nexora.shared.domain.vo.PrincipalData

interface TokenGenerator {
    fun generateTokens(principalData: PrincipalData, sessionId: SessionId): Tokens
    fun getSession(token: AccessToken): SessionData
}
