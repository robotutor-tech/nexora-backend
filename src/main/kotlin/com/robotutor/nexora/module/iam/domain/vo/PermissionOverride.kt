package com.robotutor.nexora.module.iam.domain.vo

data class PermissionOverride(val permission: Permission, val effect: PermissionEffect)

enum class PermissionEffect {
    ALLOW, DENY
}
