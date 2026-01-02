package com.robotutor.nexora.module.iam.interfaces.controller.mapper

import com.robotutor.nexora.module.iam.domain.vo.CredentialId
import com.robotutor.nexora.module.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.module.iam.interfaces.controller.view.CredentialRotatedResponse

object CredentialMapper {
    fun toCredentialRotatedResponse(pair: Pair<CredentialId, CredentialSecret>): CredentialRotatedResponse {
        return CredentialRotatedResponse(pair.first.value, pair.second.value)
    }
}