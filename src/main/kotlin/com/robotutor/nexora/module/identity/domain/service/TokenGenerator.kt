package com.robotutor.nexora.module.identity.domain.service

import com.robotutor.nexora.module.identity.domain.vo.SessionData
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.domain.vo.AccessToken
import com.robotutor.nexora.shared.domain.vo.Tokens
import com.robotutor.nexora.shared.domain.vo.principal.AccountData

interface TokenGenerator {
    fun generateTokens(accountData: AccountData, sessionId: SessionId): Tokens
    fun getSession(token: AccessToken): SessionData
}
