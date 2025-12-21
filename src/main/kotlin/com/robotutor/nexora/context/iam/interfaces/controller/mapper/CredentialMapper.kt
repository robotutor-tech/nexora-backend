package com.robotutor.nexora.context.iam.interfaces.controller.mapper

import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.context.iam.interfaces.controller.view.CredentialRotatedResponse

object CredentialMapper {
    fun toCredentialRotatedResponse(pair: Pair<CredentialId, CredentialSecret>): CredentialRotatedResponse {
        return CredentialRotatedResponse(pair.first.value, pair.second.value)
    }
}