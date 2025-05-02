package com.robotutor.nexora.auth.repositories

import com.robotutor.nexora.auth.models.InvitationId
import com.robotutor.nexora.auth.models.UserInvitation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInvitationRepository : ReactiveMongoRepository<UserInvitation, InvitationId> {
}
