package com.robotutor.nexora.context.iam.domain.vo

data class PermissionOverride(val permission: Permission, val effect: PermissionEffect)

enum class PermissionEffect {
    ALLOW, DENY
}
