package com.robotutor.nexora.module.identity.interfaces.controller.mapper

import com.robotutor.nexora.module.identity.domain.vo.CredentialId
import com.robotutor.nexora.module.identity.domain.vo.CredentialSecret
import com.robotutor.nexora.module.identity.interfaces.controller.view.CredentialRotatedResponse

object CredentialMapper {
    fun toCredentialRotatedResponse(pair: Pair<CredentialId, CredentialSecret>): CredentialRotatedResponse {
        return CredentialRotatedResponse(pair.first.value, pair.second.value)
    }
}
