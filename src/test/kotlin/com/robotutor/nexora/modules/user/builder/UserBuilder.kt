//package com.robotutor.nexora.modules.user.builder
//
//import com.robotutor.nexora.modules.user.domain.entity.User
//import com.robotutor.nexora.shared.domain.model.Email
//import com.robotutor.nexora.shared.domain.model.Mobile
//import com.robotutor.nexora.shared.domain.vo.Name
//import com.robotutor.nexora.shared.domain.model.UserId
//import java.time.Instant
//
//data class UserBuilder(
//    val userId: UserId = UserId("userId"),
//    val name: Name = Name("John"),
//    val email: Email = Email("example@email.com"),
//    val mobile: Mobile = Mobile("9012345678"),
//    val isEmailVerified: Boolean = false,
//    val isMobileVerified: Boolean = false,
//    val registeredAt: Instant = Instant.parse("2023-01-01T00:00:00Z"),
//    val version: Long = 0
//) {
//    fun build(): User {
//        return User(
//            userId = userId,
//            name = name,
//            email = email,
//            mobile = mobile,
//            isEmailVerified = isEmailVerified,
//            isMobileVerified = isMobileVerified,
//            registeredAt = registeredAt,
//            version = version
//        )
//    }
//}
