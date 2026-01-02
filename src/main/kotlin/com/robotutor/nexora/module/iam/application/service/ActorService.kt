package com.robotutor.nexora.module.iam.application.service

import com.robotutor.nexora.module.iam.domain.repository.ActorRepository
import com.robotutor.nexora.module.iam.domain.repository.GroupRepository
import com.robotutor.nexora.module.iam.domain.repository.RoleRepository
import com.robotutor.nexora.module.iam.domain.vo.Permissions
import com.robotutor.nexora.shared.domain.vo.ActorId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActorService(
    private val actorRepository: ActorRepository,
    private val roleRepository: RoleRepository,
    private val groupRepository: GroupRepository,
) {
    fun getActorPermissions(actorId: ActorId): Mono<Permissions> {
        return actorRepository.findByActorId(actorId)
            .flatMap { actor ->
                groupRepository.findAllByGroupIds(actor.groupIds).collectList()
                    .flatMap { groups ->
                        val roleIds = groups.flatMap { group -> group.roleIds }.toSet().plus(actor.roleIds)
                        roleRepository.findAllByRoleIds(roleIds).collectList()
                            .map { roles ->
                                val permissions = roles.flatMap { role -> role.permissions }.toSet()
                                Permissions(
                                    premisesId = actor.premisesId,
                                    permissions = permissions,
                                    override = actor.overrides
                                )
                            }
                    }
            }
    }
}