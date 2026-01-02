package com.robotutor.nexora.module.zone.application.command

import com.robotutor.nexora.module.device.domain.vo.ModelNo
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ZoneId

data class CreateWidgetsCommand(
    val zoneId: ZoneId,
    val premisesId: PremisesId,
    val modelNo: ModelNo,
    val feedIds: List<FeedId>
) : Command
