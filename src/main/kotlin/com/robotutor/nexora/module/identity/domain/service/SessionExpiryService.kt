package com.robotutor.nexora.module.identity.domain.service

import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.SubjectType
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class SessionExpiryService {

    fun getExpiryForRefreshToken(accountData: AccountData): Instant {
        return when (accountData.subjectType) {
            SubjectType.USER -> Instant.now().plus(Duration.ofHours(7))
            SubjectType.DEVICE -> Instant.now().plus(Duration.ofDays(30))
        }
    }

    fun getExpiryForAccessToken(accountData: AccountData): Instant {
        return when (accountData.subjectType) {
            SubjectType.USER -> Instant.now().plus(Duration.ofHours(1))
            SubjectType.DEVICE -> Instant.now().plus(Duration.ofDays(1))
        }
    }
}
