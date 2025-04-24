package com.robotutor.nexora.auth.controllers.views

import com.robotutor.nexora.auth.models.Invitation
import com.robotutor.nexora.auth.models.InvitationId
import com.robotutor.nexora.auth.models.Token
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.UserId
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AuthUserRequest(
    @field:NotBlank(message = "UserId is required")
    val userId: UserId,
    @field:Email(message = "Email should be valid")
    val email: String,
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+\$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    val password: String
)

data class AuthLoginRequest(
    @field:Email(message = "Email should be valid")
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class TokenRequest(
    @field:Pattern(regexp = "^\\d{8}$", message = "PremisesId should be valid")
    val premisesId: PremisesId,
)

data class DeviceRequest(
    @field:NotBlank(message = "Model No is required")
    val modelNo: String,
    @field:NotBlank(message = "Device Name is required")
    val name: String,
)

data class TokenView(val token: String) {
    companion object {
        fun from(token: Token): TokenView {
            return TokenView("Bearer ${token.value}")
        }
    }
}

data class AuthValidationView(val userId: UserId, val premisesId: PremisesId?) {
    companion object {
        fun from(token: Token): AuthValidationView {
            return AuthValidationView(userId = token.userId, premisesId = token.premisesId)
        }
    }
}

data class InvitationView(
    val invitationId: InvitationId,
    val name: String,
    val modelNo: String,
    val token: String,
) {
    companion object {
        fun from(token: Token, invitation: Invitation): InvitationView {
            return InvitationView(
                invitationId = invitation.invitationId,
                name = invitation.name,
                modelNo = invitation.modelNo,
                token = token.value,
            )
        }
    }
}