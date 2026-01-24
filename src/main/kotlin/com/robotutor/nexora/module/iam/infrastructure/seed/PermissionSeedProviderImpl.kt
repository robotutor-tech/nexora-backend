package com.robotutor.nexora.module.iam.infrastructure.seed

import com.robotutor.nexora.module.iam.application.seed.PermissionSeedProvider
import com.robotutor.nexora.module.iam.domain.aggregate.RoleType
import com.robotutor.nexora.module.iam.domain.vo.Permission
import com.robotutor.nexora.shared.domain.vo.ActionType
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import org.springframework.stereotype.Service

@Service
class PermissionSeedProviderImpl : PermissionSeedProvider {
    override fun getDefaultPermissions(roleType: RoleType, premisesId: PremisesId): List<Permission> {
        return when (roleType) {
            RoleType.FULL_ACCESS -> listOf(
                Permission(ActionType.READ, ResourceType.AUTOMATION, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.DEVICE, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.PREMISES, ResourceId(premisesId.value), premisesId),
                Permission(ActionType.READ, ResourceType.WIDGET, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.ZONE, ResourceId.ALL, premisesId),
                Permission(ActionType.CONTROL, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.UPDATE, ResourceType.AUTOMATION, ResourceId.ALL, premisesId),
                Permission(ActionType.UPDATE, ResourceType.DEVICE, ResourceId.ALL, premisesId),
                Permission(ActionType.UPDATE, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.UPDATE, ResourceType.PREMISES, ResourceId(premisesId.value), premisesId),
                Permission(ActionType.UPDATE, ResourceType.WIDGET, ResourceId.ALL, premisesId),
                Permission(ActionType.UPDATE, ResourceType.ZONE, ResourceId.ALL, premisesId),
                Permission(ActionType.CREATE, ResourceType.AUTOMATION, ResourceId.ALL, premisesId),
                Permission(ActionType.CREATE, ResourceType.DEVICE, ResourceId.ALL, premisesId),
                Permission(ActionType.CREATE, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.CREATE, ResourceType.PREMISES, ResourceId(premisesId.value), premisesId),
                Permission(ActionType.CREATE, ResourceType.WIDGET, ResourceId.ALL, premisesId),
                Permission(ActionType.CREATE, ResourceType.ZONE, ResourceId.ALL, premisesId),
                Permission(ActionType.DELETE, ResourceType.AUTOMATION, ResourceId.ALL, premisesId),
                Permission(ActionType.DELETE, ResourceType.DEVICE, ResourceId.ALL, premisesId),
                Permission(ActionType.DELETE, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.DELETE, ResourceType.PREMISES, ResourceId(premisesId.value), premisesId),
                Permission(ActionType.DELETE, ResourceType.WIDGET, ResourceId.ALL, premisesId),
                Permission(ActionType.DELETE, ResourceType.ZONE, ResourceId.ALL, premisesId),
            )

            RoleType.FULL_WRITE -> listOf(
                Permission(ActionType.READ, ResourceType.AUTOMATION, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.DEVICE, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.PREMISES, ResourceId(premisesId.value), premisesId),
                Permission(ActionType.READ, ResourceType.WIDGET, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.ZONE, ResourceId.ALL, premisesId),
                Permission(ActionType.CONTROL, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.UPDATE, ResourceType.AUTOMATION, ResourceId.ALL, premisesId),
                Permission(ActionType.UPDATE, ResourceType.DEVICE, ResourceId.ALL, premisesId),
                Permission(ActionType.UPDATE, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.UPDATE, ResourceType.PREMISES, ResourceId(premisesId.value), premisesId),
                Permission(ActionType.UPDATE, ResourceType.WIDGET, ResourceId.ALL, premisesId),
                Permission(ActionType.UPDATE, ResourceType.ZONE, ResourceId.ALL, premisesId),
                Permission(ActionType.CREATE, ResourceType.AUTOMATION, ResourceId.ALL, premisesId),
                Permission(ActionType.CREATE, ResourceType.DEVICE, ResourceId.ALL, premisesId),
                Permission(ActionType.CREATE, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.CREATE, ResourceType.PREMISES, ResourceId(premisesId.value), premisesId),
                Permission(ActionType.CREATE, ResourceType.WIDGET, ResourceId.ALL, premisesId),
                Permission(ActionType.CREATE, ResourceType.ZONE, ResourceId.ALL, premisesId),
            )

            RoleType.FULL_READ -> listOf(
                Permission(ActionType.READ, ResourceType.AUTOMATION, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.DEVICE, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.PREMISES, ResourceId(premisesId.value), premisesId),
                Permission(ActionType.READ, ResourceType.WIDGET, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.ZONE, ResourceId.ALL, premisesId),
                Permission(ActionType.CONTROL, ResourceType.FEED, ResourceId.ALL, premisesId),
            )

            RoleType.CONTROL_ONLY -> listOf(
                Permission(ActionType.READ, ResourceType.DEVICE, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.FEED, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.PREMISES, ResourceId(premisesId.value), premisesId),
                Permission(ActionType.READ, ResourceType.WIDGET, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.ZONE, ResourceId.ALL, premisesId),
                Permission(ActionType.CONTROL, ResourceType.FEED, ResourceId.ALL, premisesId),
            )

            RoleType.READ_ONLY -> listOf(
                Permission(ActionType.READ, ResourceType.PREMISES, ResourceId(premisesId.value), premisesId),
                Permission(ActionType.READ, ResourceType.DEVICE, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.WIDGET, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.ZONE, ResourceId.ALL, premisesId),
                Permission(ActionType.READ, ResourceType.FEED, ResourceId.ALL, premisesId),
            )

            RoleType.DEVICE_ACCESS -> emptyList()
            RoleType.CUSTOM -> emptyList()
        }
    }
}