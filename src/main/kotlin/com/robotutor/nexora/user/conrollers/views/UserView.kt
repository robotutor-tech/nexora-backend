package com.robotutor.nexora.user.conrollers.views

import com.robotutor.nexora.user.models.UserDetails
import com.robotutor.nexora.utils.models.UserId
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class UserRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 30, message = "Name should not be less than 2 char or more than 30 char")
    val name: String,
    @field:Email(message = "Email should be valid")
    val email: String,
)

data class UserView(
    val userId: UserId,
    val name: String,
    val email: String,
    val registeredAt: LocalDateTime
) {
    companion object {
        fun from(userDetails: UserDetails): UserView {
            return UserView(
                userId = userDetails.userId,
                name = userDetails.name,
                email = userDetails.email,
                registeredAt = userDetails.registeredAt
            )
        }
    }
}
