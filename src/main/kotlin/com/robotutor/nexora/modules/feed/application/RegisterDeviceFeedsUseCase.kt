package com.robotutor.nexora.modules.feed.application

import com.robotutor.nexora.modules.feed.domain.repository.FeedRepository
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service

@Service
class RegisterDeviceFeedsUseCase(
    private val feedRepository: FeedRepository,
    private val idGeneratorService: IdGeneratorService,
) {
    val logger = Logger(this::class.java)


}