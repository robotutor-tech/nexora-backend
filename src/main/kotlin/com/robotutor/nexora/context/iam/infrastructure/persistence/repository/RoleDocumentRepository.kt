package com.robotutor.nexora.context.iam.infrastructure.persistence.repository

import com.robotutor.nexora.context.iam.infrastructure.persistence.document.RoleDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleDocumentRepository : ReactiveCrudRepository<RoleDocument, String>