package com.robotutor.nexora.context.iam.infrastructure.seed

import com.robotutor.nexora.context.iam.application.seed.PermissionSeedProvider
import com.robotutor.nexora.context.iam.domain.aggregate.RoleType
import com.robotutor.nexora.context.iam.domain.vo.Permission
import com.robotutor.nexora.context.iam.domain.vo.ResourceSelector
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceType
import org.springframework.stereotype.Service

@Service
class PermissionSeedProviderImpl : PermissionSeedProvider {
    override fun getDefaultPermissions(roleType: RoleType): List<Permission> {
        return when (roleType) {
            RoleType.FULL_ACCESS -> listOf(
                Permission(ActionType.READ, ResourceType.AUTOMATION, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.AUTOMATION_RULE, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.DEVICE, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.FEED, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.INVITATION, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.PREMISES, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.WIDGET, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.ZONE, ResourceSelector.All),
                Permission(ActionType.CONTROL, ResourceType.FEED, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.AUTOMATION, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.AUTOMATION_RULE, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.DEVICE, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.FEED, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.INVITATION, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.PREMISES, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.WIDGET, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.ZONE, ResourceSelector.All),
                Permission(ActionType.DELETE, ResourceType.AUTOMATION, ResourceSelector.All),
                Permission(ActionType.DELETE, ResourceType.AUTOMATION_RULE, ResourceSelector.All),
                Permission(ActionType.DELETE, ResourceType.DEVICE, ResourceSelector.All),
                Permission(ActionType.DELETE, ResourceType.FEED, ResourceSelector.All),
                Permission(ActionType.DELETE, ResourceType.INVITATION, ResourceSelector.All),
                Permission(ActionType.DELETE, ResourceType.PREMISES, ResourceSelector.All),
                Permission(ActionType.DELETE, ResourceType.WIDGET, ResourceSelector.All),
                Permission(ActionType.DELETE, ResourceType.ZONE, ResourceSelector.All),
                )

            RoleType.FULL_WRITE -> listOf(
                Permission(ActionType.READ, ResourceType.AUTOMATION, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.AUTOMATION_RULE, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.DEVICE, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.FEED, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.INVITATION, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.PREMISES, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.WIDGET, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.ZONE, ResourceSelector.All),
                Permission(ActionType.CONTROL, ResourceType.FEED, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.AUTOMATION, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.AUTOMATION_RULE, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.DEVICE, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.FEED, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.PREMISES, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.WIDGET, ResourceSelector.All),
                Permission(ActionType.WRITE, ResourceType.ZONE, ResourceSelector.All),
            )

            RoleType.FULL_READ -> listOf(
                Permission(ActionType.READ, ResourceType.AUTOMATION, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.AUTOMATION_RULE, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.DEVICE, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.FEED, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.INVITATION, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.PREMISES, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.WIDGET, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.ZONE, ResourceSelector.All),
                Permission(ActionType.CONTROL, ResourceType.FEED, ResourceSelector.All),
            )

            RoleType.CONTROL_ONLY -> listOf(
                Permission(ActionType.READ, ResourceType.DEVICE, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.FEED, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.PREMISES, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.WIDGET, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.ZONE, ResourceSelector.All),
                Permission(ActionType.CONTROL, ResourceType.FEED, ResourceSelector.All),
            )

            RoleType.READ_ONLY -> listOf(
                Permission(ActionType.READ, ResourceType.PREMISES, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.DEVICE, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.WIDGET, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.ZONE, ResourceSelector.All),
                Permission(ActionType.READ, ResourceType.FEED, ResourceSelector.All),
            )

            RoleType.CUSTOM -> emptyList()
        }
    }
}