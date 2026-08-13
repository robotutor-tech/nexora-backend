package com.robotutor.nexora.shared.message.persistence.repository

import com.robotutor.nexora.shared.message.persistence.document.DLDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DLRepository: ReactiveCrudRepository<DLDocument, String>
