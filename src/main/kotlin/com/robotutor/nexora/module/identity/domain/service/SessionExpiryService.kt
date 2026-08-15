package com.robotutor.nexora.module.identity.domain.service

import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.DeviceData
import com.robotutor.nexora.shared.domain.vo.UserData
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class SessionExpiryService {

    fun getExpiryForRefreshToken(principalData: PrincipalData): Instant {
        return when (principalData) {
            is DeviceData -> Instant.now().plus(Duration.ofDays(30))
            is UserData -> Instant.now().plus(Duration.ofDays(7))
            is ActorData -> Instant.now().plus(Duration.ofDays(7))
        }
    }

    fun getExpiryForAccessToken(principalData: PrincipalData): Instant {
        return when (principalData) {
            is DeviceData -> Instant.now().plus(Duration.ofHours(1))
            is UserData -> Instant.now().plus(Duration.ofDays(1))
            is ActorData -> Instant.now().plus(Duration.ofDays(1))
        }
    }
}
